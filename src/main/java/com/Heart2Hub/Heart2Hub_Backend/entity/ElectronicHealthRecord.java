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
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Table(name = "electronicHealthRecord")
public class ElectronicHealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long electronicHealthRecordId;

    @NotNull
    private String nric;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfBirth;

    @NotNull
    private String placeOfBirth;

    @NotNull
    private String sex;

    @NotNull
    private String race;

    @NotNull
    private String nationality;

    @NotNull
    private String address;

    @NotNull
    private String contactNumber;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EHR_Id", nullable = true)
    private List<Subsidy> listOfSubsidies;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EHR_Id")
    private List<Admission> listOfPastAdmissions;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EHR_Id")
    private List<Appointment> listOfPastAppointments;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EHR_Id")
    private List<NextOfKinRecord> listOfNextOfKinRecords;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EHR_Id")
    private List<PrescriptionRecord> listOfPrescriptionRecords;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EHR_Id")
    private List<ProblemRecord> listOfProblemRecords;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "EHR_Id")
    private List<MedicalHistoryRecord> listOfMedicalHistoryRecords;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "EHR_Id")
    private List<TreatmentPlanRecord> listOfTreatmentPlanRecords;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "electronicHealthRecord", fetch = FetchType.LAZY, optional = false)
    private Patient patient;

    public ElectronicHealthRecord(){
        this.listOfPastAdmissions = new ArrayList<>();
        this.listOfPastAppointments = new ArrayList<>();
        this.listOfSubsidies = new ArrayList<>();
        this.listOfNextOfKinRecords = new ArrayList<>();
        this.listOfPrescriptionRecords = new ArrayList<>();
        this.listOfProblemRecords = new ArrayList<>();
        this.listOfMedicalHistoryRecords = new ArrayList<>();
        this.listOfTreatmentPlanRecords = new ArrayList<>();
    }

    public ElectronicHealthRecord(String nric, String firstName, String lastName, LocalDateTime dateOfBirth,
                                  String placeOfBirth, String sex, String race, String nationality, String address,
                                  String contactNumber) {
        this();
        this.nric = nric;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
        this.sex = sex;
        this.race = race;
        this.nationality = nationality;
        this.address = address;
        this.contactNumber = contactNumber;
    }
}
