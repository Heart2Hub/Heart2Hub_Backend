package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.dto.NehrDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.configuration.Heart2HubConfig;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.mapper.NehrMapper;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.http.*;
import org.springframework.security.core.parameters.P;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final SubsidyRepository subsidyRepository;
    private final TreatmentPlanRecordRepository treatmentPlanRecordRepository;
    private final AdmissionRepository admissionRepository;
    private final WardRepository wardRepository;
    private final WardClassRepository wardClassRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;
    private final PasswordEncoder passwordEncoder;
    private final Heart2HubConfig heart2HubConfig;

    public ElectronicHealthRecordService(AppointmentRepository appointmentRepository, DepartmentRepository departmentRepository, NextOfKinRecordRepository nextOfKinRecordRepository, PrescriptionRecordRepository prescriptionRecordRepository, ProblemRecordRepository problemRecordRepository, MedicalHistoryRecordRepository medicalHistoryRecordRepository, SubsidyRepository subsidyRepository, TreatmentPlanRecordRepository treatmentPlanRecordRepository, AdmissionRepository admissionRepository, WardRepository wardRepository, WardClassRepository wardClassRepository, PatientRepository patientRepository, StaffRepository staffRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository, PasswordEncoder passwordEncoder, Heart2HubConfig heart2HubConfig) {
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
        this.nextOfKinRecordRepository = nextOfKinRecordRepository;
        this.prescriptionRecordRepository = prescriptionRecordRepository;
        this.problemRecordRepository = problemRecordRepository;
        this.medicalHistoryRecordRepository = medicalHistoryRecordRepository;
        this.subsidyRepository = subsidyRepository;
        this.treatmentPlanRecordRepository = treatmentPlanRecordRepository;
        this.admissionRepository = admissionRepository;
        this.wardRepository = wardRepository;
        this.wardClassRepository = wardClassRepository;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
        this.passwordEncoder = passwordEncoder;
        this.heart2HubConfig = heart2HubConfig;
    }

    public ElectronicHealthRecord getElectronicHealthRecordByIdAndDateOfBirth(
            Long electronicHealthRecordId, LocalDateTime dateOfBirth)
            throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
                    electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {
                ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordOptional.get();
                if (!dateOfBirth.equals(electronicHealthRecord.getDateOfBirth())) {
                    throw new ElectronicHealthRecordNotFoundException("Date of Birth is invalid");
                } else {
                    return electronicHealthRecord;
                }
            } else {
                throw new ElectronicHealthRecordNotFoundException(
                        "Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
        }
    }

    public ElectronicHealthRecord getElectronicHealthRecordById(
            Long electronicHealthRecordId) {
        Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
                electronicHealthRecordId);
        if (electronicHealthRecordOptional.isPresent()) {
            return electronicHealthRecordOptional.get();
        } else {
            throw new ElectronicHealthRecordNotFoundException(
                    "Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
        }
    }

    public List<ElectronicHealthRecord> getAllElectronicHealthRecords() {
        return electronicHealthRecordRepository.findAll();
    }

    public ElectronicHealthRecord getElectronicHealthRecordByUsername(String username)
            throws ElectronicHealthRecordNotFoundException {
        return electronicHealthRecordRepository.findByPatientUsername(username).orElseThrow(
                () -> new ElectronicHealthRecordNotFoundException(
                        "Electronic Health Record does not exist for " + username));
    }

//    public ElectronicHealthRecord getNehrRecordByNric(String nric) {
//        try {
//            final String uri = "http://localhost:3002/records/" + nric;
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("encoded-message", encodeSecretMessage());
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//            RestTemplate restTemplate = new RestTemplate();
//            ElectronicHealthRecord result = restTemplate.exchange(uri, HttpMethod.GET, entity,
//                    ElectronicHealthRecord.class).getBody();
//            return result;
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//            return null;
//        }
//    }

    public ElectronicHealthRecord getNehrRecordByNric(String nric) {
        try {
            final String uri = "http://localhost:3002/records/" + nric;
            HttpHeaders headers = new HttpHeaders();
            headers.set("encoded-message", encodeSecretMessage());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            RestTemplate restTemplate = new RestTemplate();
            System.out.println("ASDHBASDH");
            ResponseEntity<NehrDTO> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, NehrDTO.class);
            // NehrDTO result = restTemplate.getForObject(uri, NehrDTO.class);
            System.out.println("ASDHBASDH" + response);
            NehrDTO result = response.getBody();
            NehrMapper nehrMapper = new NehrMapper();
            ElectronicHealthRecord toReturn = nehrMapper.convertToEntity(result);
            return toReturn;
        } catch (Exception ex) {
            System.out.print("error ajkshdajkhsd" + ex.getMessage());
            return null;
        }
    }

    public String encodeSecretMessage() {
        try {
            String SECRET_MESSAGE = heart2HubConfig.getJwt().getSecretMessage();
            String SECRET_KEY = heart2HubConfig.getJwt().getSecretKey();

            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");
            hmac.init(secretKeySpec);

            byte[] hash = hmac.doFinal(SECRET_MESSAGE.getBytes(StandardCharsets.UTF_8));
            System.out.println("SECRET HERE: " +  Base64.getEncoder().encodeToString(hash));
            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC", e);
        }
    }

    public ElectronicHealthRecord updateElectronicHealthRecord(Long electronicHealthRecordId,
                                                               ElectronicHealthRecord newElectronicHealthRecord)
            throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(
                    electronicHealthRecordId);

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
                existingElectronicHealthRecord.setContactNumber(
                        newElectronicHealthRecord.getContactNumber());

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
                for (Subsidy subsidy : newElectronicHealthRecord.getListOfSubsidies()) {
                    try {
                        // For update (only DTO fields)
                        Optional<Subsidy> subsidyToUpdateOptional = subsidyRepository.findBySubsidyNehrId(subsidy.getSubsidyNehrId());
                        if (subsidyToUpdateOptional.isPresent()) {
                            Subsidy subsidyToUpdate = subsidyToUpdateOptional.get();

                            subsidyToUpdate.setSubsidyRate(subsidy.getSubsidyRate());
                            subsidyToUpdate.setItemTypeEnum(subsidy.getItemTypeEnum());
                            subsidyToUpdate.setMinDOB(subsidy.getMinDOB());
                            subsidyToUpdate.setSex(subsidy.getSex());
                            subsidyToUpdate.setRace(subsidy.getRace());
                            subsidyToUpdate.setNationality(subsidy.getNationality());
                            subsidyToUpdate.setSubsidyName(subsidy.getSubsidyName());
                            subsidyToUpdate.setSubsidyDescription(subsidy.getSubsidyDescription());

                            subsidyRepository.save(subsidyToUpdate);
                        } else {
                            // For create (only DTO fields)
                            existingElectronicHealthRecord.getListOfSubsidies().add(subsidy);
                            subsidyRepository.save(subsidy);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreateElectronicHealthRecordException(ex.getMessage());
                    }
                }
                // For delete
                for (Subsidy existingSubsidy : existingElectronicHealthRecord.getListOfSubsidies()) {
                    if (!newElectronicHealthRecord.getListOfSubsidies().stream()
                            .anyMatch(subsidy ->
                                    subsidy.getSubsidyNehrId().equals(existingSubsidy.getSubsidyNehrId()))) {
                        subsidyRepository.delete(existingSubsidy);
                        existingElectronicHealthRecord.getListOfSubsidies().remove(existingSubsidy);
                    }
                }

                // Past Admissions
                for (Admission pastAdmission : newElectronicHealthRecord.getListOfPastAdmissions()) {
                    try {
                        // For update (only DTO fields)
                        Optional<Admission> pastAdmissionToUpdateOptional = admissionRepository.findByAdmissionNehrId(pastAdmission.getAdmissionNehrId());
                        if (pastAdmissionToUpdateOptional.isPresent()) {
                            Admission pastAdmissionToUpdate = pastAdmissionToUpdateOptional.get();

                            pastAdmissionToUpdate.setDuration(pastAdmission.getDuration());
                            pastAdmissionToUpdate.setAdmissionDateTime(pastAdmission.getAdmissionDateTime());
                            pastAdmissionToUpdate.setDischargeDateTime(pastAdmission.getDischargeDateTime());
                            pastAdmissionToUpdate.setComments(pastAdmission.getComments());

                            // Ward
                            Optional<Ward> wardToUpdateOptional = wardRepository.findByUnitNehrId(pastAdmission.getWard().getUnitNehrId());
                            if (wardToUpdateOptional.isPresent()) {
                                // For update ward? (only DTO fields)
                                Ward wardToUpdate = wardToUpdateOptional.get();

                                wardToUpdate.setName(pastAdmission.getWard().getName());
                                wardToUpdate.setLocation(pastAdmission.getWard().getLocation());

                                Optional<WardClass> wardClassToUpdateOptional = wardClassRepository.findByWardClassNehrId(wardToUpdate.getWardClass().getWardClassNehrId());
                                if (wardClassToUpdateOptional.isPresent()) {
                                    // For update ward class? (only DTO fields)
                                    WardClass wardClassToUpdate = wardClassToUpdateOptional.get();

                                    wardClassToUpdate.setWardClassName(pastAdmission.getWard().getWardClass().getWardClassName());

                                    wardClassRepository.save(wardClassToUpdate);
                                } else {
                                    // For create ward class? (only DTO fields)
                                    wardToUpdate.setWardClass(wardToUpdate.getWardClass());
                                    wardClassRepository.save(wardToUpdate.getWardClass());
                                }

                                wardRepository.save(wardToUpdate);
                            } else {
                                // For create ward? (only DTO fields)
                                pastAdmissionToUpdate.setWard(pastAdmission.getWard());
                                wardRepository.save(pastAdmission.getWard());
                            }

                            admissionRepository.save(pastAdmissionToUpdate);
                        } else {
                            // For create
                            existingElectronicHealthRecord.getListOfPastAdmissions().add(pastAdmission);
                            admissionRepository.save(pastAdmission);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreateElectronicHealthRecordException(ex.getMessage());
                    }
                }
                // For delete
                for (Admission existingAdmission : existingElectronicHealthRecord.getListOfPastAdmissions()) {
                    if (!newElectronicHealthRecord.getListOfPastAdmissions().stream()
                            .anyMatch(admission ->
                                    admission.getAdmissionNehrId().equals(existingAdmission.getAdmissionNehrId()))) {
                        admissionRepository.delete(existingAdmission);
                        existingElectronicHealthRecord.getListOfPastAdmissions().remove(existingAdmission);
                    }
                }

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

                                departmentRepository.save(departmentToUpdate);
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
                                if (!pastAppointment.getListOfStaff().stream()
                                        .anyMatch(staff ->
                                                staff.getStaffNehrId().equals(existingStaff.getStaffNehrId()))) {
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
                    if (!newElectronicHealthRecord.getListOfPastAppointments().stream()
                            .anyMatch(pastAppointment ->
                                    pastAppointment.getAppointmentNehrId().equals(existingAppointment.getAppointmentNehrId()))) {
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
                    if (!newElectronicHealthRecord.getListOfNextOfKinRecords().stream()
                            .anyMatch(nextOfKinRecord ->
                                    nextOfKinRecord.getNextOfKinRecordNehrId().equals(existingNextOfKinRecord.getNextOfKinRecordNehrId()))) {
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
                    if (!newElectronicHealthRecord.getListOfPrescriptionRecords().stream()
                            .anyMatch(prescriptionRecord ->
                                    prescriptionRecord.getPrescriptionRecordNehrId().equals(existingPrescriptionRecord.getPrescriptionRecordNehrId()))) {
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
                        throw new UnableToCreateElectronicHealthRecordException(ex.getMessage());
                    }
                }
                // For delete
                for (ProblemRecord existingProblemRecord : existingElectronicHealthRecord.getListOfProblemRecords()) {
                    if (!newElectronicHealthRecord.getListOfProblemRecords().stream()
                            .anyMatch(problemRecord ->
                                    problemRecord.getProblemRecordNehrId().equals(existingProblemRecord.getProblemRecordNehrId()))) {
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
                        throw new UnableToCreateElectronicHealthRecordException(ex.getMessage());
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
                for (TreatmentPlanRecord treatmentPlanRecord : newElectronicHealthRecord.getListOfTreatmentPlanRecords()) {
                    try {
                        // For update (only DTO fields)
                        Optional<TreatmentPlanRecord> treatmentPlanRecordToUpdateOptional = treatmentPlanRecordRepository.findByTreatmentPlanRecordNehrId(treatmentPlanRecord.getTreatmentPlanRecordNehrId());
                        if (treatmentPlanRecordToUpdateOptional.isPresent()) {
                            TreatmentPlanRecord treatmentPlanRecordToUpdate = treatmentPlanRecordToUpdateOptional.get();

                            treatmentPlanRecordToUpdate.setDescription(treatmentPlanRecord.getDescription());
//                            treatmentPlanRecordToUpdate.setPrimaryDoctor(treatmentPlanRecord.getPrimaryDoctor());
//                            treatmentPlanRecordToUpdate.setSecondaryDoctors(treatmentPlanRecord.getSecondaryDoctors());
                            treatmentPlanRecordToUpdate.setStartDate(treatmentPlanRecord.getStartDate());
                            treatmentPlanRecordToUpdate.setEndDate(treatmentPlanRecord.getEndDate());
                            treatmentPlanRecordToUpdate.setTreatmentPlanTypeEnum(treatmentPlanRecord.getTreatmentPlanTypeEnum());

                            treatmentPlanRecordRepository.save(treatmentPlanRecordToUpdate);
                        } else {
                            // For create (only DTO fields)
                            existingElectronicHealthRecord.getListOfTreatmentPlanRecords().add(treatmentPlanRecord);
                            treatmentPlanRecordRepository.save(treatmentPlanRecord);
                        }
                    } catch (Exception ex) {
                        throw new UnableToCreateElectronicHealthRecordException(ex.getMessage());
                    }
                }
                // For delete
                for (TreatmentPlanRecord existingTreatmentPlanRecord : existingElectronicHealthRecord.getListOfTreatmentPlanRecords()) {
                    if (!newElectronicHealthRecord.getListOfTreatmentPlanRecords().stream()
                            .anyMatch(treatmentPlanRecord -> treatmentPlanRecord.getTreatmentPlanRecordNehrId().equals(existingTreatmentPlanRecord.getTreatmentPlanRecordNehrId()))) {
                        treatmentPlanRecordRepository.delete(existingTreatmentPlanRecord);
                        existingElectronicHealthRecord.getListOfTreatmentPlanRecords().remove(existingTreatmentPlanRecord);
                    }
                }

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

    public ElectronicHealthRecord findEHRByTreatmentPlanId(Long treatmentPlanId) {
        return electronicHealthRecordRepository.findByListOfTreatmentPlanRecords_TreatmentPlanRecordId(
                treatmentPlanId).orElseThrow(
                () -> new ElectronicHealthRecordNotFoundException("No such EHR found for treatment plan"));
    }
}
