package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Subsidy;
import com.Heart2Hub.Heart2Hub_Backend.entity.TreatmentPlanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TreatmentPlanRecordRepository extends JpaRepository<TreatmentPlanRecord, Long> {

    Optional<TreatmentPlanRecord> findByTreatmentPlanRecordNehrId(UUID treatmentPlanRecordNehrId);

}
