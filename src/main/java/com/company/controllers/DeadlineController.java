package com.company.controllers;

import com.company.models.Deadline;
import com.company.services.DeadlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("deadlines")
class DeadlineController {

    @Autowired
    private DeadlineService deadlineService;

    @PostMapping
    Deadline postDeadline(@RequestBody Deadline deadline) {
        return deadlineService.postDeadline(deadline);
    }

    @PatchMapping(path = "/{id}")
    Deadline patchDeadline(@PathVariable long id, @RequestBody Deadline.DeadlinePatch patch) {
        return deadlineService.patchDeadline(id, patch);
    }

    @GetMapping
    Deadline.DeadlinesBucket getDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                       @RequestParam(value = "creatorId", required = false) Long creatorId,
                       @RequestParam(defaultValue = "true") boolean relevant) {
        return deadlineService.getDeadline(groupId, creatorId, relevant);
    }

    @GetMapping("/{id}")
    Deadline getDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                       @PathVariable(value = "id") long id) {
        return deadlineService.getDeadline(groupId, id);
    }

    @DeleteMapping("/{id}")
    void deleteDeadline(@RequestParam(value = "groupId", required = false) Long groupId,
                        @PathVariable(value = "id") long id) {
        deadlineService.deleteDeadline(groupId, id);
    }
}
