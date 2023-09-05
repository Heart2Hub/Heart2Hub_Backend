package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ProblemTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "medicalHistoryRecord")
public class MedicalHistoryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicalRecordId;

    @NotNull
    private String description;

    @NotNull
    private String createdBy;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    private LocalDateTime resolvedDate;

    @NotNull
    private PriorityEnum priorityEnum;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProblemTypeEnum problemTypeEnum;

    public MedicalHistoryRecord() {
    }

    public MedicalHistoryRecord(String description, String createdBy, LocalDateTime createdDate, LocalDateTime resolvedDate, PriorityEnum priorityEnum, ProblemTypeEnum problemTypeEnum) {
        this.description = description;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.resolvedDate = resolvedDate;
        this.priorityEnum = priorityEnum;
        this.problemTypeEnum = problemTypeEnum;
    }
}
