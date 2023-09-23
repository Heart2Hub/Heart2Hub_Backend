package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Table(name = "admission")
public class Admission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long admissionId;

    @NotNull
    private Integer duration;

    @NotNull
    private LocalDateTime admissionDateTime;

    @NotNull
    private LocalDateTime dischargeDateTime;

    @NotNull
    private String comments;

    @NotNull
    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admission_id", referencedColumnName = "id")
    private Patient patient;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "admission_id", referencedColumnName = "id")
    private FacilityBooking facilityBooking;

    @JsonBackReference
    @OneToMany(mappedBy = "admission")
    private List<MedicationOrder> listOfMedicationOrders;

    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PatientRequest> listOfPatientRequests;

    @JsonIgnore
    @ManyToOne
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
