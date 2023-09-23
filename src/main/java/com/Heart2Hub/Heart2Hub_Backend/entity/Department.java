package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "department")
public class Department extends Unit{


    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<Facility> listOfFacilities;

    public Department() {
        this.listOfFacilities = new ArrayList<>();
    }

    public Department(String name) {
        super(name);
        this.listOfFacilities = new ArrayList<>();
    }
}
