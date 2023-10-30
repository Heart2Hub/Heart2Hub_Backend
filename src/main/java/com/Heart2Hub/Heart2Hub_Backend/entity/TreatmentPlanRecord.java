package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.TreatmentPlanTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "treatmentPlanRecord")
public class TreatmentPlanRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long treatmentPlanRecordId;

    @NotNull
    @NotBlank
    @Size(max = 5000, message = "Description too long")
    private String description;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull
    private Boolean isCompleted;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TreatmentPlanTypeEnum treatmentPlanTypeEnum;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ImageDocument> listOfImageDocuments;

    public TreatmentPlanRecord() {
        this.listOfImageDocuments = new ArrayList<>();
        this.endDate = null;
        this.isCompleted=false;
    }

    public TreatmentPlanRecord(String description, LocalDateTime startDate, TreatmentPlanTypeEnum treatmentPlanTypeEnum) {
        this();
        this.description = description;
        this.startDate = startDate;
        this.treatmentPlanTypeEnum = treatmentPlanTypeEnum;
    }
}
