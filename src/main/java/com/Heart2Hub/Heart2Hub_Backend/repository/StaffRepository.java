package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;

import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff, Long> {

  Optional<Staff> findByUsername(String username);
  List<Staff> findByStaffRoleEnumAndUnitNameEqualsIgnoreCase(StaffRoleEnum staffRoleEnum, String name);


  Optional <List<Staff>> findAllByIsHead(Boolean b);
}
