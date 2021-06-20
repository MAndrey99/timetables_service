package com.company.repositories;

import com.company.dto.DeadlineFilter;
import com.company.models.Deadline;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Long>, JpaSpecificationExecutor<Deadline> {

    Optional<Deadline> findFirstByDateTimeGreaterThanOrderByDateTime(LocalDateTime dt);

    default List<Deadline> findAllByFilter(DeadlineFilter filter) {
        return findAll((r, cq, cb) -> {
            cq.orderBy(cb.asc(r.get("dateTime")));
            var res = cb.conjunction();
            if (filter.groupId() != null) {
                res = cb.and(res, cb.equal(r.get("groupId"), filter.groupId()));
            }
            if (filter.creatorId() != null) {
                res = cb.and(res, cb.equal(r.get("creatorId"), filter.creatorId()));
            }
            if (filter.relevant())
                res = cb.and(res, cb.greaterThan(r.get("dateTime"), LocalDateTime.now()));
            return res;
        });
    }
}
