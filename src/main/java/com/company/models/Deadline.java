package com.company.models;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Entity
@Table(name = "deadline")
@ToString
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ToString.Exclude
    @Getter private long id;
    @Getter private final long creatorId;
    @Getter private final Long groupId;
    @Getter @Setter private LocalDateTime creationDateTime;
    @Getter @Setter private LocalDateTime dateTime;
    @Getter @Setter private String title;
    @Getter @Setter private String description;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public Deadline(@JsonProperty(value = "creatorId") long creatorId,
                    @JsonProperty(value = "groupId", defaultValue = "null") Long groupId,
                    @JsonProperty(value = "dateTime") String dateTime,
                    @JsonProperty(value = "title") String title,
                    @JsonProperty(value = "description") String description) {
        this(creatorId, groupId, LocalDateTime.now(), LocalDateTime.parse(dateTime, formatter), title, description);
    }

    protected Deadline() {
        creatorId = -1;
        groupId = -1L;
    }

    public Deadline(long creatorId, Long groupId, LocalDateTime creationDateTime, LocalDateTime dateTime,
                    String title, String description) {
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.creationDateTime = creationDateTime;
        this.dateTime = dateTime;
        this.title = title;
        this.description = description;
    }
}
