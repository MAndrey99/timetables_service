package com.company.repositories;

import com.company.models.Subscriber;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends CrudRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    Optional<Subscriber> findFirstByWebhookURL(String webhookURL);

    @Query("select min(lastSendDeadlineDatetime) from Subscriber")
    Optional<LocalDateTime> findMinLastSendDeadlineDatetime();
}
