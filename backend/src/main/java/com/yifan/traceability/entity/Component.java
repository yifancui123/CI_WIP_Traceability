package com.yifan.traceability.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "components")
@Getter
@Setter
@NoArgsConstructor
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long componentId;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private String splitCode;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private String status;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}