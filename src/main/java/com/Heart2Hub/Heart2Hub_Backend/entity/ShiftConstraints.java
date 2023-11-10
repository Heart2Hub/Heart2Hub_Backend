package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "shiftConstraints")
public class ShiftConstraints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftConstraintsId;

    @NotNull
    @Column(unique = true)
    private UUID shiftConstraintsNehrId = UUID.randomUUID();

    @NotNull
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime startTime;

    @NotNull
    @JsonFormat(pattern="HH:mm:ss")
    private LocalTime endTime;

    @NotNull
    private Integer minPax;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StaffRoleEnum staffRoleEnum;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "facility_id", nullable = true)
    private Facility facility;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "ward_id", nullable = true)
    private Ward ward;
    public ShiftConstraints() {}

    public ShiftConstraints(LocalTime startTime, LocalTime endTime, Integer minPax, StaffRoleEnum staffRoleEnum) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.minPax = minPax;
        this.staffRoleEnum = staffRoleEnum;
    }
}
