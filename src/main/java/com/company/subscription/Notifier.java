package com.company.subscription;


import com.company.models.Deadline;
import com.company.models.Subscriber;
import com.company.repositories.DeadlineRepository;
import com.company.repositories.SubscriberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;


@Component
@EnableAsync(proxyTargetClass = true)
@Slf4j
public class Notifier extends Thread {
    @Autowired private DeadlineRepository deadlineRepository;
    @Autowired private SubscriberRepository subscriberRepository;
    @Autowired private ObjectMapper objectMapper;
    @Value("${application.subscribers.maxAdvanceNoticeSeconds}") protected int maxAdvanceNoticeSeconds;
    private boolean needRecalculateDelay = false;
    private boolean sending = false;
    private final Object sendingLock = new Object();

    @PostConstruct
    void postConstruct() {
        this.start();
    }

    @Async
    protected CompletableFuture<Boolean> sendNotification(Subscriber sub, Deadline.DeadlinesBucket bucket)
            throws InterruptedException, JsonProcessingException {
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(sub.getWebhookURL()))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(bucket)))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        final boolean successful = (response != null && response.statusCode() == 200);
        if (successful) {
            sub.setLastSendDeadlineDatetime(bucket.getDeadlines().get(bucket.getDeadlines().size() - 1).getDateTime());
            subscriberRepository.save(sub);
        }
        return CompletableFuture.completedFuture(successful);
    }

    @SneakyThrows
    protected void sendNotifications() {
        synchronized (sendingLock) {
            if (needRecalculateDelay)
                return;
            sending = true;
        }
        log.info("начата рассылка сообщений");

        final var now = LocalDateTime.now();
        final List<CompletableFuture<Boolean>> tasks = new LinkedList<>();

        for (var subscriber : subscriberRepository.findAll()) {
            List<Deadline> toSending = new ArrayList<>(deadlineRepository.findAll((r, cq, cb) -> {
                cq.orderBy(cb.asc(r.get("dateTime")));
                return cb.and(
                        cb.lessThan(r.get("dateTime"), now.plusSeconds(maxAdvanceNoticeSeconds)),
                        cb.greaterThan(r.get("dateTime"), subscriber.getLastSendDeadlineDatetime())
                );
            }));

            if (!toSending.isEmpty())
                tasks.add(sendNotification(subscriber, new Deadline.DeadlinesBucket(toSending)));
        }

        log.info("разосланы уведомления %d подписчикам".formatted(tasks.stream().filter(f -> {
            try {
                return f.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                interrupt();
            }
            return false;
        }).count()));

        sending = false;
        synchronized (this) {
            needRecalculateDelay = true;
            notifyAll();
        }
    }

    protected long getNextNotificationDelay() {
        final var now = OffsetDateTime.now();
        final var lastSentDeadlineDateTime = subscriberRepository.findMinLastSendDeadlineDatetime();
        if (lastSentDeadlineDateTime.isEmpty())
            return -1;  // нет подписчиков
        final var deadline = deadlineRepository.findFirstByDateTimeGreaterThanOrderByDateTime(
                lastSentDeadlineDateTime.get()
        );
        if (deadline.isEmpty())
            return -1;  // нет предстоящих дедлайнов
        var res = deadline.get().getDateTime().toEpochSecond(now.getOffset())
                - now.toEpochSecond() - maxAdvanceNoticeSeconds;
        if (res <= 0)
            return 0;  // требуется отправить дедлайн, который мы почему-то не отправили ранее
        return res;
    }

    public synchronized void updateNextNotificationTime() {
        if (!needRecalculateDelay) {
            needRecalculateDelay = true;
            notifyAll();
        }
    }

    private synchronized void waitRecalculation() throws InterruptedException {
        needRecalculateDelay = false;
        while (!needRecalculateDelay)
            wait();
    }

    private void cancelSending(ScheduledFuture<?> future) throws ExecutionException, InterruptedException {
        synchronized (sendingLock) {
            if (sending) {
                future.get();
                sending = false;
            } else {
                future.cancel(true);
            }
        }
    }

    @Override
    @SneakyThrows
    public void run() {
        var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        Optional<ScheduledFuture<?>> future = Optional.empty();

        while (true) {
            var nextNotificationDelay = getNextNotificationDelay();
            synchronized (this) {
                if (nextNotificationDelay >= 0) {
                    log.info("следующая рассылка уведомлений подписчикам через " + nextNotificationDelay + " секунд");
                    future = Optional.of(scheduledExecutor.schedule(
                            this::sendNotifications, nextNotificationDelay, TimeUnit.SECONDS
                    ));
                } else {
                    log.info("пока отправлять нечего");
                }
                waitRecalculation();
            }

            if (future.isPresent() && !future.get().isDone())
                cancelSending(future.get());
        }
    }
}
