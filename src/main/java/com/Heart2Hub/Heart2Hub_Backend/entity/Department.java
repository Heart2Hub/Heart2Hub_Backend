package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Size(max = 100)
    @NotNull
    private String departmentName;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "department")
    private List<SubDepartment> listOfSubDepartments;

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<Staff> listOfStaff;

    public Department() {
        this.listOfSubDepartments = List.of();
        this.listOfStaff = List.of();
    }

    public Department(String departmentName) {
        this();
        this.departmentName = departmentName;
    }
}
