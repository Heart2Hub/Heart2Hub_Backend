package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Ward;
import com.Heart2Hub.Heart2Hub_Backend.entity.WardClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WardRepository extends JpaRepository<Ward, Long> {

    List<Ward> findByNameContainingIgnoreCase(String name);

    List<Ward> findByWardClass(WardClass wardClass);

    Optional<Ward> findByUnitNehrId(UUID unitNehrId);
}
