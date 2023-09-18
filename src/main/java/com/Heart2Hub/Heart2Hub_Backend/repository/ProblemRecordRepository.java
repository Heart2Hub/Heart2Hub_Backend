package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRecordRepository extends JpaRepository<ProblemRecord, Long> {

}
