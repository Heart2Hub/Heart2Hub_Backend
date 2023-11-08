package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicalHistoryRecordRepository extends JpaRepository<MedicalHistoryRecord, Long> {
    Optional<MedicalHistoryRecord> findByMedicalHistoryRecordNehrId(UUID medicalHistoryRecordNehrId);

}
