package com.company.dto;

import com.company.models.Deadline;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC,
        fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC
)
@AllArgsConstructor
@RequiredArgsConstructor
public class Schedule {

    @NonNull @Getter @Setter
    protected List<Deadline> scheduledDeadlines;

    @Getter @Setter
    protected List<Deadline> unscheduledDeadlines = new LinkedList<>();

    public Schedule() {
        scheduledDeadlines = new LinkedList<>();
    }
}
