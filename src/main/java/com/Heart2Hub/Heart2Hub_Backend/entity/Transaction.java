package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
    @Column(unique = true)
    private UUID transactionNehrId = UUID.randomUUID();

    @NotNull
    //@CreationTimestamp
    private LocalDateTime transactionDate;

    @NotNull
    private BigDecimal transactionAmount;

    @NotNull
    private ApprovalStatusEnum approvalStatusEnum;

    public Transaction(BigDecimal transactionAmount, ApprovalStatusEnum approvalStatusEnum) {
//        this.transactionDate = LocalDateTime.now();
        this.transactionAmount = transactionAmount;
        this.approvalStatusEnum = approvalStatusEnum;
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(){}

}
