package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "medicationOrder")
public class MedicationOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicationOrderId;

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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medication_id", nullable = false)
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

    public MedicationOrder(String title, Integer quantity, String comments, LocalDateTime startDate, LocalDateTime endDate) {
        this();
        this.title = title;
        this.quantity = quantity;
        this.comments = comments;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
