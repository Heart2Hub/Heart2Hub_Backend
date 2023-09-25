package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @NotNull
    private BigDecimal invoiceAmount;

    @NotNull
    private LocalDateTime invoiceDueDate;

    @NotNull
    private InvoiceStatusEnum invoiceStatusEnum;

    //Can be Null. An unpaid Invoice has zero Transaction
    @OneToOne(cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true)
    private Transaction transaction;

    //Can be Null. An Invoice can have no Insurance Claim
    @OneToOne(cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "insuranceClaim_id", nullable = true)
    private InsuranceClaim insuranceClaim;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "invoice", fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "insuranceClaim_id", nullable = true)
    private MedishieldClaim medishieldClaim;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    public Invoice(BigDecimal invoiceAmount, LocalDateTime invoiceDueDate, InvoiceStatusEnum invoiceStatusEnum, Patient patient) {
        this.invoiceAmount = invoiceAmount;
        this.invoiceDueDate = invoiceDueDate;
        this.invoiceStatusEnum = InvoiceStatusEnum.UNPAID;
        this.patient = patient;
        this.transaction = null;
        this.insuranceClaim = null;
        this.medishieldClaim = null;
    }

    public Invoice(){}
}
