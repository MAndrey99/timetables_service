package com.company.scheduling;

import com.company.models.Deadline;
import com.company.models.Schedule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class Scheduler {
    protected List<Deadline> deadlines;
    protected Schedule schedule;
    private boolean scheduled = true;

    public void setDeadlines(List<Deadline> deadlineList) {
        this.deadlines = deadlineList;
        scheduled = false;
    }

    public Optional<Schedule> getSchedule() {
        if (!scheduled) {
            prepare();
            scheduled = schedule();
            if (!scheduled) {
                deadlines = null;
                scheduled = true;
                return Optional.empty();
            }
        }
        return Optional.ofNullable(schedule);
    }

    /**
     * выполняет подготовку к построению расписания. Выполняется непосредственно перед schedule.
     */
    protected void prepare() {
        // убираются дедлайны, о времени выполнения которых ни чего не сказано.
        deadlines = deadlines.stream()
                .filter((deadline -> deadline.getLeadTime() > 0))
                .collect(Collectors.toList());
    }

    /**
     * строит расписание.
     * при возвращении true ожидается, что schedule содержит готовое расписание.
     *
     * @return true если удалось построить расписание
     */
    protected abstract boolean schedule();
}
