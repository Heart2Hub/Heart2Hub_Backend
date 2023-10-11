package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentId;

    @NotNull
    @Size(max = 5000, message = "Description too long")
    private String description;

    @NotNull
    @Size(max = 5000, message = "Comments too long")
    private String comments = "";

//    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDateTime;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookedDateTime;

    @NotNull
    private Time estimatedDuration = Time.valueOf("01:00:00");

    @NotNull
    private Boolean arrived = false;

    @NotNull
    private Time elapsedTime = Time.valueOf("00:00:00");

    @NotNull
    @Enumerated(EnumType.STRING)
    private PriorityEnum priorityEnum;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SwimlaneStatusEnum swimlaneStatusEnum = SwimlaneStatusEnum.REGISTRATION;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff currentAssignedStaff = null;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Staff> listOfStaff;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "appointment_id")
    private List<ImageDocument> listOfImageDocuments;

    public Appointment() {
        this.listOfStaff = new ArrayList<>();
        this.listOfImageDocuments = new ArrayList<>();
    }


    public Appointment(String description,
//        LocalDateTime actualDateTime,
        LocalDateTime bookedDateTime,
        PriorityEnum priorityEnum, Patient patient, Department department) {
        this();
        this.description = description;
//        this.actualDateTime = actualDateTime;
        this.actualDateTime = null;
        this.bookedDateTime = bookedDateTime;
        this.priorityEnum = priorityEnum;
        this.patient = patient;
        this.department = department;
    }
}
