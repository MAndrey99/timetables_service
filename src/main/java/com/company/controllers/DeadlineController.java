package com.company.controllers;

import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.company.subscription.Notifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController()
@RequestMapping("deadlines")
public class DeadlineController {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DeadlineRepository deadlineRepository;
    @Autowired private Notifier notifier;

    @PostMapping
    void postDeadline(@RequestBody String deadline) {
        try {
            Deadline dl = objectMapper.createParser(deadline).readValueAs(Deadline.class);
            deadlineRepository.save(dl);
            notifier.updateNextNotificationTime();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping
    String getDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                       @RequestParam(value = "creatorId", required = false) Long creatorId,
                       @RequestParam(defaultValue = "true") boolean relevant) throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                new Deadline.DeadlinesBucket(
                        deadlineRepository.findAll((r, cq, cb) -> {
                            cq.orderBy(cb.asc(r.get("dateTime")));
                            var res = cb.conjunction();
                            if (groupId != null) {
                                res = cb.and(res, cb.equal(r.get("groupId"), groupId));
                            }
                            if (creatorId != null) {
                                res = cb.and(res, cb.equal(r.get("creatorId"), creatorId));
                            }
                            if (relevant)
                                res = cb.and(res, cb.greaterThan(r.get("dateTime"), LocalDateTime.now()));
                            return res;
                        })
                )
        );
    }

    @DeleteMapping
    void deleteDeadline(@RequestParam(value = "id") long id) {
        deadlineRepository.deleteById(id);
    }
}
