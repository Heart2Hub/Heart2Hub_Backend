package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.InpatientTreatment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InpatientTreatmentRepository extends JpaRepository<InpatientTreatment, Long> {

    List<InpatientTreatment> findByStaffAndStartDateBetween(Staff staff, LocalDateTime startDate, LocalDateTime endDate);

}
