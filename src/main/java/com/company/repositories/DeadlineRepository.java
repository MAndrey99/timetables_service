package com.company.repositories;

import com.company.models.Deadline;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Long>, JpaSpecificationExecutor<Deadline> {}
