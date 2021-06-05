package com.company.scheduling;

import com.company.models.Deadline;
import com.company.models.Schedule;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.*;

class LLFcheduleStrategy implements ScheduleStrategy {
    @Override
    public Comparator<Deadline> getComparator() {
        return Comparator.comparing(Deadline::getRemainingTime);
    }

    @Override
    public Optional<Schedule> schedule(@NonNull LocalDateTime from, @NonNull LocalDateTime to, @NonNull List<Deadline> deadlines) {
        var localDt = from;
        var scheduled = new ArrayList<Deadline>();
        var unscheduled = new ArrayList<Deadline>();

        for (var it : deadlines) {
            var newDt = localDt.plusSeconds(it.getLeadTime());
            if (it.getDateTime().isBefore(newDt)) {
                unscheduled.add(it);
            } else {
                it.setDateTime(newDt);
                scheduled.add(it);
                localDt = newDt;
            }
        }

        if (scheduled.isEmpty())
            return Optional.empty();
        else {
            var res = new Schedule();
            res.setScheduledDeadlines(scheduled);
            res.setUnscheduledDeadlines(unscheduled);
            return Optional.of(res);
        }
    }

    /**
     * После выполнения основного алгоритма может возникнуть ситуация, когда некоторые задачи не удалось выполнить.
     * В таком случае необходимо попытаться заменить вошедшие в расписание дедлайны с низким приоритетом
     * на не вошедшие с высоким.
     */
    @Override
    public void schedulePostprocessing(@NonNull LocalDateTime from, @NonNull LocalDateTime to,
                                       @NonNull Schedule schedule, @NonNull List<Deadline> unscheduledDeadlines) {
        // создаём мапу приоритет-список дедлайнов
        var priorityToDeadlinesMap = new HashMap<Short, List<Deadline>>();
        for (var it : unscheduledDeadlines) {
            if (!priorityToDeadlinesMap.containsKey(it.getPriority()))
                priorityToDeadlinesMap.put(it.getPriority(), new ArrayList<>());
            priorityToDeadlinesMap.get(it.getPriority()).add(it);
        }

        // сортируем списки дедлайнов в мапе, чтобы вначале были самые короткие по времени(они в приоритете)
        for (var key : priorityToDeadlinesMap.keySet())
            priorityToDeadlinesMap.get(key).sort(Comparator.comparing(Deadline::getLeadTime));

        // TODO: пытаемся впихнуть невпихуемое
    }
}
