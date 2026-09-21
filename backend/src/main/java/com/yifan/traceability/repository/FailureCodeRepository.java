package com.yifan.traceability.repository;

import com.yifan.traceability.entity.FailureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FailureCodeRepository extends JpaRepository<FailureCode, Long>{

    FailureCode findByCode(String code);
    //List<FailureCode> FindByDescription(String description);
}