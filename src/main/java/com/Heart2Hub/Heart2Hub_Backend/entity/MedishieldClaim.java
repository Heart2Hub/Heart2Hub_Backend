package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PatientRequestsEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    public MedishieldClaim(){}
}
