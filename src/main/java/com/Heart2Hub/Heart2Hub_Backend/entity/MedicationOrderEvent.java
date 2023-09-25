package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "medicationOrderEvent")
public class MedicationOrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicationOrderEventId;

    @NotNull
    @Future
    private LocalDateTime startDateTime;

    @NotNull
    @Future
    private LocalDateTime endDateTime;

    @NotNull
    private LocalDateTime completionDateTime;

    @NotNull
    private Boolean isCompleted;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medication_order_id", nullable = false)
    private MedicationOrder medicationOrder;

    public MedicationOrderEvent() {
        this.completionDateTime = LocalDateTime.MAX;
        this.isCompleted = false;
    }

    public MedicationOrderEvent(LocalDateTime startDateTime, LocalDateTime endDateTime, MedicationOrder medicationOrder) {
        this();
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.medicationOrder = medicationOrder;
    }
}
