package com.Heart2Hub.Heart2Hub_Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "unit")
@Inheritance(strategy = InheritanceType.JOINED)
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;

    @NotNull
    @Column(unique = true)
    private UUID unitNehrId = UUID.randomUUID();

    @Size(max = 100)
    @NotNull
    private String name;

    public Unit() {
    }

    public Unit(String name) {
        this.name = name;
    }
}
