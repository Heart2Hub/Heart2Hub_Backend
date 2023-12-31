package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "leave")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @NotNull
    @Column(unique = true)
    private UUID leaveNehrId = UUID.randomUUID();

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Size(max = 200)
    private String comments;

    private LocalDateTime approvedDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ApprovalStatusEnum approvalStatusEnum;

    @Enumerated(EnumType.STRING)
    @NotNull
    private LeaveTypeEnum leaveTypeEnum;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "headstaff_id", nullable = false)
    private Staff headStaff;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    private ImageDocument imageDocuments;

    public Leave() {
        this.approvalStatusEnum = ApprovalStatusEnum.PENDING;
    }

    public Leave(LocalDateTime startDate, LocalDateTime endDate, LeaveTypeEnum leaveTypeEnum) {
        this();
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveTypeEnum = leaveTypeEnum;
    }

    public Leave(LocalDateTime startDate, LocalDateTime endDate, String comments, LeaveTypeEnum leaveTypeEnum) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.comments = comments;
        this.leaveTypeEnum = leaveTypeEnum;
    }


}
