package com.company.models;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "deadline")
@ToString
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Exclude
    @Getter private long id;
    @Getter private final long creatorId;
    @Getter @Setter private LocalDateTime creationDateTime;
    @Getter @Setter private LocalDateTime dateTime;
    @Getter @Setter private String title;
    @Getter @Setter private String description;

    protected Deadline() {
        creatorId = -1;
    }

    public Deadline(long creatorId, LocalDateTime creationDateTime, LocalDateTime dateTime,
                    String title, String description) {
        this.creatorId=creatorId;
        this.creationDateTime = creationDateTime;
        this.dateTime = dateTime;
        this.title = title;
        this.description = description;
    }
}
