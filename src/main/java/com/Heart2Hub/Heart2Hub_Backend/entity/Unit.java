package com.Heart2Hub.Heart2Hub_Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "unit")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long unitId;

    @Size(max = 100)
    @NotNull
    private String name;

    public Unit() {
    }

    public Unit(String name) {
        this.name = name;
    }
}
