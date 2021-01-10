package com.company.controllers;

import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.company.subscription.Notifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;

@RestController()
@RequestMapping("deadlines")
public class DeadlineController {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DeadlineRepository deadlineRepository;
    @Autowired private Notifier notifier;

    @PostMapping
    String postDeadline(@RequestBody Deadline deadline) throws JsonProcessingException {
        deadline = deadlineRepository.save(deadline);
        notifier.updateNextNotificationTime();
        return objectMapper.writeValueAsString(deadline);
    }

    @PatchMapping(path = "/{id}")
    String patchDeadline(@PathVariable long id, @RequestBody Deadline.DeadlinePatch patch)
            throws JsonProcessingException {
        var deadline = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        deadline.applyPatch(patch);
        deadlineRepository.save(deadline);
        return objectMapper.writeValueAsString(deadline);
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

    @GetMapping("/{id}")
    String getDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                       @PathVariable(value = "id") long id) throws JsonProcessingException {
        var d = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (groupId != null && d.getGroupId() != groupId)
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        return objectMapper.writeValueAsString(d);
    }

    @DeleteMapping("/{id}")
    void deleteDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                        @PathVariable(value = "id") long id) {
        var deadline = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
        if (groupId != null && deadline.getGroupId() != groupId)
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        deadlineRepository.delete(deadline);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Deadline.class, new PropertyEditorSupport(objectMapper));
    }
}
