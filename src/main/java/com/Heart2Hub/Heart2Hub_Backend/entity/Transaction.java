package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotNull
    @CreationTimestamp
    private LocalDateTime transactionDate;

    @NotNull
    private BigDecimal transactionAmount;

    @NotNull
    private ApprovalStatusEnum approvalStatusEnum;

    public Transaction(BigDecimal transactionAmount, ApprovalStatusEnum approvalStatusEnum) {
        this.transactionAmount = transactionAmount;
        this.approvalStatusEnum = approvalStatusEnum;
    }

    public Transaction(){}

}
