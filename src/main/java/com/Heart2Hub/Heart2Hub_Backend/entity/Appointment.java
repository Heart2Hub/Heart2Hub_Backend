package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size(max = 200)
    private String description;

    @Size(max = 200)
    private String comments;

    @NotNull
    @Future
    private LocalDateTime actualDateTime;

    @NotNull
    @Future
    private LocalDateTime bookedDateTime;

    @NotNull
    private Time estimatedDuration;

    @NotNull
    private Boolean arrived = false;

    @NotNull
    private Time elapsedTime;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PriorityEnum priorityEnum;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Staff> listOfStaff;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "staff_id", nullable = true)
    private Staff currentAssignedStaff;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "appointment_id",nullable = true)
    private List<ImageDocument> listOfImageDocuments;

    public Appointment() {
        this.listOfStaff = List.of();
    }

    public Appointment(LocalDateTime actualDateTime, LocalDateTime bookedDateTime) {
        this();
        this.actualDateTime = actualDateTime;
        this.bookedDateTime = bookedDateTime;
    }

    public Appointment(String description, String comments, LocalDateTime actualDateTime, LocalDateTime bookedDateTime) {
        this();
        this.description = description;
        this.comments = comments;
        this.actualDateTime = actualDateTime;
        this.bookedDateTime = bookedDateTime;
    }
}
