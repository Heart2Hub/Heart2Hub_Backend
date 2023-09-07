package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Time;

@Entity
@Data
@Table(name = "shiftConstraints")
public class ShiftConstraints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftConstraintsId;

    @NotNull
    private Time startTime;

    @NotNull
    private Time endTime;

    @NotNull
    private Integer minPax;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StaffRoleEnum roleEnum;

    public ShiftConstraints() {}

    public ShiftConstraints(Time startTime, Time endTime, Integer minPax, StaffRoleEnum roleEnum) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.minPax = minPax;
        this.roleEnum = roleEnum;
    }
}
