package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ShiftConstraintsRepository extends JpaRepository<ShiftConstraints, Long> {

    List<ShiftConstraints> findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndStaffRoleEnumEquals(LocalTime startTime, LocalTime endTime, StaffRoleEnum staffRoleEnum);

    List<ShiftConstraints> findByStaffRoleEnum(StaffRoleEnum staffRoleEnum);
}
