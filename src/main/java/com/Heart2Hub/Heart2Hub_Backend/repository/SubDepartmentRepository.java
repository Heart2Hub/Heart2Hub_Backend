package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubDepartmentRepository extends JpaRepository<SubDepartment, Long> {

    Optional<SubDepartment> findBySubDepartmentName(String subDepartmentName);
}
