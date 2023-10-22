package com.Heart2Hub.Heart2Hub_Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "drugRestriction")
public class DrugRestriction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drugRestrictionId;

    @NotNull
    @Size(max = 5000)
    private String drugName;

    public DrugRestriction(String drugName) {

        this.drugName = drugName;
    }

    public DrugRestriction() {
    }
}
