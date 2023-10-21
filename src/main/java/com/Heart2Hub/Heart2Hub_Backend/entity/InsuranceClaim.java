package com.Heart2Hub.Heart2Hub_Backend.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Entity
@Data
@Table(name = "insuranceClaim")
public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insuranceClaimId;

    @NotNull
    @Column(unique = true)
    private UUID insuranceClaimNehrId = UUID.randomUUID();

    @NotNull
    private LocalDateTime insuranceClaimDateApplied;

    @NotNull
    private BigDecimal insuranceClaimAmount;

    @NotNull
    private String insurerName;

    @NotNull
    private boolean isPrivateInsurer;

    public InsuranceClaim(LocalDateTime insuranceClaimDateApplied, BigDecimal insuranceClaimAmount, String insurerName, boolean isPrivateInsurer) {
        this.insuranceClaimDateApplied = insuranceClaimDateApplied;
        this.insuranceClaimAmount = insuranceClaimAmount;
        this.insurerName = insurerName;
        this.isPrivateInsurer = isPrivateInsurer;
    }

    public InsuranceClaim(){}
}
