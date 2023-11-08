package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PrescriptionStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.OverlappingBookingException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreatePrescriptionRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.InventoryItemRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PrescriptionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        List<PrescriptionRecord> prescriptionRecords = electronicHealthRecordRepository.findById(id).get().getListOfPrescriptionRecords();

        // Iterate through the list of prescription records
        for (PrescriptionRecord prescriptionRecord : prescriptionRecords) {
            LocalDateTime expirationDate = prescriptionRecord.getExpirationDate();
            LocalDateTime currentDate = LocalDateTime.now();;
            if (expirationDate.isBefore(currentDate)) {
                // Update the enum for the expired prescription record
                prescriptionRecord.setPrescriptionStatusEnum(PrescriptionStatusEnum.EXPIRED);
            }
            if (expirationDate.isAfter(currentDate)) {
                // Update the enum for the expired prescription record
                prescriptionRecord.setPrescriptionStatusEnum(PrescriptionStatusEnum.ONGOING);
            }
        }
        return prescriptionRecords;
    }

    public TransactionItem checkOutPrescription(Long prescriptionId, Long ehrId) {
        PrescriptionRecord pr = prescriptionRecordRepository.findById(prescriptionId).get();
        //pr.setPrescriptionStatusEnum(PrescriptionStatusEnum.PENDING);
        Medication medicine = (Medication) pr.getInventoryItem();
        Patient p = electronicHealthRecordRepository.findById(ehrId).get().getPatient();

        TransactionItem item = transactionItemService.addToCart(p.getPatientId(), "(Prescription Record " + pr.getPrescriptionRecordId() + "): " + medicine.getInventoryItemName(), medicine.getInventoryItemDescription(),
                pr.getDosage(), medicine.getRetailPricePerQuantity(), medicine.getInventoryItemId());

        return item;
    }

    public PrescriptionRecord doctorCreateNewPrescription(PrescriptionRecord pr, Long itemId, Long ehrId) {
        Medication m = (Medication) inventoryItemRepository.findById(itemId).get();
        LocalDateTime expirationDate = pr.getExpirationDate();
        LocalDateTime currentDate = LocalDateTime.now();
        if(expirationDate.isBefore(currentDate)) {
            throw new OverlappingBookingException("Expiry Date must be later than today");
        }

        pr.setInventoryItem(m);
        pr.setMedicationName(m.getInventoryItemName());
        pr.setMedicationQuantity(m.getQuantityInStock());
        return createPrescriptionRecord(ehrId, pr);
    }

    public PrescriptionRecord updatePrescriptionRecord(Long id, Integer dosage, String description, String comments,
                                                        LocalDateTime expirationDate) {
        PrescriptionRecord prescriptionRecord = prescriptionRecordRepository.findById(id).get();
        // Update the necessary fields with the new values from updatedRecord
        //prescriptionRecord.setPrescriptionStatusEnum(prescriptionStatusEnum);
        prescriptionRecord.setExpirationDate(expirationDate);
        //prescriptionRecord.setMedicationQuantity(medicationQuantity);
        prescriptionRecord.setDosage(dosage);
        prescriptionRecord.setDescription(description);
        prescriptionRecord.setComments(comments);

        // Save the updated prescription record
        return prescriptionRecordRepository.save(prescriptionRecord);
    }

    public void deletePrescriptionRecord(Long id) {
        prescriptionRecordRepository.deleteById(id);
    }

    public List<PrescriptionRecord> getPrescriptionRecordByNric(String nric) {
        Optional<ElectronicHealthRecord> ehrOptional = electronicHealthRecordRepository.findByNric(nric);
        if (ehrOptional.isPresent()) {
            ElectronicHealthRecord ehr = ehrOptional.get();
            return ehr.getListOfPrescriptionRecords();
        } else {
            throw new ElectronicHealthRecordNotFoundException("NRIC " + nric + " not found in EHR.");
        }
    }
    public ElectronicHealthRecord deleteAllPrescriptionRecordsFromElectronicHealthRecord(Long electronicHealthRecordId) throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {
                ElectronicHealthRecord existingElectronicHealthRecord = electronicHealthRecordOptional.get();
                existingElectronicHealthRecord.getListOfPrescriptionRecords().clear();
                electronicHealthRecordRepository.save(existingElectronicHealthRecord);
                return existingElectronicHealthRecord;
            } else {
                throw new ElectronicHealthRecordNotFoundException("Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
        }
    }

}
