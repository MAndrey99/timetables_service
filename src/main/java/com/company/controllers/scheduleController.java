package com.company.controllers;


import com.company.models.Schedule;
import com.company.repositories.DeadlineRepository;
import com.company.scheduling.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController()
@RequestMapping("schedule")
@Slf4j
public class scheduleController {
    @Autowired private DeadlineRepository deadlineRepository;

    @GetMapping
    Schedule getSchedule(
            @RequestParam(value = "groupId") long groupId,
            @RequestParam(value = "algorithm", defaultValue = "prioritySRTF") String algorithm
    ) {
        try {
            log.info("строим расписание для groupId=" + groupId + " алгоритмом " + algorithm);
            var schedule = Schedulers.schedule(deadlineRepository.findAll((r, cq, cb) -> {
                cq.orderBy(cb.asc(r.get("dateTime")));
                var res = cb.conjunction();
                res = cb.and(res, cb.equal(r.get("groupId"), groupId));
                res = cb.and(res, cb.greaterThan(r.get("dateTime"), LocalDateTime.now()));
                return res;
            }), algorithm);
            if (schedule.isEmpty()) {
                log.info("расписание не сформировано(groupId=" + groupId + ")");
                throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
            } else {
                log.info("расписание успешно сформировано(groupId=" + groupId + ")");
            }
            return schedule.get();
        } catch (UnsupportedOperationException e) {
            log.info("ошибка построения расписания(groupId=" + groupId + "):" + e.getLocalizedMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
