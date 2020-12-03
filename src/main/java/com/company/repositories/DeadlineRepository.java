package com.company.repositories;

import com.company.models.Deadline;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Long>, JpaSpecificationExecutor<Deadline> {
    Optional<Deadline> findFirstByDateTimeGreaterThanEqualOrderByDateTimeAsc(final LocalDateTime dt);
}
