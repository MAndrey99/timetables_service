package com.company.scheduling;

import com.company.models.Deadline;

import java.util.List;
import java.util.Optional;

public interface Scheduler {
    void setDeadlines(List<Deadline> deadlineList);
    Optional<List<Deadline>> getSchedule();
}
