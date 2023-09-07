package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "shiftPreference")
public class ShiftPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftPreferenceId;

    @NotNull
    private Time startTime;

    @NotNull
    private Time endTime;


    public ShiftPreference() {

    }

    public ShiftPreference(Time startTime, Time endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
