package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "ward")
public class Ward extends SubDepartment {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private WardClass wardClass;

    public Ward() {
    }


}
