package com.yifan.traceability.repository;

import com.yifan.traceability.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OperatorRepository extends JpaRepository<Operator, Long>{
    List<Operator> findByFullName(String fullName);
    Operator findByEmployeeNo(String employeeNo);
    List<Operator> findByRole(String role);
    //Operator findByCreateAt(OffsetDateTime);
}