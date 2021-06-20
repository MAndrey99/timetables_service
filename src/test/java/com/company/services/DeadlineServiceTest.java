package com.company.services;

import com.company.configuration.TestUtilsFactory;
import com.company.models.Deadline;
import com.company.repositories.DeadlineRepository;
import com.company.subscription.Notifier;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@Import(TestUtilsFactory.class)
@SpringBootTest
class DeadlineServiceTest {

    @MockBean
    private DeadlineRepository deadlineRepository;

    @Autowired
    private DeadlineService deadlineService;

    @MockBean
    private Notifier notifier;

    @Autowired
    private Clock clock;

    @Test
    void postDeadline() {
        var deadline = Deadline.builder()
                .id(1L)
                .isTransferable(true)
                .title("title")
                .leadTime(10L)
                .creationDateTime(LocalDateTime.now(clock))
                .dateTime(LocalDateTime.now(clock).plusDays(5))
                .priority((short)2)
                .build();

        deadlineService.postDeadline(deadline);

        verify(deadlineRepository).save(ArgumentMatchers.eq(deadline));
        verify(notifier).updateNextNotificationTime();
    }

    @Test
    void patchDeadline() {
        // TODO
    }

    @Test
    void getDeadline() {
        // TODO
    }

    @Test
    void testGetDeadline() {
        // TODO
    }

    @Test
    void deleteDeadline() {
        // TODO
    }
}