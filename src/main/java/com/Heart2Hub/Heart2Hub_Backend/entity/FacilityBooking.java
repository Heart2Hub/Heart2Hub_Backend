package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Size(max = 200)
    private String comments;

    @JsonBackReference(value="shift-fb")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Shift shift;

    @ManyToOne(fetch = FetchType.EAGER)
    private Facility facility;

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
