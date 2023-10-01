package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ShiftConstraintsRepository extends JpaRepository<ShiftConstraints, Long> {

    List<ShiftConstraints> findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndFacilityNameAndStaffRoleEnumEquals(LocalTime startTime, LocalTime endTime, String name, StaffRoleEnum staffRoleEnum);

    List<ShiftConstraints> findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndWardNameAndStaffRoleEnumEquals(LocalTime startTime, LocalTime endTime, String name, StaffRoleEnum staffRoleEnum);

    List<ShiftConstraints> findByStaffRoleEnumAndFacilityName(StaffRoleEnum staffRoleEnum, String name);

    List<ShiftConstraints> findByStaffRoleEnumAndWardName(StaffRoleEnum staffRoleEnum, String name);

    List<ShiftConstraints> findByFacility(Facility facility);

}
