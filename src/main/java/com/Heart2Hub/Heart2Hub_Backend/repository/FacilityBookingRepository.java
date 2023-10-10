package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FacilityBookingRepository extends JpaRepository<FacilityBooking, Long> {

    List<FacilityBooking> findAllByFacility(Facility facility);

    List<FacilityBooking> findFacilityBookingByStaffUsername(String staffUsername);

    List<FacilityBooking> findAllByStartDateTimeBetween(LocalDateTime shiftStart, LocalDateTime shiftEnd);
}
