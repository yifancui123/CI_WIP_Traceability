package com.yifan.traceability.repository;

import com.yifan.traceability.entity.MovementHistory;
import com.yifan.traceability.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovementHistoryRepository extends JpaRepository<MovementHistory, Long> {
    List<MovementHistory> findByComponentOrderByMovedAtDesc(Component component);
}
