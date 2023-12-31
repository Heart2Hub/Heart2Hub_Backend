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
import java.util.UUID;

@Entity
@Data
@Table(name = "facilityBooking")
public class FacilityBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityBookingId;

    @NotNull
    @Column(unique = true)
    private UUID facilityBookingNehrId = UUID.randomUUID();

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    @NotNull
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    @Size(max = 200)
    private String comments;

    private String staffUsername;

    @JsonBackReference(value="shift-fb")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    private Shift shift;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "facility_id", nullable = true)
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
