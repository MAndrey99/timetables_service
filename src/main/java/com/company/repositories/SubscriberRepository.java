package com.company.repositories;

import com.company.models.Subscriber;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends CrudRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {}
