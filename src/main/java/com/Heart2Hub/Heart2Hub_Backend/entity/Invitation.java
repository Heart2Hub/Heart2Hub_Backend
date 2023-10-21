package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.TreatmentPlanTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    @NotNull
    @Column(unique = true)
    private UUID invitationNehrId = UUID.randomUUID();

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    public Invitation() {
    }

    public Invitation(String createdDate, String invitedBy, Boolean isAccepted, Boolean isRead) {
        this.createdDate = createdDate;
        this.invitedBy = invitedBy;
        this.isAccepted = isAccepted;
        this.isRead = isRead;
    }
}
