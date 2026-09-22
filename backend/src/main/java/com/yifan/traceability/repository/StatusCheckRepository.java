package com.yifan.traceability.repository;

import com.yifan.traceability.entity.StatusCheck;
import com.yifan.traceability.entity.Component;
import com.yifan.traceability.entity.FailureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatusCheckRepository extends JpaRepository<StatusCheck, Long> {
    List<StatusCheck> findByComponentOrderByCheckedAtDesc(Component component);
    List<StatusCheck> findByFailureCode(FailureCode failureCode);
}
