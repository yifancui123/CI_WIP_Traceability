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
@Table(name = "status_checks")
@Getter
@Setter
@NoArgsConstructor
public class StatusCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkId;

    @ManyToOne
    @JoinColumn(name = "component_id")
    private Component component;

    private String status;

    @ManyToOne
    @JoinColumn(name = "failure_code_id")
    private FailureCode failureCode;

    @ManyToOne
    @JoinColumn(name = "checked_by")
    private Operator checkedBy;

    private OffsetDateTime checkedAt;

    private Integer quantity;

    private String notes;
}