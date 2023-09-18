package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByNameContainingIgnoreCase(String name);
}
