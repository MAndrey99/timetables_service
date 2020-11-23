package com.company.controllers;

import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController()
@RequestMapping("deadlines")
public class DeadlineController {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DeadlineRepository deadlineRepository;

    @PostMapping
    void postDeadline(@RequestBody String deadline) {
        try {
            Deadline dl = objectMapper.createParser(deadline).readValueAs(Deadline.class);
            deadlineRepository.save(dl);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping
    String getDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                       @RequestParam(value = "creatorId", required = false) Long creatorId,
                       @RequestParam(defaultValue = "true") boolean relevant) throws JsonProcessingException {
        Deadline.DeadlinesBucket result;

        if (groupId == null) {
            if (creatorId == null)
                result = new Deadline.DeadlinesBucket(
                        StreamSupport.stream(
                                (relevant ? deadlineRepository.findAllByDateTimeAfter(LocalDateTime.now())
                                : deadlineRepository.findAll()).spliterator(),
                                false).collect(Collectors.toList())
                );
            else
                result = new Deadline.DeadlinesBucket(deadlineRepository.findByCreatorId(creatorId, relevant));
        } else {
            if (creatorId == null)
                result = new Deadline.DeadlinesBucket(deadlineRepository.findByGroupId(groupId, relevant));
            else
                result = new Deadline.DeadlinesBucket(
                        deadlineRepository.findByGroupIdAndCreatorId(groupId, creatorId, relevant)
                );
        }

        return objectMapper.writeValueAsString(result);
    }

    @DeleteMapping
    void deleteDeadline(@RequestParam(value = "id") long id) {
        deadlineRepository.deleteById(id);
    }
}
