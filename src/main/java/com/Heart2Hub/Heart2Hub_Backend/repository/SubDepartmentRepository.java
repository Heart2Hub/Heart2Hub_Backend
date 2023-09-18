package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubDepartmentRepository extends JpaRepository<SubDepartment, Long> {
    List<SubDepartment> findByNameContainingIgnoreCase(String name);
}
