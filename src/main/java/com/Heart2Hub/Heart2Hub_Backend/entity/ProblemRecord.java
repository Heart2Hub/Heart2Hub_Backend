package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ProblemTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "problemRecord")
public class ProblemRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemRecordId;

    @NotNull
    @Column(unique = true)
    private UUID problemRecordNehrId = UUID.randomUUID();

    @NotNull
    private String description;

    @NotNull
    private String createdBy;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PriorityEnum priorityEnum;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProblemTypeEnum problemTypeEnum;

    public ProblemRecord() {
    }

    public ProblemRecord(String description, String createdBy, LocalDateTime createdDate, PriorityEnum priorityEnum, ProblemTypeEnum problemTypeEnum) {
        this.description = description;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.priorityEnum = priorityEnum;
        this.problemTypeEnum = problemTypeEnum;
    }
}
