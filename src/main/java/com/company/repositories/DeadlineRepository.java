package com.company.repositories;

import com.company.models.Deadline;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Long> {
    List<Deadline> findAllByDateTimeAfter(LocalDateTime localDateTime);
    List<Deadline> findByCreatorId(long creatorId);
    List<Deadline> findByGroupId(Long groupId);
    List<Deadline> findByGroupIdAndCreatorId(Long groupId, long creatorId);

    List<Deadline> findByCreatorIdAndDateTimeAfter(long creatorId, LocalDateTime localDateTime);
    List<Deadline> findByGroupIdAndDateTimeAfter(Long groupId, LocalDateTime localDateTime);
    List<Deadline> findByGroupIdAndCreatorIdAndDateTimeAfter(Long groupId, long creatorId, LocalDateTime localDateTime);

    default List<Deadline> findByCreatorId(long creatorId, boolean relevant) {
        return relevant ? findByCreatorIdAndDateTimeAfter(creatorId, LocalDateTime.now())
                : findByCreatorId(creatorId);
    }
    default List<Deadline> findByGroupId(long groupId, boolean relevant) {
        return relevant ? findByGroupIdAndDateTimeAfter(groupId, LocalDateTime.now())
                : findByGroupId(groupId);
    }
    default List<Deadline> findByGroupIdAndCreatorId(Long groupId, long creatorId, boolean relevant) {
        return relevant ? findByGroupIdAndCreatorIdAndDateTimeAfter(groupId, creatorId, LocalDateTime.now())
                : findByGroupIdAndCreatorId(groupId, creatorId);
    }
}
