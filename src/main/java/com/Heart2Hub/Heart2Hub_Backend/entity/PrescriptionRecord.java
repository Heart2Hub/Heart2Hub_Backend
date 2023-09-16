package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PrescriptionStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "prescriptionRecord")
public class PrescriptionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionRecordId;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    private String medicationName;

    @NotNull
    private String medicationQuantity;

    @NotNull
    private Integer dosage;

    @NotNull
    private String description;

    @NotNull
    private String comments;

    @NotNull
    private String prescribedBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PrescriptionStatusEnum prescriptionStatusEnum;

    public PrescriptionRecord() {
    }

    public PrescriptionRecord(LocalDateTime createdDate, String medicationName, String medicationQuantity, Integer dosage, String description, String comments, String prescribedBy, PrescriptionStatusEnum prescriptionStatusEnum) {
        this.createdDate = createdDate;
        this.medicationName = medicationName;
        this.medicationQuantity = medicationQuantity;
        this.dosage = dosage;
        this.description = description;
        this.comments = comments;
        this.prescribedBy = prescribedBy;
        this.prescriptionStatusEnum = prescriptionStatusEnum;
    }
}
