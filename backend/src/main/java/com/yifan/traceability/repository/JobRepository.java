package com.yifan.traceability.repository;

import com.yifan.traceability.entity.Job;
import com.yifan.traceability.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    Job findByJobNumber(String jobNumber);
    List<Job> findByPart(Part part);
    List<Job> findByExpiryDate(LocalDate expiryDate);
    List<Job> findByIsDeleted(boolean isDeleted);
}
