package com.company.controllers;

import com.company.models.Schedule;
import com.company.scheduling.ScheduleAlgorithms;
import com.company.services.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("schedule")
class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    Schedule getSchedule(
            @RequestParam(value = "groupId") long groupId,
            @RequestParam(value = "algorithm", required = false) String algorithm
    ) {
        if (algorithm == null)
            algorithm = ScheduleAlgorithms.PriorityLLF.name();  // значение по-умолчанию
        return scheduleService.getSchedule(groupId, algorithm);
    }
}
