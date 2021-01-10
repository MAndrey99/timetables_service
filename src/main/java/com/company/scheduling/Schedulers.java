package com.company.scheduling;

import com.company.models.Deadline;
import com.company.models.Schedule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public final class Schedulers {
    static Map<String, Scheduler> schedulers = new TreeMap<>();
    static {
        schedulers.put("SRTF", new SRTFScheduler());
        schedulers.put("prioritySRTF", new PrioritySRTFScheduler());
    }

    private Schedulers() {}

    public static Optional<Schedule> schedule(List<Deadline> deadlineList, String schedulerName) {
        var scheduler = schedulers.get(schedulerName);
        if (scheduler == null)
            throw new UnsupportedOperationException("алгоритм планирования " + schedulerName + " не поддерживается");

        scheduler.setDeadlines(deadlineList);
        return scheduler.getSchedule();
    }
}
