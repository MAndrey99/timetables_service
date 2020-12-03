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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.max;


@Component
@EnableAsync(proxyTargetClass = true)
@Slf4j
public class Notifier extends Thread {
    @Autowired protected DeadlineRepository deadlineRepository;
    @Autowired protected SubscriberRepository subscriberRepository;
    @Autowired protected ObjectMapper objectMapper;
    @Value("${application.subscribers.maxAdvanceNoticeSeconds}") protected int maxAdvanceNoticeSeconds;
    protected Timer timer = null;

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

        final boolean successful = response != null && response.statusCode() == 200;
        if (successful) {
            sub.setLastSendDeadlineDatetime(bucket.getDeadlines().get(0).getDateTime());
            subscriberRepository.save(sub);
        }
        return CompletableFuture.completedFuture(successful);
    }

    protected synchronized void sendNotifications() {
        log.info("начата рассылка сообщений");

        final var now = LocalDateTime.now();
        final List<CompletableFuture<Boolean>> tasks = new LinkedList<>();

        for (var subscriber : subscriberRepository.findAll()) {
            List<Deadline> toSending = new ArrayList<>(deadlineRepository.findAll((r, cq, cb) -> {
                cq.orderBy(cb.desc(r.get("dateTime")));
                return cb.and(
                        cb.lessThan(r.get("dateTime"), now.plusSeconds(maxAdvanceNoticeSeconds / 2)),
                        cb.greaterThanOrEqualTo(r.get("dateTime"), subscriber.getLastSendDeadlineDatetime())
                );
            }));

            if (!toSending.isEmpty()) {
                try {
                    tasks.add(sendNotification(subscriber, new Deadline.DeadlinesBucket(toSending)));
                } catch (InterruptedException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        log.info("разосланы уведомления " + tasks.stream().filter((f) -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return false;
        }).count() + " подписчикам");
        notify();
    }

    protected long nextNotificationDelay() {
        final var deadline = deadlineRepository.findFirstByDateTimeGreaterThanEqualOrderByDateTimeAsc(
                LocalDateTime.now().minusSeconds(maxAdvanceNoticeSeconds / 2)
        );
        var now = OffsetDateTime.now();
        if (deadline.isPresent() && now.isBefore(deadline.get().getDateTime().atOffset(now.getOffset()))) {
            return max(deadline.get().getDateTime().toEpochSecond(now.getOffset())
                    - now.toEpochSecond()
                    - maxAdvanceNoticeSeconds, 0);
        }
        return -1;
    }

    public synchronized void updateNextNotificationTime() {
        notify();
    }

    @Override
    @SneakyThrows
    public synchronized void run() {
        while (true) {
            var nextNotification = nextNotificationDelay();
            if (nextNotification >= 0) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        sendNotifications();
                    }
                }, nextNotification * 1000);
                log.info("следующая рассылка уведомлений подписчикам через " + nextNotification + " секунд");
            } else {
                timer = null;
                log.info("ближайших дедлайнов не найдено, ожидание появления новых");
            }
            sleep(1000);  // как минимум секунда между отправками обновлений(отправляем немного наперёд)
            wait();
            if (timer != null)
                timer.cancel();
        }
    }
}
