package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NextOfKinRecordRepository extends JpaRepository<NextOfKinRecord, Long> {
    Optional<NextOfKinRecord> findByNextOfKinRecordNehrId(UUID nextOfKinRecordNehrId);

}
