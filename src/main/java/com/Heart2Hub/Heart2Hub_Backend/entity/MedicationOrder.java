package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "medicationOrder")
public class MedicationOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicationOrderId;

    @NotNull
    @Column(unique = true)
    private UUID medicationOrderNehrId = UUID.randomUUID();

    @NotNull
    private String title;

    @NotNull
    private Integer quantity;

    @NotNull
    private String comments;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull
    private Boolean isCompleted = false;

    private String createdBy;


    @ManyToOne(optional = true)
    @JoinColumn(name = "medication_id", nullable = true)
    private Medication medication;

//    @JsonBackReference
//    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @JoinColumn(name = "admission_id",nullable = true)
//    private Admission admission;
//
//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "prescription_record_id", nullable = false)
//    private PrescriptionRecord prescriptionRecord;
//
//    @JsonManagedReference
//    @OneToMany(mappedBy = "medicationOrder", fetch = FetchType.LAZY)
//    private List<MedicationOrderEvent> listOfMedicationOrderEvents;


    public MedicationOrder() {
    }

    public MedicationOrder(String title, Integer quantity, String comments, LocalDateTime startDate, LocalDateTime endDate, String createdBy) {
        this();
        this.title = title;
        this.quantity = quantity;
        this.comments = comments;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdBy = createdBy;
    }
}
