package com.company.services;

import com.company.models.Deadline;
import com.company.models.Subscriber;
import com.company.repositories.SubscriberRepository;
import com.company.subscription.Notifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
public class SubscriberService {

    private final ObjectMapper objectMapper;
    private final SubscriberRepository subscriberRepository;
    private final Notifier notifier;

    @Autowired
    private SubscriberService(ObjectMapper objectMapper, SubscriberRepository subscriberRepository, Notifier notifier) {
        this.objectMapper = objectMapper;
        this.subscriberRepository = subscriberRepository;
        this.notifier = notifier;
    }

    public void subscribe( String subscriberUrl) throws JsonProcessingException, InterruptedException {
        if (!subscriberUrl.startsWith("http"))
            subscriberUrl = "https://" + subscriberUrl;

        if (subscriberRepository.findFirstByWebhookURL(subscriberUrl).isPresent()) {
            log.info("предотвращено повторное добавление подписчика");
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // пытаемся отправить новому подписчику пробный запрос
        final var client = HttpClient.newHttpClient();
        final var request = HttpRequest.newBuilder()
                .uri(URI.create(subscriberUrl))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
                        new Deadline.DeadlinesBucket(
                                new ArrayList<>())
                )))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null && response.statusCode() == 200) {
            // подписчик принял наш запрос
            var newSubscriber = new Subscriber();
            newSubscriber.setWebhookURL(subscriberUrl);
            newSubscriber.setLastSendDeadlineDatetime(LocalDateTime.now());
            subscriberRepository.save(newSubscriber);
            log.info("добавлен новый подписчик: " + subscriberUrl);
        } else {
            // подписчик не принял наш запрос или сделать запрос не удалось
            log.info("добавить подписчика " + subscriberUrl + " не удалось");
            throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
        }

        notifier.updateNextNotificationTime();
    }

    public void unsubscribe(String url) {
        if (!url.startsWith("http"))
            url = "https://" + url;

        var sub = subscriberRepository.findFirstByWebhookURL(url);

        if (sub.isPresent()) {
            subscriberRepository.delete(sub.get());
            log.info("удален подписчик " + url);
        } else {
            log.info("не удалось удалить подписчика(подписчик не найден) " + url);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }
}
