package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ElectronicHealthRecordRepository extends JpaRepository<ElectronicHealthRecord, Long> {

    Optional<ElectronicHealthRecord> findByNricIgnoreCase(String nric);
    Optional<ElectronicHealthRecord> findByNric(String nric);

    List<ElectronicHealthRecord> findByFirstNameContainsIgnoreCase(String firstName);
    List<ElectronicHealthRecord> findByLastNameContainsIgnoreCase(String lastName);

    Optional<ElectronicHealthRecord> findByPatientUsername(String username);

    Optional<ElectronicHealthRecord> findByListOfTreatmentPlanRecords_TreatmentPlanRecordId(Long treatmentPlanRecordId);

    Optional<ElectronicHealthRecord> findByListOfPastAppointments_AppointmentId(Long appointmentId);

}
