package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreatePrescriptionRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PrescriptionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PrescriptionRecordService {

    private final PrescriptionRecordRepository prescriptionRecordRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public PrescriptionRecordService(PrescriptionRecordRepository prescriptionRecordRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.prescriptionRecordRepository = prescriptionRecordRepository;
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

    public List<PrescriptionRecord> getPrescriptionRecordsByEHRId(Long id) {
        return electronicHealthRecordRepository.findById(id).get().getListOfPrescriptionRecords();
    }

    public PrescriptionRecord updatePrescriptionRecord(Long id,  Integer medicationQuantity,
                                                       Integer dosage, String description, String comments) {
        PrescriptionRecord prescriptionRecord = prescriptionRecordRepository.findById(id).get();
        // Update the necessary fields with the new values from updatedRecord
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
