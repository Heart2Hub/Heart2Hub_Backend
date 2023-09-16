package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "medishieldClaim")
public class MedishieldClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medishieldClaimId;

    @NotNull
    private LocalDateTime medishieldClaimDateApplied;

    @NotNull
    private BigDecimal medishieldClaimAmount;

    @NotNull
    private ApprovalStatusEnum approvalStatusEnum;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;


    public MedishieldClaim(LocalDateTime medishieldClaimDateApplied, BigDecimal medishieldClaimAmount, ApprovalStatusEnum approvalStatusEnum, Invoice invoice) {
        this.medishieldClaimDateApplied = medishieldClaimDateApplied;
        this.medishieldClaimAmount = medishieldClaimAmount;
        this.approvalStatusEnum = approvalStatusEnum;
        this.invoice = invoice;
    }

    public MedishieldClaim() {
    }
}
