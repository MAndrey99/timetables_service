package com.company.repositories;

import com.company.models.Deadline;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Long> {
    List<Deadline> findByCreatorId(long creatorId);
    List<Deadline> findByGroupId(Long groupId);
    List<Deadline> findByGroupIdAndCreatorId(Long groupId, long creatorId);
}
