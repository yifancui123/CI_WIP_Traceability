package com.yifan.traceability.repository;

import com.yifan.traceability.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {
    List <Part> findByPartName(String partName);
    Part findByPartNumber(String partNumber);
    List <Part> findByUnit(String unit);
}