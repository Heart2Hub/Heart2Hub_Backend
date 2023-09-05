package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "facility")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;

    @Size(max = 100)
    @NotNull
    private String name;

    @Size(max = 100)
    @NotNull
    private String location;

    @Size(max = 300)
    @NotNull
    private String description;

    @Size(max = 200)
    @NotNull
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FacilityStatusEnum facilityStatusEnum;

    @Enumerated(EnumType.STRING)
    @NotNull
    private FacilityTypeEnum facilityTypeEnum;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    private List<FacilityBooking> listOfFacilityBookings;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    private SubDepartment subDepartment;

    public Facility() {
        this.listOfFacilityBookings = List.of();
    }

    public Facility(String name, String location, String description, Integer capacity, FacilityStatusEnum facilityStatusEnum, FacilityTypeEnum facilityTypeEnum) {
        this();
        this.name = name;
        this.location = location;
        this.description = description;
        this.capacity = capacity;
        this.facilityStatusEnum = facilityStatusEnum;
        this.facilityTypeEnum = facilityTypeEnum;
    }
}
