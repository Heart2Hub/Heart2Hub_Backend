package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicationOrder;
import com.Heart2Hub.Heart2Hub_Backend.exception.MedicationNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.repository.AdmissionRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.AppointmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationOrderRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicationOrderService {

    private final MedicationOrderRepository medicationOrderRepository;

    private final AdmissionRepository admissionRepository;

    private final MedicationRepository medicationRepository;

    public MedicationOrderService(MedicationOrderRepository medicationOrderRepository, AdmissionRepository admissionRepository, MedicationRepository medicationRepository) {
        this.medicationOrderRepository = medicationOrderRepository;
        this.admissionRepository = admissionRepository;
        this.medicationRepository = medicationRepository;
    }

    public MedicationOrder createMedicationOrder(Long medicationId, Long admissionId, MedicationOrder medicationOrder) {
        Admission admission = admissionRepository.findById(admissionId).get();
        Medication medication = medicationRepository.findById(medicationId).get();

        admission.getListOfMedicationOrders().add(medicationOrder);
        medicationOrder.setMedication(medication);
        medication.setQuantityInStock(medication.getQuantityInStock()-medicationOrder.getQuantity());
System.out.println("checking " + admission.getListOfMedicationOrders());
//        admissionRepository.save(admission);
        medicationOrderRepository.save(medicationOrder);
        return medicationOrder;
    }

//    public MedicationOrder updateMedicationOrder(Long medicationOrderId, MedicationOrder newMedicationOrder) {
//        Optional<MedicationOrder> medicationOrderOptional = medicationOrderRepository.findById(medicationOrderId);
//        if (medicationOrderOptional.isPresent()) {
//            MedicationOrder currentMedicationOrder = medicationOrderOptional.get();
//
//            currentMedicationOrder.setComments(newMedicationOrder.getComments());
//            currentMedicationOrder.setQuantity(newMedicationOrder.getQuantity());
//            currentMedicationOrder.setStartDate(newMedicationOrder.getStartDate());
//            currentMedicationOrder.setEndDate(newMedicationOrder.getEndDate());
//            medicationOrderRepository.save(currentMedicationOrder);
//            return currentMedicationOrder;
//        }
//        else {
//            throw new MedicationNotFoundException("Medication Order with ID: " + medicationOrderId + " is not found");
//        }
//    }

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
