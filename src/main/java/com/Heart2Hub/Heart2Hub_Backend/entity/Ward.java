package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private WardClass wardClass;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<WardAvailability> listOfWardAvailabilities;

    @JsonIgnore
    @OneToMany(mappedBy = "ward")
    private List<Admission> listOfAdmissions;

    public Ward() {
        this.listOfWardAvailabilities = new ArrayList<>();
        this.listOfAdmissions = new ArrayList<>();
    }

    public Ward(String name, String location, Integer capacity) {
        super(name);
        this.listOfWardAvailabilities = new ArrayList<>();
        this.listOfAdmissions = new ArrayList<>();
        this.location = location;
        this.capacity = capacity;
    }
}
