package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "inpatientTreatment")
public class InpatientTreatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inpatientTreatmentId;

    private String location;

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

    private Boolean arrived = false;

    private String createdBy;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    @ManyToOne(optional = true)
    @JoinColumn(name = "service_item_id", nullable = true)
    private ServiceItem serviceItem;

    public InpatientTreatment() {

    }

}
