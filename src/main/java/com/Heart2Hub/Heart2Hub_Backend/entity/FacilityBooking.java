package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "facilityBooking")
public class FacilityBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityBookingId;

    @NotNull
    @Future
    private LocalDateTime startDateTime;

    @NotNull
    @Future
    private LocalDateTime endDateTime;

    @Size(max = 200)
    private String comments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Shift shift;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    private Facility facility;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Admission admission;

    public FacilityBooking() {

    }

    public FacilityBooking(LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.startDateTime = startTime;
        this.endDateTime = endTime;
    }

    public FacilityBooking(LocalDateTime startTime, LocalDateTime endTime, String comments) {
        this();
        this.startDateTime = startTime;
        this.endDateTime = endTime;
        this.comments = comments;
    }
}
