package com.company.controllers;

import com.company.models.Deadline;
import com.company.models.Subscriber;
import com.company.repositories.SubscriberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;

@RestController()
@RequestMapping("subscribers")
public class SubscriberController {
    @Autowired private ObjectMapper objectMapper;
    @Autowired private SubscriberRepository subscriberRepository;

    @PostMapping
    void subscribe(@RequestBody String subscriberUrl) throws JsonProcessingException, InterruptedException {
        if (!subscriberUrl.startsWith("http"))
            subscriberUrl = "https://" + subscriberUrl;

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
            var newSubscriber = new Subscriber();
            newSubscriber.setWebhookURL(subscriberUrl);
            newSubscriber.setLastSendDeadlineDatetime(LocalDateTime.now());
            subscriberRepository.save(newSubscriber);
        } else throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
    }
}
