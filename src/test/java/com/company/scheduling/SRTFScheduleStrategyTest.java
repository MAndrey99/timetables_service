package com.company.scheduling;

import com.company.models.Deadline;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.LinkedList;


class SRTFScheduleStrategyTest {
    private final SRTFScheduleStrategy srtfScheduleStrategy = new SRTFScheduleStrategy();

    @Test
    void schedule() {
        var deadlines = new LinkedList<Deadline>();
        deadlines.add(
                Deadline.builder()
                        .id(0L)
                        .leadTime(2000L)
                        .dateTime(LocalDateTime.of(2020, 1, 1, 12, 20))
                        .build()
        );
        deadlines.add(
                Deadline.builder()
                        .id(1L)
                        .leadTime(1000L)
                        .dateTime(LocalDateTime.of(2020, 1, 1, 12, 30))
                        .build()
        );
        var res = srtfScheduleStrategy.schedule(
                LocalDateTime.of(2019, 10, 11, 20, 30),
                LocalDateTime.of(2020, 11, 11, 20, 30),
                deadlines
        ).orElseThrow();
        assertEquals(0, res.getUnscheduledDeadlines().size());

        var scheduled = res.getScheduledDeadlines();
        assertEquals(2, scheduled.size());
        assertEquals(0, scheduled.get(0).getId());
        assertEquals(LocalDateTime.of(2019, 10, 11, 21, 3, 20), scheduled.get(0).getDateTime());
        assertEquals(LocalDateTime.of(2019, 10, 11, 21, 20), scheduled.get(1).getDateTime());
    }

//    @Test
//    void schedulePostprocessing() {
//        // TODO
//    }
}
