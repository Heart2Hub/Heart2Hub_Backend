package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByUnitNehrId(UUID unitNehrId);

    List<Department> findByNameContainingIgnoreCase(String name);

    Optional<Department> findByName(String name);
}
