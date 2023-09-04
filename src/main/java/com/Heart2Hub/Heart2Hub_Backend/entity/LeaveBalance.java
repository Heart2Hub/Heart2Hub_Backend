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
@Table(name = "leaveBalance")
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveBalanceId;

    @NotNull
    private Integer annualLeave = 14;

    @NotNull
    private Integer sickLeave = 30;

    @NotNull
    private Integer parentalLeave = 0;

    public LeaveBalance() {

    }

    public LeaveBalance(Integer annualLeave, Integer sickLeave, Integer parentalLeave) {
        this.annualLeave = annualLeave;
        this.sickLeave = sickLeave;
        this.parentalLeave = parentalLeave;
    }
}
