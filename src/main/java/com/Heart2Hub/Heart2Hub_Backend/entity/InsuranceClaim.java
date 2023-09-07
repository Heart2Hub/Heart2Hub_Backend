package com.Heart2Hub.Heart2Hub_Backend.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "insuranceClaim")
public class InsuranceClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long insuranceClaimId;

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
