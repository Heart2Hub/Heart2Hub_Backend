package com.Heart2Hub.Heart2Hub_Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "wardAvailability")
public class WardAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wardAvailabilityId;

    @NotNull
    @Column(unique = true)
    private UUID wardAvailabilityNehrId = UUID.randomUUID();

    @NotNull
    private LocalDateTime date;

    @Min(0)
    @Max(200)
    @NotNull
    private Integer bedsAvailable;

    public WardAvailability() {
    }

    public WardAvailability(LocalDateTime date, Integer bedsAvailable) {
        this.date = date;
        this.bedsAvailable = bedsAvailable;
    }
}
