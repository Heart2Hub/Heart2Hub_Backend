package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "shift")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftId;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    @Size(max = 200)
    private String comments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Staff staff;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shift")
    private List<FacilityBooking> listOfFacilityBookings;

    public Shift() {
        this.listOfFacilityBookings = List.of();
    }

    public Shift(LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Shift(LocalDateTime startTime, LocalDateTime endTime, String comments) {
        this();
        this.startTime = startTime;
        this.endTime = endTime;
        this.comments = comments;
    }
}
