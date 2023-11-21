package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.Subsidy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    Optional<Admission> findByPatient(Patient patient);

    Optional<Admission> findByAdmissionNehrId(UUID admissionNehrId);
}
