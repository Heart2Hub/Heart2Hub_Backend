package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.util.UUID;

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
    @Column(unique = true)
    private UUID admissionNehrId = UUID.randomUUID();

    @NotNull
    private Integer duration;

    private Integer room;

    private Integer bed;

    private String reason;

    private String comments = "";

    private Boolean arrived = false;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime admissionDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dischargeDateTime;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "admin_id")
//    private Staff currentAssignedAdmin;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "nurse_id")
//    private Staff currentAssignedNurse;
//
//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "doctor_id")
//    private Staff currentAssignedDoctor;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Staff> listOfAssignedStaff;

    @JsonIgnore
    @OneToMany(mappedBy = "admission",fetch = FetchType.LAZY)
    private List<MedicationOrder> listOfMedicationOrders;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id")
    private List<PatientRequest> listOfPatientRequests;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    public Admission(){
        this.listOfMedicationOrders = new ArrayList<>();
        this.listOfPatientRequests = new ArrayList<>();
        this.listOfAssignedStaff = new ArrayList<>();
    }

    public Admission(Integer duration, String reason) {
        this();
        this.duration = duration;
        this.reason = reason;
    }

//    public Admission(Integer duration, LocalDateTime admissionDateTime, LocalDateTime dischargeDateTime, String comments) {
//        this();
//        this.duration = duration;
//        this.admissionDateTime = admissionDateTime;
//        this.dischargeDateTime = dischargeDateTime;
//        this.comments = comments;
//    }

//    public Admission(LocalDateTime admissionDateTime, LocalDateTime dischargeDateTime, String comments, Ward ward) {
//        this();
//        this.admissionDateTime = admissionDateTime;
//        this.dischargeDateTime = dischargeDateTime;
//        this.comments = comments;
//        this.ward = ward;
//    }


}
