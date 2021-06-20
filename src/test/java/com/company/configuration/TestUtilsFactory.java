package com.company.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@TestConfiguration
public class TestUtilsFactory {
    @Bean
    Clock clock() {
        var zone = ZoneId.of("Europe/Moscow");
        var dt = ZonedDateTime.of(LocalDateTime.of(2050, 7, 7, 1, 30), zone);
        return Clock.fixed(dt.toInstant(), zone);
    }
}
