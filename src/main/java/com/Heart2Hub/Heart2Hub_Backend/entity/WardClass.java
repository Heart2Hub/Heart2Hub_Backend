package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "wardClass")
public class WardClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wardClassId;

    @Size(max = 100)
    @NotNull
    private String wardClassName;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal wardClassRate;


    public WardClass() {
    }

    public WardClass(String wardClassName, BigDecimal wardClassRate) {
        this.wardClassName = wardClassName;
        this.wardClassRate = wardClassRate;
    }
}
