package com.company.controllers;

import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController()
@RequestMapping("deadlines")
public class DeadlineController {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DeadlineRepository deadlineRepository;

    @PostMapping
    void postDeadline(@RequestBody String deadline) {
        try {
            Deadline dl = objectMapper.createParser(deadline).readValueAs(Deadline.class);
            deadlineRepository.save(dl);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
