package com.company.scheduling;

import com.company.models.Deadline;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

public class PrioritySRTFScheduler extends SRTFScheduler {
    /**
     * Строит расписание таким образом, чтобы было выполнено максимум дедлайнов с учетом приоритета
     */
    @Override
    protected boolean schedule() {
        var localDt = LocalDateTime.now();
        var it = deadlines.listIterator();

        while (it.hasNext()) {
            var newDt = executeOrRemove(it, localDt);
            if (newDt.isPresent())
                localDt = newDt.get();
        }

        return super.schedule();
    }

    /**
     * Проверяет, что задание может быть выполнено. Если не может - удаляет.
     * Возможно удаляет предыдущие дедлайны с более низким приоритетом.
     *
     * @param it итератор на дедлайн, который надо поместить в расписание.
     * @param at время окончания предыдущей задачи.
     * @return время окончания новой задачи или ничего, если задача не может быть выполнена
     */
    private Optional<LocalDateTime> executeOrRemove(ListIterator<Deadline> it, LocalDateTime at) {
        var d = it.next();
        at = at.plusSeconds(d.getLeadTime());
        var offset = OffsetDateTime.now().getOffset();

        if (at.isAfter(d.getDateTime())) {
            var lost = getPastDeadlinesOfIteratorWithMinimalPriorityLessThenAndSumLeadTimeGreaterThen(
                    it, d.getPriority(),
                    (int) (at.toEpochSecond(offset) - d.getDateTime().toEpochSecond(offset))
            );
            if (lost.isPresent()) {
                for (var lostDeadline : lost.get())
                    at = at.minusSeconds(lostDeadline.getLeadTime());
                while (it.hasNext()) {
                    // возвращаем итератор на место, удаляя невыполнимые дедлайны
                    var next = it.next();
                    if (d.getId() == next.getId())
                        break;
                    if (lost.get().contains(next))
                        it.remove();
                }
            } else {
                while (d.getId() != it.next().getId());  // возвращаем итератор на место
                it.remove();
                return Optional.empty();
            }
        }

        return Optional.of(at);
    }

    private Optional<Set<Deadline>> getPastDeadlinesOfIteratorWithMinimalPriorityLessThenAndSumLeadTimeGreaterThen(
            ListIterator<Deadline> it, short priority, int sumLeadTime
    ) {
        PriorityQueue<Deadline> deadlinesToDelete = new PriorityQueue<>(
                (a, b) -> (b.getPriority() - a.getPriority()) * 10 + Integer.compare(a.getLeadTime(), b.getLeadTime())
        );

        int leadTimeInQueue = 0;
        while (it.hasPrevious()) {
            var d = it.previous();
            if (d.getPriority() > priority) {
                deadlinesToDelete.add(d);
                leadTimeInQueue += d.getLeadTime();
            }
        }

        if (leadTimeInQueue < sumLeadTime)
            return Optional.empty();

        var res = new TreeSet<Deadline>((a, b) -> (int) (a.getId() - b.getId()));

        while (!deadlinesToDelete.isEmpty() && sumLeadTime > 0) {
            var tmp = deadlinesToDelete.poll();
            sumLeadTime -= tmp.getLeadTime();
            res.add(tmp);
        }

        return Optional.of(res);
    }
}
