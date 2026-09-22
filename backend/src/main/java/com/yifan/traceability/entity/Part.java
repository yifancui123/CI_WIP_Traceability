package com.yifan.traceability.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity // table
@Table(name = "parts")   // maps to the plural table name
@Getter                      // Lombok generates all getters
@Setter                      // Lombok generates all setters
@NoArgsConstructor           // Lombok generates the no-arg constructor JPA needs
public class Part {

    @Id // columns
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partId;

    private String partNumber;
    private String name;
    private String unit;
}
