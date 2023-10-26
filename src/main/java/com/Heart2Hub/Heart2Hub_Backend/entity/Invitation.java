package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.TreatmentPlanTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @NotNull
    private String invitedBy;

    @NotNull
    private Boolean isPrimary;

    @NotNull
    private Boolean isRead;

    @NotNull
    private Boolean isApproved;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatmentPlanRecordId",nullable = false)
    private TreatmentPlanRecord treatmentPlanRecord;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    public Invitation() {
        this.isRead = false;
        this.isApproved = false;
    }

    public Invitation(String invitedBy, Boolean isPrimary) {
        this();
        this.createdDate = LocalDateTime.now();
        this.invitedBy = invitedBy;
        this.isPrimary = isPrimary;
    }
}
