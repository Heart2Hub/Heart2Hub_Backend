package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.TreatmentPlanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentPlanRecordRepository extends JpaRepository<TreatmentPlanRecord, Long> {

}
