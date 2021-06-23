package com.company.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
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
        protected Long leadTime;
        protected Short priority;
        protected Boolean isTransferable;

        public DeadlinePatch(
                @JsonProperty(value = "dateTime") LocalDateTime dateTime,
                @JsonProperty(value = "leadTime") Long leadTime,
                @JsonProperty(value = "title") String title,
                @JsonProperty(value = "description") String description,
                @JsonProperty(value = "priority") Short priority,
                @JsonProperty(value = "isTransferable") Boolean isTransferable
        ) {
            this.title = title;
            this.description = description;
            this.dateTime = dateTime;
            this.leadTime = leadTime;
            this.priority = priority;
            this.isTransferable = isTransferable;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    protected final Long creatorId;
    protected final Long groupId;
    @Setter protected LocalDateTime creationDateTime;  // время появления задачи
    @Setter protected LocalDateTime dateTime;  // время, к которому задача должна быть выполнена
    @Setter protected Long leadTime;  // предположительное время выполнения задачи в секундах
    @Setter protected String title;
    @Setter protected String description;
    @Setter protected Short priority;  // приоритет задачи. 0 - стандартный. Ниже значение - выше приоритет
    @Setter protected Boolean isTransferable;

    public Deadline(@JsonProperty(value = "creatorId", required = true) Long creatorId,
                    @JsonProperty(value = "groupId", required = true) Long groupId,
                    @JsonProperty(value = "dateTime", required = true) LocalDateTime dateTime,
                    @JsonProperty(value = "leadTime") Long leadTime,
                    @JsonProperty(value = "priority") Short priority,
                    @JsonProperty(value = "title", required = true) String title,
                    @JsonProperty(value = "description") String description,
                    @JsonProperty(value = "isTransferable") Boolean isTransferable) {
        this.creatorId = creatorId;
        this.groupId = groupId;
        this.creationDateTime = LocalDateTime.now();
        this.dateTime = dateTime;
        this.leadTime = leadTime == null ? 0 : leadTime;
        this.priority = priority == null ? 0 : priority;
        this.isTransferable = isTransferable == null || isTransferable;
        this.title = title;
        this.description = description;
    }

    protected Deadline() {
        creatorId = groupId = leadTime = -1L;
        priority = 0;
        isTransferable = true;
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
        if (patch.isTransferable != null)
            isTransferable = patch.isTransferable;
    }

    /**
     * Самое позднее время, в которое можно приступить к выполнению чтобы успеть.
     */
    @JsonIgnore
    public LocalDateTime getRemainingTime() {
        return dateTime.minusSeconds(leadTime);
    }
}
