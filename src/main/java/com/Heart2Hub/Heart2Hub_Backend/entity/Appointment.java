package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    private Staff currentAssignedStaff;

    @NotNull
    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    private Patient patient;

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
