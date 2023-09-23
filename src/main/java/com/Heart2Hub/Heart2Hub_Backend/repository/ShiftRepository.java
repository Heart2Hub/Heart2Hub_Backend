package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

   List<Shift> findShiftsByStaff(Staff staff);

   List<Shift> findByStaffStaffRoleEnumAndStartTimeBetween(StaffRoleEnum staffRoleEnum, LocalDateTime start, LocalDateTime end);

   List<Shift> findByStaffUsernameAndStartTimeBetween(String username, LocalDateTime start, LocalDateTime end);
}
