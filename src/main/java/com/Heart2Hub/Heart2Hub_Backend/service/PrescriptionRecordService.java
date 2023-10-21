package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreatePrescriptionRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PrescriptionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
