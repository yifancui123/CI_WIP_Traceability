package com.yifan.traceability.repository;

import com.yifan.traceability.entity.Component;
import com.yifan.traceability.entity.Location;
import com.yifan.traceability.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long>{

    Component findBySplitCode(String splitCode);
    List<Component> findByQuantity(int quantity);
    List<Component> findByStatus(String status);
    List<Component> findByLocation(Location location);
    List<Component> findByJob(Job job);
}