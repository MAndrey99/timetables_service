package com.company.scheduling;

import com.company.models.Deadline;
import com.company.dto.Schedule;
import lombok.NonNull;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.*;

public class Scheduler {

    protected List<Deadline> deadlines;
    protected Schedule schedule;
    private final ScheduleStrategy scheduleStrategy;
    private boolean scheduled = true;

    Scheduler(@NonNull ScheduleStrategy scheduleStrategy) {
        this.scheduleStrategy = scheduleStrategy;
    }

    Scheduler(@NonNull ScheduleStrategy scheduleStrategy, @NonNull List<Deadline> deadlineList) {
        this(scheduleStrategy);
        setDeadlines(deadlineList);
    }

    public static Optional<Schedule> schedule(@NonNull List<Deadline> deadlineList,
                                              @NonNull ScheduleAlgorithms algorithm)
            throws IllegalArgumentException {
        var scheduler = new Scheduler(algorithm.getStrategy(), deadlineList);
        return scheduler.getSchedule();
    }

    public void setDeadlines(@NonNull List<Deadline> deadlineList) {
        this.deadlines = deadlineList;
        scheduled = false;
    }

    public Optional<Schedule> getSchedule() {
        if (!scheduled) {
            prepare();
            var now = LocalDateTime.now();
            schedule = buildSchedule(now, deadlines.stream()
                    .map(Deadline::getDateTime)
                    .max((d1, d2) -> d1.isAfter(d2) ? 1 : 0)
                    .orElse(now)
            );
            deadlines = null;  // расписание построено, дедлайны, по которым строили хранить больше не нужно
        }
        return Optional.ofNullable(schedule);
    }

    /**
     * выполняет подготовку к построению расписания. Выполняется непосредственно перед buildSchedule.
     * Убираются дедлайны, время которых неизвестно, а оставшиеся сортируются.
     */
    protected void prepare() {
        deadlines = deadlines.stream()
                .filter((deadline -> deadline.getLeadTime() > 0))
                .sorted(scheduleStrategy.getComparator())
                .toList();
    }

    /**
     * строит расписание.
     * ожидается, что buildSchedule содержит готовое расписание если его построение возможно
     */
    protected Schedule buildSchedule(@NonNull LocalDateTime from, @NonNull LocalDateTime to) {
        var scheduleIntervalsForTransferableTasks = getScheduleIntervalsForTransferableTasks(from, to);
        var scheduleParts = new LinkedList<Schedule>();

        for (var it : scheduleIntervalsForTransferableTasks) {
            var newPart = scheduleStrategy.schedule(it.getFirst(), it.getSecond(), deadlines);
            if (newPart.isEmpty())
                continue;
            if (newPart.get().getScheduledDeadlines().size() != 0)
                scheduleParts.add(newPart.get());
            deadlines = newPart.get().getUnscheduledDeadlines();
            newPart.get().setUnscheduledDeadlines(null);
            if (deadlines.isEmpty())
                break;
        }

        var schedule = linkScheduleParts(scheduleParts);
        scheduleStrategy.schedulePostprocessing(from, to, schedule, deadlines);
        return schedule;
    }

    private Schedule linkScheduleParts(@NonNull LinkedList<Schedule> scheduleParts) {
        var res = new ArrayList<Deadline>(scheduleParts.stream().mapToInt(s -> s.getScheduledDeadlines().size()).sum());
        for (var schedule : scheduleParts)
            res.addAll(schedule.getScheduledDeadlines());
        return new Schedule(res);
    }

    /**
     * Рассчитывает промежутки времени для выполнения переносимых дедлайнов.
     * Расписание равно: дедлайны промежутка 1, непереносимый дедлайн 1, дедлайны промежутка 2, непереносимый дедлайн 2
     *
     * @return Промежутки времени, в которые необходимо вписать переносимые дедлайны.
     */
    protected List<Pair<LocalDateTime, LocalDateTime>> getScheduleIntervalsForTransferableTasks(
            @NonNull LocalDateTime from, @NonNull LocalDateTime to
    ) {
        var res = new LinkedList<Pair<LocalDateTime, LocalDateTime>>();
        var tmpBeg = from;

        for (var it : deadlines) {
            if (!it.getIsTransferable()) {
                res.add(Pair.of(tmpBeg, it.getRemainingTime()));
                tmpBeg = it.getDateTime();
            }
        }

        return res;
    }
}
