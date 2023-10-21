package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "wardClass")
public class WardClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wardClassId;

    @NotNull
    @Column(unique = true)
    private UUID wardClassNehrId = UUID.randomUUID();

    @Size(max = 100)
    @NotNull
    private String wardClassName;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal wardClassRate;

    @Min(1)
    @Max(100)
    private Integer bedsPerRoom;

    public WardClass() {
    }

    public WardClass(String wardClassName, BigDecimal wardClassRate, Integer bedsPerRoom) {
        this.wardClassName = wardClassName;
        this.wardClassRate = wardClassRate;
        this.bedsPerRoom = bedsPerRoom;
    }
}
