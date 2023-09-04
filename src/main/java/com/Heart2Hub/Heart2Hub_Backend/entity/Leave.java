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

@Entity
@Data
@Table(name = "leave")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime startDate;

    @NotNull
    @Future
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

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Staff staff;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Staff headStaff;

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
