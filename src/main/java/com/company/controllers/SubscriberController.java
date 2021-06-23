package com.company.controllers;

import com.company.services.SubscriberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
@RequestMapping("subscribers")
@Timed
class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @PostMapping
    void subscribe(@RequestBody String subscriberUrl) throws JsonProcessingException, InterruptedException {
        subscriberService.subscribe(subscriberUrl);
    }

    @DeleteMapping
    void unsubscribe(@RequestParam String url) {
        subscriberService.unsubscribe(url);
    }
}
