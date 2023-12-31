package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateInpatientTreatmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.AdmissionRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.InpatientTreatmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ServiceItemRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class InpatientTreatmentService {

    private final InpatientTreatmentRepository inpatientTreatmentRepository;

    private final AdmissionRepository admissionRepository;

    private final StaffRepository staffRepository;

    private final ServiceItemRepository serviceItemRepository;

    private final TransactionItemService transactionItemService;

    public InpatientTreatmentService(InpatientTreatmentRepository inpatientTreatmentRepository, AdmissionRepository admissionRepository, StaffRepository staffRepository, ServiceItemRepository serviceItemRepository, TransactionItemService transactionItemService) {
        this.inpatientTreatmentRepository = inpatientTreatmentRepository;
        this.staffRepository = staffRepository;
        this.admissionRepository = admissionRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.transactionItemService = transactionItemService;
    }

    public InpatientTreatment createInpatientTreatment(Long serviceItemId, Long admissionId, Long staffId, InpatientTreatment inpatientTreatment) throws UnableToCreateInpatientTreatmentException {
        Staff staff = staffRepository.findById(staffId).get();
        LocalDateTime start = inpatientTreatment.getStartDate().withHour(0);
        LocalDateTime end = inpatientTreatment.getEndDate().withHour(23);

        List<InpatientTreatment> inpatientTreatmentsToCheck = inpatientTreatmentRepository.findByStaffAndStartDateBetween(staff, start, end);

        Comparator<InpatientTreatment> comparatorAsc = (it1, it2) -> it1.getStartDate()
                .compareTo(it2.getStartDate());

        Collections.sort(inpatientTreatmentsToCheck, comparatorAsc);

        for (InpatientTreatment existingInpatientTreatment : inpatientTreatmentsToCheck) {
            if (inpatientTreatment.getStartDate().isBefore(existingInpatientTreatment.getStartDate())
            && inpatientTreatment.getEndDate().isAfter(existingInpatientTreatment.getEndDate())) {
                throw new UnableToCreateInpatientTreatmentException("Staff has another inpatient treatment in this time slot");
            } else if (inpatientTreatment.getEndDate().isAfter(existingInpatientTreatment.getStartDate())
            && inpatientTreatment.getEndDate().isBefore(existingInpatientTreatment.getEndDate())) {
                throw new UnableToCreateInpatientTreatmentException("Staff has another inpatient treatment in this time slot");
            } else if (inpatientTreatment.getStartDate().isAfter(existingInpatientTreatment.getStartDate())
            && inpatientTreatment.getStartDate().isBefore(existingInpatientTreatment.getEndDate())) {
                throw new UnableToCreateInpatientTreatmentException("Staff has another inpatient treatment in this time slot");
            } else if (inpatientTreatment.getStartDate().isEqual(existingInpatientTreatment.getStartDate())
            && inpatientTreatment.getEndDate().isEqual(existingInpatientTreatment.getEndDate())) {
                throw new UnableToCreateInpatientTreatmentException("Staff has another inpatient treatment in this time slot");
            }
        }
        ServiceItem serviceItem = serviceItemRepository.findById(serviceItemId).get();
        Admission admission = admissionRepository.findById(admissionId).get();

        inpatientTreatment.setServiceItem(serviceItem);
        inpatientTreatment.setStaff(staff);
        admission.getListOfInpatientTreatments().add(inpatientTreatment);
        inpatientTreatmentRepository.save(inpatientTreatment);
        return inpatientTreatment;
    }

    public InpatientTreatment getInpatientTreatmentById(Long inpatientTreatmentId) {
        return inpatientTreatmentRepository.findById(inpatientTreatmentId).get();
    }

    public InpatientTreatment updateArrival(Long inpatientTreatmentId, Boolean arrivalStatus) {
        InpatientTreatment inpatientTreatment = inpatientTreatmentRepository.findById(inpatientTreatmentId).get();
        inpatientTreatment.setArrived(arrivalStatus);

        return inpatientTreatment;
    }

    public InpatientTreatment updateComplete(Long inpatientTreatmentId, Long admissionId) {
        InpatientTreatment inpatientTreatment = inpatientTreatmentRepository.findById(inpatientTreatmentId).get();
        Admission currentAdmission = admissionRepository.findById(admissionId).get();

        inpatientTreatment.setIsCompleted(!inpatientTreatment.getIsCompleted());
        String inventoryItemDescription = "Inpatient Treatment";
        BigDecimal price = inpatientTreatment.getServiceItem().getRetailPricePerQuantity();

        transactionItemService.inpatientAddToCart(currentAdmission.getPatient().getPatientId(), inpatientTreatment.getServiceItem().getInventoryItemName(),
                inventoryItemDescription, 1, price, inpatientTreatment.getServiceItem().getInventoryItemId());

        return inpatientTreatment;
    }

    public String deleteInpatientTreatment(Long inpatientTreatmentId, Long admissionId) {
        InpatientTreatment inpatientTreatment = inpatientTreatmentRepository.findById(inpatientTreatmentId).get();
        Admission admission = admissionRepository.findById(admissionId).get();
        admission.getListOfInpatientTreatments().remove(inpatientTreatment);
        inpatientTreatmentRepository.delete(inpatientTreatment);

        return "Inpatient Treatment Deleted";
    }

}
