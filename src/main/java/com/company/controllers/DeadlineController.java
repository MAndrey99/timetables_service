package com.company.controllers;

import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.company.subscription.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController()
@RequestMapping("deadlines")
public class DeadlineController {
    @Autowired private DeadlineRepository deadlineRepository;
    @Autowired private Notifier notifier;

    @PostMapping
    Deadline postDeadline(@RequestBody Deadline deadline) {
        deadline = deadlineRepository.save(deadline);
        notifier.updateNextNotificationTime();
        return deadline;
    }

    @PatchMapping(path = "/{id}")
    Deadline patchDeadline(@PathVariable long id, @RequestBody Deadline.DeadlinePatch patch) {
        var deadline = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        deadline.applyPatch(patch);
        deadlineRepository.save(deadline);
        return deadline;
    }

    @GetMapping
    Deadline.DeadlinesBucket getDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                       @RequestParam(value = "creatorId", required = false) Long creatorId,
                       @RequestParam(defaultValue = "true") boolean relevant) {
        return new Deadline.DeadlinesBucket(
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
            );
    }

    @GetMapping("/{id}")
    Deadline getDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                       @PathVariable(value = "id") long id) {
        var d = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (groupId != null && d.getGroupId() != groupId)
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        return d;
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
}
