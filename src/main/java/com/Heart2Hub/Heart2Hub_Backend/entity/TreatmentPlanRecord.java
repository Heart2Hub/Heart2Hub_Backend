package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.TreatmentPlanTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private String description;

    @NotNull
    private String primaryDoctor;

    @ElementCollection
    private List<String> secondaryDoctors;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TreatmentPlanTypeEnum treatmentPlanTypeEnum;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ImageDocument> listOfImageDocuments;

    public TreatmentPlanRecord() {
        this.listOfImageDocuments = new ArrayList<>();
    }

    public TreatmentPlanRecord(String description, String primaryDoctor, List<String> secondaryDoctors, LocalDateTime startDate, LocalDateTime endDate, TreatmentPlanTypeEnum treatmentPlanTypeEnum) {
        this.description = description;
        this.primaryDoctor = primaryDoctor;
        this.secondaryDoctors = secondaryDoctors;
        this.startDate = startDate;
        this.endDate = endDate;
        this.treatmentPlanTypeEnum = treatmentPlanTypeEnum;
    }
}
