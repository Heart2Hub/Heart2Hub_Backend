package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftPreference;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ShiftPreferenceRepository extends JpaRepository<ShiftPreference, Long> {


}
