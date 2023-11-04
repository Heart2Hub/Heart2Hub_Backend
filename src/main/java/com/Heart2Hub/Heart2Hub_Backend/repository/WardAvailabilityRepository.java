package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Ward;
import com.Heart2Hub.Heart2Hub_Backend.entity.WardAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WardAvailabilityRepository extends JpaRepository<WardAvailability, Long> {

    List<WardAvailability> findByWard(Ward ward);
    List<WardAvailability> findByWardAndDateBetween(Ward ward, LocalDateTime startDate, LocalDateTime endDate);


}
