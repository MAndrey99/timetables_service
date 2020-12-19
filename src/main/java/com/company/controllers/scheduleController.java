package com.company.controllers;


import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.company.scheduling.EDFScheduler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class scheduleController {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DeadlineRepository deadlineRepository;

    @GetMapping
    String getSchedule(@RequestParam(value = "groupId") long groupId) throws JsonProcessingException {
        var scheduler = new EDFScheduler();
        scheduler.setDeadlines(deadlineRepository.findAll((r, cq, cb) -> {
            cq.orderBy(cb.asc(r.get("dateTime")));
            var res = cb.conjunction();
            res = cb.and(res, cb.equal(r.get("groupId"), groupId));
            res = cb.and(res, cb.greaterThan(r.get("dateTime"), LocalDateTime.now()));
            return res;
        }));
        var schedule = scheduler.getSchedule();
        if (schedule.isEmpty())
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        return objectMapper.writeValueAsString(new Deadline.DeadlinesBucket(schedule.get()));
    }
}
