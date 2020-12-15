package com.company.models;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "deadline")
@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC,
        fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class Deadline {
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    public static class DeadlinesBucket {
        @Getter protected List<Deadline> deadlines;

        public DeadlinesBucket(List<Deadline> data) {
            this.deadlines = data;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;
    @Getter protected final long creatorId;
    @Getter protected final long groupId;
    @Getter @Setter protected LocalDateTime creationDateTime;
    @Getter @Setter protected LocalDateTime dateTime;
    @Getter @Setter protected Long leadTime;  // предположительное время выполнения задачи в секундах
    @Getter @Setter protected String title;
    @Getter @Setter protected String description;

    public Deadline(@JsonProperty(value = "creatorId", required = true) long creatorId,
                    @JsonProperty(value = "groupId", required = true) long groupId,
                    @JsonProperty(value = "dateTime", required = true) LocalDateTime dateTime,
                    @JsonProperty(value = "leadTime") Long leadTime,
                    @JsonProperty(value = "title", required = true) String title,
                    @JsonProperty(value = "description") String description) {
        this(creatorId, groupId, LocalDateTime.now(), dateTime, leadTime, title, description);
    }

    protected Deadline() {
        creatorId = groupId = -1;
    }

    public Deadline(long creatorId, Long groupId, LocalDateTime creationDateTime, LocalDateTime dateTime,
                    Long leadTime, String title, String description) {
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.creationDateTime = creationDateTime;
        this.dateTime = dateTime;
        this.leadTime = leadTime;
        this.title = title;
        this.description = description;
    }
}
