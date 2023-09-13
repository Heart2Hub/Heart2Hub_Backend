package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "shiftConstraints")
public class ShiftConstraints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftConstraintsId;

    @NotNull
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime endTime;

    @NotNull
    private Integer minPax;

    @Enumerated(EnumType.STRING)
    @NotNull
    private RoleEnum roleEnum;

    public ShiftConstraints() {}

    public ShiftConstraints(LocalTime startTime, LocalTime endTime, Integer minPax, RoleEnum roleEnum) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.minPax = minPax;
        this.roleEnum = roleEnum;
    }
}
