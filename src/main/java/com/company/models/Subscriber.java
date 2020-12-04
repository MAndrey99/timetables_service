package com.company.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сервис, которому требуется присылать уведомления о таких событиях как наступление дедлайна.
 */
@Entity
@Table(name = "subscriber")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * По этому url будет отправляться json с наступающими дедлайнами в push запросе
     */
    @Getter @Setter private String webhookURL;

    @Getter @Setter private LocalDateTime lastSendDeadlineDatetime;
}
