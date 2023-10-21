package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Entity
@Data
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @NotNull
    @Column(unique = true)
    private UUID invoiceNehrId = UUID.randomUUID();

    @NotNull
    private BigDecimal invoiceAmount;

    @NotNull
    private LocalDateTime invoiceDueDate;

    @NotNull
    private InvoiceStatusEnum invoiceStatusEnum;

    @NotNull
    @Lob
    private String invoiceBreakdown;

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

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id")
    private List<TransactionItem> listOfTransactionItem;

    public Invoice(BigDecimal invoiceAmount, LocalDateTime invoiceDueDate,
                   Patient patient, List<TransactionItem> listOfTransactionItem,
                   String invoiceBreakdown) {
        this();
        this.invoiceAmount = invoiceAmount;
        this.invoiceDueDate = invoiceDueDate;
        this.invoiceStatusEnum = InvoiceStatusEnum.UNPAID;
        this.listOfTransactionItem = listOfTransactionItem;
        this.patient = patient;
        this.invoiceBreakdown = invoiceBreakdown;
        this.transaction = null;
        this.insuranceClaim = null;
        this.medishieldClaim = null;
    }

    public Invoice(){
        this.listOfTransactionItem = new ArrayList<>();
    }
}
