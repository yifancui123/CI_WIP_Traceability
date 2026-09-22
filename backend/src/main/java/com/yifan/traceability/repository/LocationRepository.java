package com.yifan.traceability.repository;

import com.yifan.traceability.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByName(String name);
    List<Location> findByZone(String zone);
}