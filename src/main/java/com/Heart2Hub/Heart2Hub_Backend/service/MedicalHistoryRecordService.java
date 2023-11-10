package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.MedicalHistoryRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicalHistoryRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateSubDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicalHistoryRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicalHistoryRecordService {

    private final MedicalHistoryRecordRepository medicalHistoryRecordRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public MedicalHistoryRecordService(MedicalHistoryRecordRepository medicalHistoryRecordRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.medicalHistoryRecordRepository = medicalHistoryRecordRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    public MedicalHistoryRecord createMedicalHistoryRecord(Long electronicHealthRecordId, MedicalHistoryRecord newMedicalHistoryRecord) throws UnableToCreateMedicalHistoryRecordException {
        try {
            ElectronicHealthRecord assignedElectronicHealthRecord =  electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
            assignedElectronicHealthRecord.getListOfMedicalHistoryRecords().add(newMedicalHistoryRecord);
            medicalHistoryRecordRepository.save(newMedicalHistoryRecord);
            electronicHealthRecordRepository.save(assignedElectronicHealthRecord);
            return newMedicalHistoryRecord;
        } catch (Exception ex) {
            throw new UnableToCreateMedicalHistoryRecordException(ex.getMessage());
        }
    }

    public String deleteMedicalHistoryRecord(Long electronicHealthRecordId, Long medicalHistoryRecordId) throws MedicalHistoryRecordNotFoundException {
        try {
            Optional<MedicalHistoryRecord> medicalHistoryRecordOptional = medicalHistoryRecordRepository.findById(medicalHistoryRecordId);
            if (medicalHistoryRecordOptional.isPresent()) {
                MedicalHistoryRecord medicalHistoryRecord = medicalHistoryRecordOptional.get();
                ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
                electronicHealthRecord.getListOfMedicalHistoryRecords().remove(medicalHistoryRecord);
                medicalHistoryRecordRepository.delete(medicalHistoryRecord);
                return "MedicalHistoryRecord with MedicalHistoryRecordId " + medicalHistoryRecordId + " has been deleted successfully.";
            } else {
                throw new MedicalHistoryRecordNotFoundException("MedicalHistoryRecord with ID: " + medicalHistoryRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new MedicalHistoryRecordNotFoundException(ex.getMessage());
        }
    }

    public MedicalHistoryRecord updateMedicalHistoryRecord(Long medicalHistoryRecordId, MedicalHistoryRecord updatedMedicalHistoryRecord) throws MedicalHistoryRecordNotFoundException {
        try {
            Optional<MedicalHistoryRecord> medicalHistoryRecordOptional = medicalHistoryRecordRepository.findById(medicalHistoryRecordId);
            if (medicalHistoryRecordOptional.isPresent()) {
                MedicalHistoryRecord medicalHistoryRecord = medicalHistoryRecordOptional.get();
                if (updatedMedicalHistoryRecord.getDescription() != null) medicalHistoryRecord.setDescription(updatedMedicalHistoryRecord.getDescription());
                if (updatedMedicalHistoryRecord.getPriorityEnum() != null) medicalHistoryRecord.setPriorityEnum(updatedMedicalHistoryRecord.getPriorityEnum());
                if (updatedMedicalHistoryRecord.getProblemTypeEnum() != null) medicalHistoryRecord.setProblemTypeEnum(updatedMedicalHistoryRecord.getProblemTypeEnum());
                medicalHistoryRecordRepository.save(medicalHistoryRecord);
                return medicalHistoryRecord;
            } else {
                throw new MedicalHistoryRecordNotFoundException("MedicalHistoryRecord with ID: " + medicalHistoryRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new MedicalHistoryRecordNotFoundException(ex.getMessage());
        }
    }

    public List<MedicalHistoryRecord> getAllMedicalHistoryRecordsByElectronicHealthRecordId(Long electronicHealthRecordId) throws MedicalHistoryRecordNotFoundException {
        try {
            List<MedicalHistoryRecord> medicalHistoryRecordList = electronicHealthRecordRepository.findById(electronicHealthRecordId).get().getListOfMedicalHistoryRecords();
            return medicalHistoryRecordList;
        } catch (Exception ex) {
            throw new MedicalHistoryRecordNotFoundException(ex.getMessage());
        }
    }

    public ElectronicHealthRecord deleteAllMedicalHistoryRecordsFromElectronicHealthRecord(Long electronicHealthRecordId) throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {
                ElectronicHealthRecord existingElectronicHealthRecord = electronicHealthRecordOptional.get();
                existingElectronicHealthRecord.getListOfMedicalHistoryRecords().clear();
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
