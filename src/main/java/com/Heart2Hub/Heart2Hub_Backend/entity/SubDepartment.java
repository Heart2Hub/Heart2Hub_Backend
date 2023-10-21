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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "subDepartment")
public class SubDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subDepartmentId;

    @NotNull
    @Column(unique = true)
    private UUID subDepartmentNehrId = UUID.randomUUID();

    @NotNull
    private String name;

    @Size(max = 300)
    @NotNull
    private String description;

//    @JsonIgnore
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subDepartment")
//    private List<Facility> listOfFacilities;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public SubDepartment() {
//        this.listOfFacilities = new ArrayList<>();
    }

    public SubDepartment(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
}
