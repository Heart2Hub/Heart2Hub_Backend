package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
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
    private StaffRoleEnum staffRoleEnum;

    public ShiftConstraints() {}

    public ShiftConstraints(LocalTime startTime, LocalTime endTime, Integer minPax, StaffRoleEnum staffRoleEnum) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.minPax = minPax;
        this.staffRoleEnum = staffRoleEnum;
    }
}
