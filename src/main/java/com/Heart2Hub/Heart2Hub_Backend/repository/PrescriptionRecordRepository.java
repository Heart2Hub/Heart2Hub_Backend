package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRecordRepository extends JpaRepository<PrescriptionRecord, Long> {

}
