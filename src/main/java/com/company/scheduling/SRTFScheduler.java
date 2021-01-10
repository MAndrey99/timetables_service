package com.company.scheduling;

import com.company.models.Schedule;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class SRTFScheduler extends Scheduler {
    @Override
    protected void prepare() {
        super.prepare();

        var offset = OffsetDateTime.now().getOffset();
        deadlines.sort((a, b) -> (int) (
                a.getRemainingTime().toEpochSecond(offset) - b.getRemainingTime().toEpochSecond(offset))
        );
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
    @Override
    protected boolean schedule() {
        var localDt = LocalDateTime.now();

        for (var it : deadlines) {
            localDt = localDt.plusSeconds(it.getLeadTime());
            if (it.getDateTime().isBefore(localDt))
                return false;
            it.setDateTime(localDt);
        }

        schedule = new Schedule(deadlines);
        return true;
    }
}
