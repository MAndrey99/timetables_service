package com.company.scheduling;

import com.company.models.Deadline;
import com.company.dto.Schedule;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

interface ScheduleStrategy {

    /**
     * Перед построением расписаний выполняется предварительная обработка дедлайнов.
     * Одним из этапов предварительной обработки является сортировка.
     */
    Comparator<Deadline> getComparator();

    /**
     * Помещает дедлайны(возможно часть) в расписание для выполнения в указанном промежутке времени.
     * Работает только для промежутков, в которых не учитываются не перемещаемые дедлайны.
     *
     * @param from время, начиная с которого могут выполняться задачи.
     * @param to время, до которого возможно выполнение задач.
     * @param deadlines перемещаемые по расписанию дедлайны дедлайны.
     * @return Schedule если хоть один дедлайн получилось выполнить в указанном промежутке времени.
     */
    Optional<Schedule> schedule(@NonNull LocalDateTime from, @NonNull  LocalDateTime to,
                                @NonNull List<Deadline> deadlines);

    /**
     * Оптимизирует уже построенное расписание с учетом того, что в нём есть и перемещаемые и не перемещаемые дедлайны.
     *
     * @param from время, начиная с которого могут выполняться задачи.
     * @param to время, до которого возможно выполнение задач.
     * @param schedule уже построенное расписание, куда вошли не все дедлайны.
     * @param unscheduledDeadlines не вошедшие в расписание дедлайны.
     */
    void schedulePostprocessing(@NonNull LocalDateTime from, @NonNull LocalDateTime to,
                                @NonNull Schedule schedule, @NonNull List<Deadline> unscheduledDeadlines);
}
