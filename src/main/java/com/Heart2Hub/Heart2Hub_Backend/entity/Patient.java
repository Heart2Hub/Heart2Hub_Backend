package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@EqualsAndHashCode(exclude="electronicHealthRecord")
@Entity
@Data
@Table(name = "patient")
public class Patient implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    @NotNull
    @Column(unique = true)
    private UUID patientNehrId = UUID.randomUUID();

    @NotNull
    @Size(min = 6)
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    private String password;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "patient")
    private List<Invoice> listOfInvoices;



    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "patient_id")
    private List<TransactionItem> listOfTransactionItem;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "patient_id")
    private List<Appointment> listOfCurrentAppointments;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EHR_id", nullable = false)
    private ElectronicHealthRecord electronicHealthRecord;

    @JsonIgnore
    @OneToOne(mappedBy = "patient", fetch = FetchType.LAZY)
    private Admission admission;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    private ImageDocument profilePicture;


    public Patient() {
        this.listOfInvoices = new ArrayList<>();
//        this.listOfPaymentMethods = new ArrayList<>();
        this.listOfTransactionItem = new ArrayList<>();
        this.listOfCurrentAppointments = new ArrayList<>();
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

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("Patient"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
