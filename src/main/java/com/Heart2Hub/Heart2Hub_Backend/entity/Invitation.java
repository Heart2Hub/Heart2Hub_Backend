package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.TreatmentPlanTypeEnum;
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
    private String createdDate;

    @NotNull
    private String invitedBy;

    @NotNull
    private Boolean isAccepted;

    @NotNull
    private Boolean isRead;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private TreatmentPlanRecord treatmentPlanRecord;

    public Invitation() {
    }

    public Invitation(String createdDate, String invitedBy, Boolean isAccepted, Boolean isRead) {
        this.createdDate = createdDate;
        this.invitedBy = invitedBy;
        this.isAccepted = isAccepted;
        this.isRead = isRead;
    }
}
