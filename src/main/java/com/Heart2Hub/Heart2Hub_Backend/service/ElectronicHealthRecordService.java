package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.dto.NehrDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.mapper.NehrMapper;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ElectronicHealthRecordService {

    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public ElectronicHealthRecordService(PatientRepository patientRepository, StaffRepository staffRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    public ElectronicHealthRecord getElectronicHealthRecordByIdAndDateOfBirth(Long electronicHealthRecordId, LocalDateTime dateOfBirth) throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {
                ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordOptional.get();
                if (!dateOfBirth.equals(electronicHealthRecord.getDateOfBirth())) {
                    throw new ElectronicHealthRecordNotFoundException("Date of Birth is invalid");
                } else {
                    return electronicHealthRecord;
                }
            } else {
                throw new ElectronicHealthRecordNotFoundException("Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
        }
    }

    public List<ElectronicHealthRecord> getAllElectronicHealthRecords() {
        return electronicHealthRecordRepository.findAll();
    }

    public ElectronicHealthRecord getElectronicHealthRecordByUsername(String username) throws ElectronicHealthRecordNotFoundException {
        return electronicHealthRecordRepository.findByPatientUsername(username).orElseThrow(() -> new ElectronicHealthRecordNotFoundException("Electronic Health Record does not exist for " + username));
    }

    public ElectronicHealthRecord getNehrRecordByNric(String nric){
        try {
            final String uri = "http://localhost:3002/records/" + nric;
            RestTemplate restTemplate = new RestTemplate();
            NehrDTO result = restTemplate.getForObject(uri, NehrDTO.class);
            NehrMapper nehrMapper = new NehrMapper();
            ElectronicHealthRecord toReturn = nehrMapper.convertToEntity(result);
            return toReturn;
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            return null;
        }
    }

    public ElectronicHealthRecord updateElectronicHealthRecord(Long electronicHealthRecordId, ElectronicHealthRecord newElectronicHealthRecord) throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {
                ElectronicHealthRecord existingElectronicHealthRecord = electronicHealthRecordOptional.get();
                existingElectronicHealthRecord.setFirstName(newElectronicHealthRecord.getFirstName());
                existingElectronicHealthRecord.setLastName(newElectronicHealthRecord.getLastName());
                existingElectronicHealthRecord.setSex(newElectronicHealthRecord.getSex());
                existingElectronicHealthRecord.setDateOfBirth(newElectronicHealthRecord.getDateOfBirth());
                existingElectronicHealthRecord.setPlaceOfBirth(newElectronicHealthRecord.getPlaceOfBirth());
                existingElectronicHealthRecord.setNationality(newElectronicHealthRecord.getNationality());
                existingElectronicHealthRecord.setRace(newElectronicHealthRecord.getRace());
                existingElectronicHealthRecord.setAddress(newElectronicHealthRecord.getAddress());
                existingElectronicHealthRecord.setContactNumber(newElectronicHealthRecord.getContactNumber());

                // Delete all and recreate (May screw with Id order). Cannot update because no Id unless check contents (May be flawed?).

                // Subsidies
                // TO-DO: Do the same for subsidies.

                // Past Admissions
                // TO-DO: Do the same for past admissions.

                // Past Appointments
//                if (newElectronicHealthRecord.getListOfPastAppointments() != null) {
//                    appointmentService.deleteAllPastAppointmentsFromElectronicHealthRecord(existingElectronicHealthRecord.getElectronicHealthRecordId());
//                    for (Appointment appointment : newElectronicHealthRecord.getListOfPastAppointments()) {
//                        appointmentService.createNewAppointment(appointment.getDescription(), String.valueOf(appointment.getBookedDateTime()),appointment.getPriorityEnum().toString(), newElectronicHealthRecord.getNric(),appointment.getDepartment().getName());
//                    }
//                }

                // Next of Kin Records
//                if (newElectronicHealthRecord.getListOfNextOfKinRecords() != null) {
//                    nextOfKinRecordService.deleteAllNextOfKinRecordsFromElectronicHealthRecord(existingElectronicHealthRecord.getElectronicHealthRecordId());
//                    for (NextOfKinRecord nextOfKinRecord : newElectronicHealthRecord.getListOfNextOfKinRecords()) {
//                        nextOfKinRecordService.createNextOfKinRecord(newElectronicHealthRecord.getElectronicHealthRecordId(),nextOfKinRecord);
//                    }
//                }

                // Prescription Records
//                if (newElectronicHealthRecord.getListOfPrescriptionRecords() != null) {
//                    prescriptionRecordService.deleteAllPrescriptionRecordsFromElectronicHealthRecord(existingElectronicHealthRecord.getElectronicHealthRecordId());
//                    for (PrescriptionRecord prescriptionRecord : newElectronicHealthRecord.getListOfPrescriptionRecords()) {
//                        prescriptionRecordService.createPrescriptionRecord(newElectronicHealthRecord.getElectronicHealthRecordId(),prescriptionRecord);
//                    }
//                }

                // Problem Records
//                if (newElectronicHealthRecord.getListOfProblemRecords() != null) {
//                    problemRecordService.deleteAllProblemRecordsFromElectronicHealthRecord(existingElectronicHealthRecord.getElectronicHealthRecordId());
//                    for (ProblemRecord problemRecord : newElectronicHealthRecord.getListOfProblemRecords()) {
//                        problemRecordService.createProblemRecord(newElectronicHealthRecord.getElectronicHealthRecordId(),problemRecord);
//                    }
//                }

                // Medical History Records
//                if (newElectronicHealthRecord.getListOfMedicalHistoryRecords() != null) {
//                    medicalHistoryRecordService.deleteAllMedicalHistoryRecordsFromElectronicHealthRecord(existingElectronicHealthRecord.getElectronicHealthRecordId());
//                    for (MedicalHistoryRecord medicalHistoryRecord : newElectronicHealthRecord.getListOfMedicalHistoryRecords()) {
//                        medicalHistoryRecordService.createMedicalHistoryRecord(newElectronicHealthRecord.getElectronicHealthRecordId(), medicalHistoryRecord);
//                    }
//                }

                // Treatment Plan Records
//                if (newElectronicHealthRecord.getListOfTreatmentPlanRecords() != null) {
//                    treatmentPlanRecordService.deleteAllTreatmentPlanRecordsFromElectronicHealthRecord(existingElectronicHealthRecord.getElectronicHealthRecordId());
//                    for (TreatmentPlanRecord treatmentPlanRecord : newElectronicHealthRecord.getListOfTreatmentPlanRecords()) {
//                        treatmentPlanRecordService.createTreatmentPlanRecord(newElectronicHealthRecord.getElectronicHealthRecordId(), treatmentPlanRecord);
//                    }
//                }

                electronicHealthRecordRepository.save(existingElectronicHealthRecord);
                return existingElectronicHealthRecord;
            } else {
                throw new ElectronicHealthRecordNotFoundException("Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
        }
    }

    public ElectronicHealthRecord findByNric(String nric) {
        try {
            return electronicHealthRecordRepository.findByNricIgnoreCase(nric).get();
        } catch (Exception ex) {
            throw new ElectronicHealthRecordNotFoundException("Invalid NRIC");
        }
    }

}
