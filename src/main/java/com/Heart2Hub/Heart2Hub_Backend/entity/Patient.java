package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @NotNull
    @Size(min = 6)
    private String username;

    @NotNull
    @Column(unique = true)
    private String password;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "patient")
    private List<Invoice> listOfInvoices;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "patient_id")
    private List<PaymentMethod> listOfPaymentMethods;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "patient_id")
    private List<TransactionItem> listOfTransactionItem;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "patient_id")
    private List<Appointment> listOfCurrentAppointments;

    @NotNull
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "EHR_id")
    private ElectronicHealthRecord electronicHealthRecord;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "patient", fetch = FetchType.LAZY, optional = true)
    private Admission admission;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    private ImageDocument profilePicture;

    public Patient() {
        this.listOfInvoices = List.of();
        this.listOfPaymentMethods = List.of();
        this.listOfTransactionItem = List.of();
    }

    public Patient(String username, String password) {
        this();
        this.username = username;
        this.password = password;
        this.electronicHealthRecord = null;
        this.admission = null;
    }

    public Patient(String username, String password, ElectronicHealthRecord electronicHealthRecord) {
        this();
        this.username = username;
        this.password = password;
        this.electronicHealthRecord = electronicHealthRecord;
        this.admission = null;
    }


}
