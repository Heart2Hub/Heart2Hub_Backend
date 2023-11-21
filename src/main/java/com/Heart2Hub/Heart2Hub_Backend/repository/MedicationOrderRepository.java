package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicationOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationOrderRepository extends JpaRepository<MedicationOrder, Long> {
}
