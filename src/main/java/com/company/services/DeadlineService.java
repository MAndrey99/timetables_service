package com.company.services;

import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.company.subscription.Notifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class DeadlineService {

    private final DeadlineRepository deadlineRepository;
    private final Notifier notifier;

    @Autowired
    private DeadlineService(DeadlineRepository deadlineRepository, Notifier notifier) {
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
        return deadline;
    }

    public Deadline.DeadlinesBucket getDeadline(Long groupId, Long creatorId, boolean relevant) {
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
