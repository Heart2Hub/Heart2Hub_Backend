package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PrescriptionStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreatePrescriptionRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.InventoryItemRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PrescriptionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrescriptionRecordService {

    private final PrescriptionRecordRepository prescriptionRecordRepository;

    private final PatientRepository patientRepository;
    private final TransactionItemService transactionItemService;
    private final InventoryItemRepository inventoryItemRepository;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public PrescriptionRecordService(PrescriptionRecordRepository prescriptionRecordRepository, PatientRepository patientRepository, TransactionItemService transactionItemService, InventoryItemRepository inventoryItemRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.prescriptionRecordRepository = prescriptionRecordRepository;
        this.patientRepository = patientRepository;
        this.transactionItemService = transactionItemService;
        this.inventoryItemRepository = inventoryItemRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    public PrescriptionRecord createPrescriptionRecord(Long electronicHealthRecordId, PrescriptionRecord newPrescriptionRecord) throws UnableToCreatePrescriptionRecordException {
        try {
            ElectronicHealthRecord assignedElectronicHealthRecord =  electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
            assignedElectronicHealthRecord.getListOfPrescriptionRecords().add(newPrescriptionRecord);
            prescriptionRecordRepository.save(newPrescriptionRecord);
            electronicHealthRecordRepository.save(assignedElectronicHealthRecord);
            return newPrescriptionRecord;
        } catch (Exception ex) {
            throw new UnableToCreatePrescriptionRecordException(ex.getMessage());
        }
    }

    public List<PrescriptionRecord> getAllPrescriptionRecords() {
        return prescriptionRecordRepository.findAll();
    }

    public List<InventoryItem> getAllInventoryItems() {
        return inventoryItemRepository.findAll();
    }

    public List<PrescriptionRecord> getPrescriptionRecordsByEHRId(Long id) {
        return electronicHealthRecordRepository.findById(id).get().getListOfPrescriptionRecords();
    }

    public TransactionItem checkOutPrescription(Long prescriptionId, Long ehrId) {
        PrescriptionRecord pr = prescriptionRecordRepository.findById(prescriptionId).get();
        pr.setPrescriptionStatusEnum(PrescriptionStatusEnum.PENDING);
        Medication medicine = (Medication) pr.getInventoryItem();
        Patient p = electronicHealthRecordRepository.findById(ehrId).get().getPatient();

        TransactionItem item = transactionItemService.addToCart(p.getPatientId(), "Prescription Record " +pr.getPrescriptionRecordId() + " " + medicine.getInventoryItemName(), medicine.getInventoryItemDescription(),
                pr.getMedicationQuantity(), medicine.getRetailPricePerQuantity(), medicine.getInventoryItemId());

        return item;
    }

    public PrescriptionRecord doctorCreateNewPrescription(PrescriptionRecord pr, Long itemId, Long ehrId) {
        pr.setInventoryItem(inventoryItemRepository.findById(itemId).get());
        pr.setMedicationName(inventoryItemRepository.findById(itemId).get().getInventoryItemName());
        return createPrescriptionRecord(ehrId, pr);
    }

    public PrescriptionRecord updatePrescriptionRecord(Long id,  Integer medicationQuantity,
                                                       Integer dosage, String description, String comments, PrescriptionStatusEnum prescriptionStatusEnum) {
        PrescriptionRecord prescriptionRecord = prescriptionRecordRepository.findById(id).get();
        // Update the necessary fields with the new values from updatedRecord
        prescriptionRecord.setPrescriptionStatusEnum(prescriptionStatusEnum);
        prescriptionRecord.setMedicationQuantity(medicationQuantity);
        prescriptionRecord.setDosage(dosage);
        prescriptionRecord.setDescription(description);
        prescriptionRecord.setComments(comments);

        // Save the updated prescription record
        return prescriptionRecordRepository.save(prescriptionRecord);
    }

    public void deletePrescriptionRecord(Long id) {
        prescriptionRecordRepository.deleteById(id);
    }
}
