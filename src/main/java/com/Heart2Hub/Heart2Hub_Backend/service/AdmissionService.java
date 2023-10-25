package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final WardRepository wardRepository;
    private final WardAvailabilityRepository wardAvailabilityRepository;
    private final ElectronicHealthRecordService electronicHealthRecordService;


    public AdmissionService(AdmissionRepository admissionRepository, PatientRepository patientRepository, WardRepository wardRepository, WardAvailabilityRepository wardAvailabilityRepository, ElectronicHealthRecordService electronicHealthRecordService, StaffRepository staffRepository) {
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.wardRepository = wardRepository;
        this.wardAvailabilityRepository = wardAvailabilityRepository;
        this.electronicHealthRecordService = electronicHealthRecordService;
        this.staffRepository = staffRepository;
    }

    public List<Admission> getAllAdmissions() {
        return admissionRepository.findAll();
    }

    public Admission createAdmission(Integer duration, String reason, Long patientId, Long doctorId) throws PatientNotFoundException, StaffNotFoundException {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException("Patient not found"));

        Staff doctor = staffRepository.findById(doctorId).orElseThrow(() -> new StaffNotFoundException("Staff not found"));

        Admission newAdmission = new Admission(duration, reason, patient, doctor);

        patient.setAdmission(newAdmission);
        doctor.getListOfDoctorAdmissions().add(newAdmission);
        return admissionRepository.save(newAdmission);
    }

    public Admission scheduleAdmission(Long admissionId, Long firstWardAvailabilityId, String admissionString, String dischargeString) throws AdmissionNotFoundException, WardAvailabilityNotFoundException {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));

        LocalDateTime admissionDateTime = LocalDateTime.parse(admissionString);
        LocalDateTime dischargeDateTime = LocalDateTime.parse(dischargeString);
        admission.setAdmissionDateTime(admissionDateTime);
        admission.setDischargeDateTime(dischargeDateTime);

        Integer duration = admission.getDuration();

        WardAvailability wardAvailability = new WardAvailability();

        for (int i = 0; i < duration; i++) {
            wardAvailability = wardAvailabilityRepository.findById(firstWardAvailabilityId).orElseThrow(() -> new WardAvailabilityNotFoundException("Ward Availability not found"));
            wardAvailability.setBedsAvailable(wardAvailability.getBedsAvailable() - 1);
            firstWardAvailabilityId++;
        }

        Ward ward = wardAvailability.getWard();
        ward.getListOfAdmissions().add(admission);

        if (admissionDateTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
            List<Admission> beds = ward.getListOfCurrentDayAdmissions();
            for (int i = 0; i < beds.size(); i++) {
                Admission bed = beds.get(i);
                if (bed.getDuration() == null) {
                    admission.setRoom(bed.getRoom());
                    admission.setBed(bed.getBed());
                    beds.set(i, admission);
                    break;
                }
            }
        }


        return admission;
    }

    public Admission assignAdmissionToNurse(Long admissionId, Long toStaffId, Long fromStaffId) throws AdmissionNotFoundException, StaffNotFoundException, UnableToAssignAdmissionException {

        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));

        if (toStaffId == 0) {
            admission.setCurrentAssignedNurse(null);
            return admission;
        } else {
            Staff toNurse = staffRepository.findById(toStaffId).orElseThrow(() -> new StaffNotFoundException("Staff not found"));

            if (toNurse.getDisabled()) {
                throw new StaffDisabledException("Unable to assign admission to Disabled Staff");
            }

            if (admission.getCurrentAssignedNurse() == null
                    || (admission.getCurrentAssignedNurse() != null && Objects.equals(
                    admission.getCurrentAssignedNurse().getStaffId(), fromStaffId))) {

                admission.setCurrentAssignedNurse(toNurse);
                toNurse.getListOfNurseAdmissions().add(admission);

                return admission;
            } else {
                throw new UnableToAssignAdmissionException(
                        "Unable to assign an admission ticket that is not yours");
            }

        }



    }

    public String dischargeAdmissions(String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<Ward> allWards = wardRepository.findAll();

        for (Ward ward : allWards) {
            List<Admission> currentAdmissions = ward.getListOfCurrentDayAdmissions();

            for (int i = 0; i < currentAdmissions.size(); i++) {
                Admission currentAdmission = currentAdmissions.get(i);
                if (currentAdmission.getDischargeDateTime() != null && currentAdmission.getDischargeDateTime().isEqual(date)) {
                    Admission emptyAdmission = new Admission();
                    emptyAdmission.setRoom(currentAdmission.getRoom());
                    emptyAdmission.setBed(currentAdmission.getBed());
                    admissionRepository.save(emptyAdmission);

                    currentAdmissions.set(i, emptyAdmission);
                }
            }
        }

        return "Discharge done";
    }

    public String allocateScheduledAdmissions(String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<Ward> allWards = wardRepository.findAll();

        for (Ward ward : allWards) {
            List<Admission> allAdmissions = ward.getListOfAdmissions();
            List<Admission> incomingAdmissions = allAdmissions.stream()
                    .filter(admission -> admission.getAdmissionDateTime().isEqual(date))
                    .collect(Collectors.toList());

            List<Admission> beds = ward.getListOfCurrentDayAdmissions();

            for (Admission incomingAdmission : incomingAdmissions) {
                for (int i = 0; i < beds.size(); i++) {
                    Admission bed = beds.get(i);
                    if (bed.getDuration() == null) {
                        incomingAdmission.setRoom(bed.getRoom());
                        incomingAdmission.setBed(bed.getBed());
                        beds.set(i, incomingAdmission);
                        break;
                    }
                }

            }

        }

        return "Allocation done";
    }

}
