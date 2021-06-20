package com.company.services;

import com.company.dto.Schedule;
import com.company.repositories.DeadlineRepository;
import com.company.scheduling.ScheduleAlgorithms;
import com.company.scheduling.Scheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ScheduleService {

    private final DeadlineRepository deadlineRepository;

    @Autowired
    private ScheduleService(DeadlineRepository deadlineRepository) {
        this.deadlineRepository = deadlineRepository;
    }

    public Schedule getSchedule(long groupId, String algorithm) {
        var scheduleAlgorithm = algorithm == null
                ? ScheduleAlgorithms.PriorityLLF
                : ScheduleAlgorithms.valueOf(algorithm);

        try {
            log.info("строим расписание для groupId=" + groupId + " алгоритмом " + algorithm);
            var schedule = Scheduler.schedule(deadlineRepository.findAll((r, cq, cb) -> {
                cq.orderBy(cb.asc(r.get("dateTime")));
                var res = cb.conjunction();
                res = cb.and(res, cb.equal(r.get("groupId"), groupId));
                res = cb.and(res, cb.greaterThan(r.get("dateTime"), LocalDateTime.now()));
                return res;
            }), scheduleAlgorithm);
            if (schedule.isEmpty()) {
                log.info("расписание не сформировано(groupId=" + groupId + ")");
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
            } else {
                log.info("расписание успешно сформировано(groupId=" + groupId + ")");
            }
            return schedule.get();
        } catch (IllegalArgumentException e) {
            log.info("ошибка построения расписания(groupId=" + groupId + "): IllegalArgumentException");
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
