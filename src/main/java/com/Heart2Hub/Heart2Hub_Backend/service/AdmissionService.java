package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
    private final TransactionItemService transactionItemService;
    private final ServiceItemRepository serviceItemRepository;

    public AdmissionService(AdmissionRepository admissionRepository, PatientRepository patientRepository, WardRepository wardRepository, WardAvailabilityRepository wardAvailabilityRepository, ElectronicHealthRecordService electronicHealthRecordService, StaffRepository staffRepository, TransactionItemService transactionItemService, ServiceItemRepository serviceItemRepository) {
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.wardRepository = wardRepository;
        this.wardAvailabilityRepository = wardAvailabilityRepository;
        this.electronicHealthRecordService = electronicHealthRecordService;
        this.staffRepository = staffRepository;
        this.transactionItemService = transactionItemService;
        this.serviceItemRepository = serviceItemRepository;
    }

    public List<Admission> getAllAdmissions() {
        return admissionRepository.findAll();
    }

    public Ward createAdmissionInDataLoader(Admission admission, Long wardId) {
        Ward ward = wardRepository.findById(wardId).get();
        admission.setWard(ward);
        ward.getListOfAdmissions().add(admission);

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

        wardRepository.save(ward);

        return ward;
    }

    public Admission createAdmission(Integer duration, String reason, Long patientId, Long doctorId) throws PatientNotFoundException, StaffNotFoundException {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new PatientNotFoundException("Patient not found"));

        Staff doctor = staffRepository.findById(doctorId).orElseThrow(() -> new StaffNotFoundException("Staff not found"));

        Admission newAdmission = new Admission(duration, reason);

        newAdmission.setPatient(patient);
        patient.setAdmission(newAdmission);

        newAdmission.getListOfAssignedStaff().add(doctor);
        doctor.getListOfAssignedAdmissions().add(newAdmission);

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
        admission.setWard(ward);

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

    public Admission updateDischargeDate(Long admissionId, String newDischargeString, Long transactionItemId) {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));

        LocalDateTime originalDischargeDate = admission.getDischargeDateTime();
        LocalDateTime newDischargeDate = LocalDateTime.parse(newDischargeString);

        long daysDifference = ChronoUnit.DAYS.between(originalDischargeDate, newDischargeDate);
        admission.setDuration(admission.getDuration() + (int) daysDifference);

        transactionItemService.updateTransactionItem(transactionItemId, admission.getDuration());

        admission.setDischargeDateTime(newDischargeDate);

        List<WardAvailability> wardAvailabilitiesToUpdate;

        if (newDischargeDate.isBefore(originalDischargeDate)) {
            newDischargeDate = newDischargeDate.plusDays(1);
            wardAvailabilitiesToUpdate = wardAvailabilityRepository.findByWardAndDateBetween(admission.getWard(), newDischargeDate, originalDischargeDate);
            for (WardAvailability wardAvailability: wardAvailabilitiesToUpdate) {
                wardAvailability.setBedsAvailable(wardAvailability.getBedsAvailable() + 1);
            }
        } else if (newDischargeDate.isAfter(originalDischargeDate)) {
            originalDischargeDate = originalDischargeDate.plusDays(1);
            wardAvailabilitiesToUpdate = wardAvailabilityRepository.findByWardAndDateBetween(admission.getWard(), originalDischargeDate, newDischargeDate);
            for (WardAvailability wardAvailability: wardAvailabilitiesToUpdate) {
                wardAvailability.setBedsAvailable(wardAvailability.getBedsAvailable() - 1);
            }
        }

        return admission;
    }



    public Admission assignAdmissionToStaff(Long admissionId, Long toStaffId) {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));
        Staff toStaff = staffRepository.findById(toStaffId).orElseThrow(() -> new StaffNotFoundException("Staff not found"));

        toStaff.getListOfAssignedAdmissions().add(admission);

        boolean reassigned = false;
        List<Staff> listOfAssignedStaff = admission.getListOfAssignedStaff();

        for (int i = 0; i < listOfAssignedStaff.size(); i++) {
            Staff assignedStaff = listOfAssignedStaff.get(i);
            if (assignedStaff.getStaffRoleEnum() == toStaff.getStaffRoleEnum()) {
                listOfAssignedStaff.set(i, toStaff);
                reassigned = true;
                break;
            }
        }

        if (!reassigned) {
            listOfAssignedStaff.add(toStaff);
        }

        return admission;
    }

    public Admission updateAdmissionArrival(Long admissionId, Boolean arrivalStatus,
                                                Long staffId) throws AdmissionNotFoundException {

        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));

        admission.setArrived(arrivalStatus);

        // Add Admission to Patient's cart
        String serviceItemName = "Ward (Class " + admission.getWard().getWardClass().getWardClassName() + ") (daily)";
        List<ServiceItem> wardClassRateList = serviceItemRepository.findByInventoryItemNameContainingIgnoreCase(serviceItemName);
        ServiceItem wardClassRate = wardClassRateList.get(0);

        String inventoryItemDescription = "Ward Charges";
        BigDecimal price = wardClassRate.getRetailPricePerQuantity();
        BigDecimal totalPrice = price.multiply(new BigDecimal(admission.getDuration()));
        transactionItemService.inpatientAddToCart(admission.getPatient().getPatientId(), wardClassRate.getInventoryItemName(),
                inventoryItemDescription, admission.getDuration(), totalPrice, wardClassRate.getInventoryItemId());

        return admission;
    }

    public Admission updateAdmissionComments(Long admissionId, String comments, Long staffId) {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));

        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> new StaffNotFoundException("Staff not found"));
        String newComment =
                comments + " (" + staff.getStaffRoleEnum() + " " + staff.getFirstname() + " "
                        + staff.getLastname() + ")";
        if (!admission.getComments().equals("")) {
            newComment = "\n" + newComment;
        }
        newComment = admission.getComments() + newComment;
        admission.setComments(newComment);
        return admission;
    }

    public String cancelAdmission(Long admissionIdToCancel, Long wardId) {
        Admission admissionToCancel = admissionRepository.findById(admissionIdToCancel).get();
        Patient patient = admissionToCancel.getPatient();
        patient.setAdmission(null);
        patient.getElectronicHealthRecord().getListOfPastAdmissions().add(admissionToCancel);


        Ward ward = wardRepository.findById(wardId).get();
        List<Admission> currentAdmissions = ward.getListOfCurrentDayAdmissions();
        for (int i = 0; i < currentAdmissions.size(); i++) {
            Admission admission = currentAdmissions.get(i);

            if (admission.getAdmissionId() == admissionIdToCancel) {
                Admission emptyAdmission = new Admission();
                emptyAdmission.setRoom(admission.getRoom());
                emptyAdmission.setBed(admission.getBed());
                admissionRepository.save(emptyAdmission);
                currentAdmissions.set(i, emptyAdmission);
            }
        }

        //Ward Availability part
        List<WardAvailability> wardAvailabilities = wardAvailabilityRepository.findByWardAndDateBetween(ward, admissionToCancel.getAdmissionDateTime(), admissionToCancel.getDischargeDateTime());
        for (WardAvailability wardAvailability: wardAvailabilities) {
            wardAvailability.setBedsAvailable(wardAvailability.getBedsAvailable() + 1);
        }

        return "Cancelled Admission";
    }



    public String dischargeAdmissions(String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        List<Ward> allWards = wardRepository.findAll();

        for (Ward ward : allWards) {
            List<Admission> currentAdmissions = ward.getListOfCurrentDayAdmissions();

            for (int i = 0; i < currentAdmissions.size(); i++) {
                Admission currentAdmission = currentAdmissions.get(i);
                Patient patient = currentAdmission.getPatient();

                if (currentAdmission.getDischargeDateTime() != null && currentAdmission.getDischargeDateTime().isEqual(date)) {

                    Admission emptyAdmission = new Admission();
                    emptyAdmission.setRoom(currentAdmission.getRoom());
                    emptyAdmission.setBed(currentAdmission.getBed());
                    admissionRepository.save(emptyAdmission);

                    currentAdmissions.set(i, emptyAdmission);

                    //Add current admission to list of past admissions in EHR
                    patient.setAdmission(null);
                    patient.getElectronicHealthRecord().getListOfPastAdmissions().add(currentAdmission);

                    //checkout
                    transactionItemService.checkout(patient.getPatientId());
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

    public Admission getAdmissionByAdmissionId(Long admissionId) {
        return admissionRepository.findById(admissionId).get();
    }

//    public Admission getAdmissionByUsername(String username) {
//        Patient patient = patientRepository.findByUsername(username).get();
//        Admission admission = patient.getAdmission();
//        if (patient.getAdmission() == null) {
//            throw new AdmissionNotFoundException("Patient is ")
//        } else if ()
//    }

    public Admission addImageAttachment(Long admissionId, String imageLink, String createdDate) {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
        LocalDateTime createdDateTime = LocalDateTime.parse(createdDate, formatter);
        ImageDocument imageDocument = new ImageDocument(imageLink, createdDateTime);
        admission.getListOfImageDocuments().add(imageDocument);

        return admission;
    }

    public List<ImageDocument> viewImageAttachments(Long admissionId) {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow(() -> new AdmissionNotFoundException("Admission not found"));
        return admission.getListOfImageDocuments();
    }

}
