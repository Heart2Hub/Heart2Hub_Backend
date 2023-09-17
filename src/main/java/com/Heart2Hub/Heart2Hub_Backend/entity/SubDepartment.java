package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "subDepartment")
public class SubDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subDepartmentId;

    @Size(max = 300)
    @NotNull
    private String subDepartmentName;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subDepartment")
    private List<Facility> listOfFacilities;


    @ManyToOne
    private Department department;

    public SubDepartment() {
        this.listOfFacilities = List.of();
    }

    public SubDepartment(String subDepartmentName) {
        this();
        this.subDepartmentName = subDepartmentName;
    }
}
