package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PrescriptionStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @NotNull
    private String medicationName;

    @NotNull
    private Integer medicationQuantity;

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

    //@NotNull
    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inventoryItem_id", nullable = true)
    private InventoryItem inventoryItem;

    public PrescriptionRecord() {
    }

    public PrescriptionRecord(LocalDateTime createdDate, String medicationName, Integer medicationQuantity, Integer dosage, String description, String comments,
                              String prescribedBy, PrescriptionStatusEnum prescriptionStatusEnum, InventoryItem inventoryItem) {
        this.createdDate = createdDate;
        this.medicationName = medicationName;
        this.medicationQuantity = medicationQuantity;
        this.dosage = dosage;
        this.description = description;
        this.comments = comments;
        this.prescribedBy = prescribedBy;
        this.prescriptionStatusEnum = prescriptionStatusEnum;
        this.inventoryItem = inventoryItem;
    }
}
