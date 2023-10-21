package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Entity
@Data
@Table(name = "admission")
public class Admission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admissionId;

    @NotNull
    @Column(unique = true)
    private UUID admissionNehrId = UUID.randomUUID();

    @NotNull
    private Integer duration;

    @NotNull
    private LocalDateTime admissionDateTime;

    @NotNull
    private LocalDateTime dischargeDateTime;

    @NotNull
    private String comments;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id", referencedColumnName = "id")
    private Patient patient;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admission_id", referencedColumnName = "id")
    private FacilityBooking facilityBooking;

    @JsonManagedReference
    @OneToMany(mappedBy = "admission",fetch = FetchType.LAZY)
    private List<MedicationOrder> listOfMedicationOrders;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PatientRequest> listOfPatientRequests;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "ward_id", nullable = false)
    private Ward ward;

    public Admission(){
        this.listOfMedicationOrders = new ArrayList<>();
        this.listOfPatientRequests = new ArrayList<>();
    }

    public Admission(Integer duration, LocalDateTime admissionDateTime, LocalDateTime dischargeDateTime, String comments, Patient patient) {
        this();
        this.duration = duration;
        this.admissionDateTime = admissionDateTime;
        this.dischargeDateTime = dischargeDateTime;
        this.comments = comments;
        this.patient = patient;
    }


}
