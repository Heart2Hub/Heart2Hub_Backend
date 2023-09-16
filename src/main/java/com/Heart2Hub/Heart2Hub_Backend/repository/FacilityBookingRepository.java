package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityBookingRepository extends JpaRepository<FacilityBooking, Long> {
}
