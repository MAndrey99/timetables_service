package com.company.services;

import com.company.dto.DeadlineFilter;
import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.company.subscription.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DeadlineService {
    // TODO: транзакции или lock-free

    private final DeadlineRepository deadlineRepository;
    private final Notifier notifier;

    @Autowired
    public DeadlineService(DeadlineRepository deadlineRepository, Notifier notifier) {
        this.deadlineRepository = deadlineRepository;
        this.notifier = notifier;
    }

    public Deadline postDeadline(Deadline deadline) {
        deadline = deadlineRepository.save(deadline);
        notifier.updateNextNotificationTime();
        return deadline;
    }

    public Deadline patchDeadline(long id, Deadline.DeadlinePatch patch) {
        var deadline = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        deadline.applyPatch(patch);
        deadlineRepository.save(deadline);
        notifier.updateNextNotificationTime();
        return deadline;
    }

    public Deadline.DeadlinesBucket getDeadline(Long groupId, Long creatorId, boolean relevant) {
        return new Deadline.DeadlinesBucket(
                deadlineRepository.findAllByFilter(new DeadlineFilter(groupId, creatorId, relevant)));
    }

    public Deadline getDeadline(Long groupId, long id) {
        var d = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (groupId != null && !d.getGroupId().equals(groupId))
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        return d;
    }

    public void deleteDeadline(Long groupId, long id) {
        var deadline = deadlineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
        if (groupId != null && !deadline.getGroupId().equals(groupId))
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        deadlineRepository.delete(deadline);
    }
}
