package com.company.scheduling;

import com.company.models.Deadline;
import com.company.models.Schedule;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public final class Schedulers {
    private Schedulers() {}

    public static Optional<Schedule> schedule(@NonNull List<Deadline> deadlineList, @NonNull String schedulerName) {
        var strategy = switch (schedulerName) {
            case "SRTF" -> new SRTFScheduleStrategy();
            default -> throw new UnsupportedOperationException(
                    "алгоритм планирования %s не поддерживается".formatted(schedulerName)
            );
        };
        var scheduler = new Scheduler(strategy, deadlineList);
        return scheduler.getSchedule();
    }
}
