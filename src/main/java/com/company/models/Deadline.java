package com.company.models;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "deadline")
@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC,
        fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC
)
public class Deadline {
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    public static class DeadlinesBucket {
        @Getter protected List<Deadline> deadlines;

        public DeadlinesBucket(List<Deadline> data) {
            this.deadlines = data;
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
    public static class DeadlinePatch {
        protected String title;
        protected String description;
        protected LocalDateTime dateTime;
        protected Integer leadTime;
        protected Short priority;

        public DeadlinePatch(
                @JsonProperty(value = "dateTime") LocalDateTime dateTime,
                @JsonProperty(value = "leadTime") Integer leadTime,
                @JsonProperty(value = "title") String title,
                @JsonProperty(value = "description") String description,
                @JsonProperty(value = "priority") Short priority
        ) {
            this.title = title;
            this.description = description;
            this.dateTime = dateTime;
            this.leadTime = leadTime;
            this.priority = priority;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter protected long id;
    @Getter protected final long creatorId;
    @Getter protected final long groupId;
    @Getter @Setter protected LocalDateTime creationDateTime;  // время появления задачи
    @Getter @Setter protected LocalDateTime dateTime;  // время, к которому задача должна быть выполнена
    @Getter @Setter protected int leadTime;  // предположительное время выполнения задачи в секундах
    @Getter @Setter protected String title;
    @Getter @Setter protected String description;
    @Getter @Setter protected short priority;  // приоритет задачи. 0 - стандартный. Ниже значение - выше приоритет

    public Deadline(@JsonProperty(value = "creatorId", required = true) long creatorId,
                    @JsonProperty(value = "groupId", required = true) long groupId,
                    @JsonProperty(value = "dateTime", required = true) LocalDateTime dateTime,
                    @JsonProperty(value = "leadTime") Integer leadTime,
                    @JsonProperty(value = "priority") Short priority,
                    @JsonProperty(value = "title", required = true) String title,
                    @JsonProperty(value = "description") String description) {
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.creationDateTime = LocalDateTime.now();
        this.dateTime = dateTime;
        this.leadTime = leadTime == null ? 0 : leadTime;
        this.priority = priority == null ? 0 : priority;
        this.title = title;
        this.description = description;
    }

    protected Deadline() {
        creatorId = groupId = leadTime = -1;
    }

    public void applyPatch(DeadlinePatch patch) {
        if (patch.title != null)
            title = patch.title;
        if (patch.dateTime != null)
            dateTime = patch.dateTime;
        if (patch.description != null)
            description = patch.description;
        if (patch.leadTime != null)
            leadTime = patch.leadTime;
        if (patch.priority != null)
            priority = patch.priority;
    }

    @JsonIgnore
    public LocalDateTime getRemainingTime() {
        return dateTime.minusSeconds(leadTime);
    }
}
