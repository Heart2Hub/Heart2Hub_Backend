package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
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

    @JsonIgnore
    @OneToMany(mappedBy = "department")
    private List<SubDepartment> listOfSubDepartments;

    public Department() {
        this.listOfSubDepartments = new ArrayList<>();
    }

    public Department(String departmentName) {
        this();
        this.departmentName = departmentName;
    }
}
