package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "ward")
public class Ward extends Unit {
    @NotNull
    private String location;

    @Min(1)
    @Max(200)
    @NotNull
    private Integer capacity;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL,optional = false)
    @JoinColumn(name = "wardClass_id", nullable = false)
    private WardClass wardClass;

    @JsonManagedReference
    @OneToMany(mappedBy = "ward", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WardAvailability> listOfWardAvailabilities;

    @JsonManagedReference
    @OneToMany(mappedBy = "ward", fetch = FetchType.LAZY)
    private List<Admission> listOfAdmissions;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Admission> listOfCurrentDayAdmissions;

    public Ward() {
        this.listOfWardAvailabilities = new ArrayList<>();
        this.listOfAdmissions = new ArrayList<>();
        this.listOfCurrentDayAdmissions = new ArrayList<>();
    }

    public Ward(String name, String location, Integer capacity) {
        super(name);
        this.listOfWardAvailabilities = new ArrayList<>();
        this.listOfAdmissions = new ArrayList<>();
        this.listOfCurrentDayAdmissions = new ArrayList<>();
        this.location = location;
        this.capacity = capacity;
    }





//    public void createDefaultAdmissions() {
//        int bedsPerRoom = this.wardClass.getBedsPerRoom();
//        int numOfRooms = this.capacity / bedsPerRoom;
//        for (int i = 1; i <= numOfRooms; i++) {
//            for (int j = 1; j <= bedsPerRoom; j++) {
//                Admission admission = new Admission();
//                admission.setRoom(i);
//                admission.setBed(j);
//                this.listOfCurrentDayAdmissions.add(admission);
//            }
//        }
//    }
}
