package com.yifan.traceability.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity // table
@Table(name = "operators")   // maps to the plural table name
@Getter                      // Lombok generates all getters
@Setter                      // Lombok generates all setters
@NoArgsConstructor           // Lombok generates the no-arg constructor JPA needs
public class Operator {

    @Id // columns
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operatorId;

    private String fullName;
    private String employeeNo;
    private String role;
    private OffsetDateTime createdAt;
}