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
                       @RequestParam(value = "creatorId", required = false) Long creatorId) throws JsonProcessingException {
        if (groupId == null) {
            if (creatorId == null)
                return objectMapper.writeValueAsString(new Deadline.DeadlinesBucket(
                        StreamSupport.stream(deadlineRepository.findAll().spliterator(), false)
                                .collect(Collectors.toList())
                ));
            else
                return objectMapper.writeValueAsString(new Deadline.DeadlinesBucket(
                        deadlineRepository.findByCreatorId(creatorId)
                ));
        } else {
            if (creatorId == null)
                return objectMapper.writeValueAsString(new Deadline.DeadlinesBucket(
                        deadlineRepository.findByGroupId(groupId)
                ));
            else
                return objectMapper.writeValueAsString(new Deadline.DeadlinesBucket(
                        deadlineRepository.findByGroupIdAndCreatorId(groupId, creatorId)
                ));
        }
    }
}
