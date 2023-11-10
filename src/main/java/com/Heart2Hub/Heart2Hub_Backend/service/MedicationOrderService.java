package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.DrugRestriction;
import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicationOrder;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.AdmissionRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.AppointmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationOrderRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicationOrderService {

    private final MedicationOrderRepository medicationOrderRepository;

    private final AdmissionRepository admissionRepository;

    private final MedicationRepository medicationRepository;
    private final TransactionItemService transactionItemService;

    public MedicationOrderService(MedicationOrderRepository medicationOrderRepository, AdmissionRepository admissionRepository, MedicationRepository medicationRepository, TransactionItemService transactionItemService) {
        this.medicationOrderRepository = medicationOrderRepository;
        this.admissionRepository = admissionRepository;
        this.medicationRepository = medicationRepository;
        this.transactionItemService = transactionItemService;
    }

    public MedicationOrder createMedicationOrder(Long medicationId, Long admissionId, MedicationOrder medicationOrder) throws UnableToCreateMedicationOrderException {
        try {
        Admission admission = admissionRepository.findById(admissionId).get();
        Medication medication = medicationRepository.findById(medicationId).get();
        if (medication.getQuantityInStock() - medicationOrder.getQuantity() < 0) {
            throw new InsufficientInventoryException("Insufficient Inventory for Medication");
        }

        if (medicationOrder.getComments() == null) {
            throw new UnableToCreateMedicationOrderException("Comments must be present.");
        }

        for (MedicationOrder order : admission.getListOfMedicationOrders()) {
            Medication currentMedication = order.getMedication();
            //Get the current Medication item's drug restrictions
            List<DrugRestriction> drugRestriction = currentMedication.getDrugRestrictions();

            //Loop through that item's drug restrictions
            for (DrugRestriction restriction : drugRestriction) {
                String drugType = restriction.getDrugName();
                String checkDrug = medication.getInventoryItemName();
                if (checkDrug.contains(drugType)) {
                    throw new InsufficientInventoryException("Patient can't be administered this drug due to " + drugType + "'s Drug Restrictions");
                }
            }
        }

        admission.getListOfMedicationOrders().add(medicationOrder);
        medicationOrder.setMedication(medication);
        medication.setQuantityInStock(medication.getQuantityInStock()-medicationOrder.getQuantity());
System.out.println("checking " + admission.getListOfMedicationOrders());
//        admissionRepository.save(admission);
        medicationOrderRepository.save(medicationOrder);
        return medicationOrder;
        } catch (Exception ex) {
            throw new UnableToCreateMedicationOrderException(ex.getMessage());
        }
    }

    public MedicationOrder updateComplete(Long medicationOrderId, Long admissionId, Boolean isCompleted) {
        Optional<MedicationOrder> medicationOrderOptional = medicationOrderRepository.findById(medicationOrderId);

        if (medicationOrderOptional.isPresent()) {
            MedicationOrder currentMedicationOrder = medicationOrderOptional.get();
            Admission currentAdmission = admissionRepository.findById(admissionId).get();

            currentMedicationOrder.setIsCompleted(isCompleted);
            medicationOrderRepository.save(currentMedicationOrder);
            String inventoryItemDescription = "Medication Order";
            BigDecimal price = currentMedicationOrder.getMedication().getRetailPricePerQuantity();
            BigDecimal totalPrice = price.multiply(new BigDecimal(currentMedicationOrder.getQuantity()));
            transactionItemService.inpatientAddToCart(currentAdmission.getPatient().getPatientId(), currentMedicationOrder.getMedication().getInventoryItemName(),
                    inventoryItemDescription, currentMedicationOrder.getQuantity(), totalPrice, currentMedicationOrder.getMedication().getInventoryItemId());
            return currentMedicationOrder;
        }
        else {
            throw new MedicationNotFoundException("Medication Order with ID: " + medicationOrderId + " is not found");
        }
    }

    public String deleteMedicationOrder(Long medicationOrderId, Long admissionId) {
        MedicationOrder medicationOrder = medicationOrderRepository.findById(medicationOrderId).get();
        Admission admission = admissionRepository.findById(admissionId).get();
        admission.getListOfMedicationOrders().remove(medicationOrder);
        admissionRepository.save(admission);
        medicationOrderRepository.delete(medicationOrder);

        return "Medication Order Deleted";
    }

    public List<MedicationOrder> getAllMedicationOrdersOfAdmission(Long admissionId) {
System.out.println("Checking Order" + admissionRepository.findById(admissionId).get().getListOfMedicationOrders());
List<MedicationOrder> medicationOrderList = admissionRepository.findById(admissionId).get().getListOfMedicationOrders();
        return medicationOrderList;
    }

    public List<MedicationOrder> getAllMedicationOrders() {
        return medicationOrderRepository.findAll();
    }

    public MedicationOrder getMedicationOrderById(Long medicationOrderId) {
        return medicationOrderRepository.findById(medicationOrderId).get();
    }


}
