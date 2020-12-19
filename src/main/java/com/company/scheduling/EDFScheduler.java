package com.company.scheduling;

import com.company.models.Deadline;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EDFScheduler {
    private List<Deadline> deadlines;

    public void setDeadlines(List<Deadline> deadlineList) {
        deadlines = deadlineList.stream()
                .filter((deadline -> deadline.getLeadTime() != null))
                .collect(Collectors.toList());
    }

    public Optional<List<Deadline>> getSchedule() {
        return schedule() ? Optional.of(deadlines) : Optional.empty();
    }

    /**
     * Уменьшает dateTime дедлайна так, чтобы, при начинании выполнения задачи за leadTime до дедлайна,
     * все задачи были выполнены.
     *
     * Например:
     * Есть 2 дедлайна через 2 часа. На выполнение каждого нужен час.
     * Тогда один из дедлайнов будет перенесён на час раньше.
     *
     * @return true в случае успеха, false если уложиться во все дедлайны невозможно
     */
    private boolean schedule() {
        var now = OffsetDateTime.now();
        var offset = now.getOffset();
        var localDt = now.toLocalDateTime();

        deadlines.sort((a, b) -> (int) (a.getDateTime().minusSeconds(a.getLeadTime()).toEpochSecond(offset)
                - b.getDateTime().minusSeconds(b.getLeadTime()).toEpochSecond(offset)));

        for (var it : deadlines) {
            localDt = localDt.plusSeconds(it.getLeadTime());
            if (it.getDateTime().isBefore(localDt))
                return false;
            it.setDateTime(localDt);
        }

        return true;
    }
}
