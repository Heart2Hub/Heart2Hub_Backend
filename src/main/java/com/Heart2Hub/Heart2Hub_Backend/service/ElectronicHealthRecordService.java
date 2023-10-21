package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.dto.NehrDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateAppointmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateNextOfKinRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreatePrescriptionRecordException;
import com.Heart2Hub.Heart2Hub_Backend.mapper.NehrMapper;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ElectronicHealthRecordService {

    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    private final NextOfKinRecordRepository nextOfKinRecordRepository;
    private final PrescriptionRecordRepository prescriptionRecordRepository;
    private final ProblemRecordRepository problemRecordRepository;
    private final MedicalHistoryRecordRepository medicalHistoryRecordRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public ElectronicHealthRecordService(AppointmentRepository appointmentRepository, DepartmentRepository departmentRepository, NextOfKinRecordRepository nextOfKinRecordRepository, PrescriptionRecordRepository prescriptionRecordRepository, ProblemRecordRepository problemRecordRepository, MedicalHistoryRecordRepository medicalHistoryRecordRepository, PatientRepository patientRepository, StaffRepository staffRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
        this.nextOfKinRecordRepository = nextOfKinRecordRepository;
        this.prescriptionRecordRepository = prescriptionRecordRepository;
        this.problemRecordRepository = problemRecordRepository;
        this.medicalHistoryRecordRepository = medicalHistoryRecordRepository;
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

    public ElectronicHealthRecord updateCascadeElectronicHealthRecord(Long electronicHealthRecordId, ElectronicHealthRecord newElectronicHealthRecord) throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {

                // Basic EHR Information

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

                // Subsidies
                // TO-DO: Do the same for subsidies.

                // Past Admissions
                // TO-DO: Do the same for past admissions.

                // Past Appointments
                for (Appointment pastAppointment : newElectronicHealthRecord.getListOfPastAppointments()) {
                    try {
                        // For update (only DTO fields)
                        Optional<Appointment> pastAppointmentToUpdateOptional = appointmentRepository.findByAppointmentNehrId(pastAppointment.getAppointmentNehrId());
                        if (pastAppointmentToUpdateOptional.isPresent()) {
                            Appointment pastAppointmentToUpdate = pastAppointmentToUpdateOptional.get();

                            pastAppointmentToUpdate.setDescription(pastAppointment.getDescription());
                            pastAppointmentToUpdate.setComments(pastAppointment.getComments());
                            pastAppointmentToUpdate.setActualDateTime(pastAppointment.getActualDateTime());
                            pastAppointmentToUpdate.setBookedDateTime(pastAppointment.getBookedDateTime());

                            // Department
                            Optional<Department> departmentToUpdateOptional = departmentRepository.findByUnitNehrId(pastAppointment.getDepartment().getUnitNehrId());
                            if (departmentToUpdateOptional.isPresent()) {
                                // For update department? (only DTO fields)
                                Department departmentToUpdate = departmentToUpdateOptional.get();

                                departmentToUpdate.setName(pastAppointment.getDepartment().getName());

                                pastAppointmentToUpdate.setDepartment(departmentToUpdate);
                            } else {
                                // For create department? (only DTO fields)
                                pastAppointmentToUpdate.setDepartment(pastAppointment.getDepartment());
                                departmentRepository.save(pastAppointment.getDepartment());
                            }

                            // Staff
                            for (Staff staff : pastAppointment.getListOfStaff()) {
                                Optional<Staff> staffToUpdateOptional = staffRepository.findByStaffNehrId(staff.getStaffNehrId());
                                if (staffToUpdateOptional.isPresent()) {
                                    // For update staff? (only DTO fields)
                                    Staff staffToUpdate = staffToUpdateOptional.get();

                                    staffToUpdate.setFirstname(staff.getFirstname());
                                    staffToUpdate.setLastname(staff.getLastname());
                                    staffToUpdate.setStaffRoleEnum(staff.getStaffRoleEnum());

                                    staffRepository.save(staffToUpdate);
                                } else {
                                    // For create staff?
                                    pastAppointmentToUpdate.getListOfStaff().add(staff);
                                    staffRepository.save(staff);
                                }
                            }
                            // For delete staff
                            for (Staff existingStaff : pastAppointmentToUpdate.getListOfStaff()) {
                                if (!pastAppointment.getListOfStaff().contains(existingStaff)) {
                                    pastAppointmentToUpdate.getListOfStaff().remove(existingStaff);
                                }
                            }

                            appointmentRepository.save(pastAppointmentToUpdate);
                        } else {
                            // For create
                            existingElectronicHealthRecord.getListOfPastAppointments().add(pastAppointment);
                            appointmentRepository.save(pastAppointment);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreateAppointmentException(ex.getMessage());
                    }
                }
                // For delete
                for (Appointment existingAppointment : existingElectronicHealthRecord.getListOfPastAppointments()) {
                    if (!newElectronicHealthRecord.getListOfMedicalHistoryRecords().stream()
                            .anyMatch(medicalHistoryRecord ->
                                    medicalHistoryRecord.getMedicalHistoryRecordNehrId().equals(existingAppointment.getAppointmentNehrId()))) {
                        appointmentRepository.delete(existingAppointment);
                        existingElectronicHealthRecord.getListOfPastAppointments().remove(existingAppointment);
                    }
                }

                // Next of Kin Records
                for (NextOfKinRecord nextOfKinRecord : newElectronicHealthRecord.getListOfNextOfKinRecords()) {
                    try {
                        // For update (only DTO fields)
                        Optional<NextOfKinRecord> nextOfKinRecordToUpdateOptional = nextOfKinRecordRepository.findByNextOfKinRecordNehrId(nextOfKinRecord.getNextOfKinRecordNehrId());
                        if (nextOfKinRecordToUpdateOptional.isPresent()) {
                            NextOfKinRecord nextOfKinRecordToUpdate = nextOfKinRecordToUpdateOptional.get();

                            nextOfKinRecordToUpdate.setNric(nextOfKinRecord.getNric());
                            nextOfKinRecordToUpdate.setRelationship(nextOfKinRecord.getRelationship());

                            nextOfKinRecordRepository.save(nextOfKinRecordToUpdate);
                        } else {
                            // For create (only DTO fields)
                            existingElectronicHealthRecord.getListOfNextOfKinRecords().add(nextOfKinRecord);
                            nextOfKinRecordRepository.save(nextOfKinRecord);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreateNextOfKinRecordException(ex.getMessage());
                    }
                }
                // For delete
                for (NextOfKinRecord existingNextOfKinRecord : existingElectronicHealthRecord.getListOfNextOfKinRecords()) {
                    if (!newElectronicHealthRecord.getListOfMedicalHistoryRecords().stream()
                            .anyMatch(medicalHistoryRecord ->
                                    medicalHistoryRecord.getMedicalHistoryRecordNehrId().equals(existingNextOfKinRecord.getNextOfKinRecordNehrId()))) {
                        nextOfKinRecordRepository.delete(existingNextOfKinRecord);
                        existingElectronicHealthRecord.getListOfNextOfKinRecords().remove(existingNextOfKinRecord);
                    }
                }

                // Prescription Records
                for (PrescriptionRecord prescriptionRecord : newElectronicHealthRecord.getListOfPrescriptionRecords()) {
                    try {
                        // For update (only DTO fields)
                        Optional<PrescriptionRecord> prescriptionRecordToUpdateOptional = prescriptionRecordRepository.findByPrescriptionRecordNehrId(prescriptionRecord.getPrescriptionRecordNehrId());
                        if (prescriptionRecordToUpdateOptional.isPresent()) {
                            PrescriptionRecord prescriptionRecordRecordToUpdate = prescriptionRecordToUpdateOptional.get();

                            prescriptionRecordRecordToUpdate.setCreatedDate(prescriptionRecord.getCreatedDate());
                            prescriptionRecordRecordToUpdate.setMedicationName(prescriptionRecord.getMedicationName());
                            prescriptionRecordRecordToUpdate.setMedicationQuantity(prescriptionRecord.getMedicationQuantity());
                            prescriptionRecordRecordToUpdate.setDosage(prescriptionRecord.getDosage());
                            prescriptionRecordRecordToUpdate.setDescription(prescriptionRecord.getDescription());
                            prescriptionRecordRecordToUpdate.setComments(prescriptionRecord.getComments());
                            prescriptionRecordRecordToUpdate.setPrescribedBy(prescriptionRecord.getPrescribedBy());
                            prescriptionRecordRecordToUpdate.setPrescriptionStatusEnum(prescriptionRecord.getPrescriptionStatusEnum());

                            prescriptionRecordRepository.save(prescriptionRecordRecordToUpdate);
                        } else {
                            // For create (only DTO fields)
                            existingElectronicHealthRecord.getListOfPrescriptionRecords().add(prescriptionRecord);
                            prescriptionRecordRepository.save(prescriptionRecord);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreatePrescriptionRecordException(ex.getMessage());
                    }
                }
                // For delete
                for (PrescriptionRecord existingPrescriptionRecord : existingElectronicHealthRecord.getListOfPrescriptionRecords()) {
                    if (!newElectronicHealthRecord.getListOfMedicalHistoryRecords().stream()
                            .anyMatch(medicalHistoryRecord ->
                                    medicalHistoryRecord.getMedicalHistoryRecordNehrId().equals(existingPrescriptionRecord.getPrescriptionRecordNehrId()))) {
                        prescriptionRecordRepository.delete(existingPrescriptionRecord);
                        existingElectronicHealthRecord.getListOfPrescriptionRecords().remove(existingPrescriptionRecord);
                    }
                }

                // Problem Records
                for (ProblemRecord problemRecord : newElectronicHealthRecord.getListOfProblemRecords()) {
                    try {
                        // For update (only DTO fields)
                        Optional<ProblemRecord> problemRecordToUpdateOptional = problemRecordRepository.findByProblemRecordNehrId(problemRecord.getProblemRecordNehrId());
                        if (problemRecordToUpdateOptional.isPresent()) {
                            ProblemRecord problemRecordToUpdate = problemRecordToUpdateOptional.get();

                            problemRecordToUpdate.setCreatedDate(problemRecord.getCreatedDate());
                            problemRecordToUpdate.setDescription(problemRecord.getDescription());
                            problemRecordToUpdate.setCreatedBy(problemRecord.getCreatedBy());
                            problemRecordToUpdate.setPriorityEnum(problemRecord.getPriorityEnum());
                            problemRecordToUpdate.setProblemTypeEnum(problemRecord.getProblemTypeEnum());

                            problemRecordRepository.save(problemRecordToUpdate);
                        } else {
                            // For create (only DTO fields)
                            existingElectronicHealthRecord.getListOfProblemRecords().add(problemRecord);
                            problemRecordRepository.save(problemRecord);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreateAppointmentException(ex.getMessage());
                    }
                }
                // For delete
                for (ProblemRecord existingProblemRecord : existingElectronicHealthRecord.getListOfProblemRecords()) {
                    if (!newElectronicHealthRecord.getListOfMedicalHistoryRecords().stream()
                            .anyMatch(medicalHistoryRecord ->
                                    medicalHistoryRecord.getMedicalHistoryRecordNehrId().equals(existingProblemRecord.getProblemRecordNehrId()))) {
                        problemRecordRepository.delete(existingProblemRecord);
                        existingElectronicHealthRecord.getListOfProblemRecords().remove(existingProblemRecord);
                    }
                }

                // Medical History Records
                for (MedicalHistoryRecord medicalHistoryRecord : newElectronicHealthRecord.getListOfMedicalHistoryRecords()) {
                    try {
                        // For update (only DTO fields)
                        Optional<MedicalHistoryRecord> medicalHistoryRecordToUpdateOptional = medicalHistoryRecordRepository.findByMedicalHistoryRecordNehrId(medicalHistoryRecord.getMedicalHistoryRecordNehrId());
                        if (medicalHistoryRecordToUpdateOptional.isPresent()) {
                            MedicalHistoryRecord medicalHistoryRecordToUpdate = medicalHistoryRecordToUpdateOptional.get();

                            medicalHistoryRecordToUpdate.setCreatedDate(medicalHistoryRecord.getCreatedDate());
                            medicalHistoryRecordToUpdate.setDescription(medicalHistoryRecord.getDescription());
                            medicalHistoryRecordToUpdate.setCreatedBy(medicalHistoryRecord.getCreatedBy());
                            medicalHistoryRecordToUpdate.setResolvedDate(medicalHistoryRecord.getResolvedDate());
                            medicalHistoryRecordToUpdate.setPriorityEnum(medicalHistoryRecord.getPriorityEnum());
                            medicalHistoryRecordToUpdate.setProblemTypeEnum(medicalHistoryRecord.getProblemTypeEnum());

                            medicalHistoryRecordRepository.save(medicalHistoryRecordToUpdate);
                        } else {
                            // For create (only DTO fields)
                            existingElectronicHealthRecord.getListOfMedicalHistoryRecords().add(medicalHistoryRecord);
                            medicalHistoryRecordRepository.save(medicalHistoryRecord);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreateAppointmentException(ex.getMessage());
                    }
                }
                // For delete
                for (MedicalHistoryRecord existingMedicalHistoryRecord : existingElectronicHealthRecord.getListOfMedicalHistoryRecords()) {
                    if (!newElectronicHealthRecord.getListOfMedicalHistoryRecords().stream()
                            .anyMatch(medicalHistoryRecord -> medicalHistoryRecord.getMedicalHistoryRecordNehrId().equals(existingMedicalHistoryRecord.getMedicalHistoryRecordNehrId()))) {
                        medicalHistoryRecordRepository.delete(existingMedicalHistoryRecord);
                        existingElectronicHealthRecord.getListOfMedicalHistoryRecords().remove(existingMedicalHistoryRecord);
                    }
                }

                // Treatment Plan Records
                // TO-DO: Do the same for treatment plan records.

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
