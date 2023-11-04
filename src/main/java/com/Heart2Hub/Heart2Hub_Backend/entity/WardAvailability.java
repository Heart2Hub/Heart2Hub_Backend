package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    @Min(0)
    @Max(200)
    @NotNull
    private Integer bedsAvailable;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    public WardAvailability() {
    }

    public WardAvailability(LocalDateTime date, Integer bedsAvailable, Ward ward) {
        this.date = date;
        this.bedsAvailable = bedsAvailable;
        this.ward = ward;
    }
}
