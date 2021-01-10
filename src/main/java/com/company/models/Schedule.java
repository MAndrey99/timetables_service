package com.company.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC,
        fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC
)
public class Schedule {
    @Getter @Setter protected List<Deadline> scheduledDeadlines;
    @Getter @Setter protected List<Deadline> unscheduledDeadlines= new LinkedList<>();

    public Schedule() {
        scheduledDeadlines = new LinkedList<>();
    }

    public Schedule(List<Deadline> scheduledDeadlines) {
        this.scheduledDeadlines = scheduledDeadlines;
    }
}
