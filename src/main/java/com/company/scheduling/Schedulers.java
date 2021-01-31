package com.company.scheduling;

import com.company.models.Deadline;
import com.company.models.Schedule;

import java.util.List;
import java.util.Optional;

public final class Schedulers {
    private Schedulers() {}

    public static Optional<Schedule> schedule(List<Deadline> deadlineList, String schedulerName) {
        var scheduler = switch (schedulerName) {
            case "SRTF" -> new SRTFScheduler();
            case "prioritySRTF" -> new PrioritySRTFScheduler();
            default -> throw new UnsupportedOperationException(
                    "алгоритм планирования %s не поддерживается".formatted(schedulerName)
            );
        };
        scheduler.setDeadlines(deadlineList);
        return scheduler.getSchedule();
    }
}
