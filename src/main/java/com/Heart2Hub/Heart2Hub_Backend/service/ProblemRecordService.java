package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.ProblemRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateProblemRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ProblemRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProblemRecordService {

    private final ProblemRecordRepository problemRecordRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    private final MedicalHistoryRecordService medicalHistoryRecordService;

    public ProblemRecordService(ProblemRecordRepository problemRecordRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository, MedicalHistoryRecordService medicalHistoryRecordService) {
        this.problemRecordRepository = problemRecordRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
        this.medicalHistoryRecordService = medicalHistoryRecordService;
    }

    public ProblemRecord createProblemRecord(Long electronicHealthRecordId, ProblemRecord newProblemRecord) throws UnableToCreateProblemRecordException {
        try {
            ElectronicHealthRecord assignedElectronicHealthRecord =  electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
            assignedElectronicHealthRecord.getListOfProblemRecords().add(newProblemRecord);
            problemRecordRepository.save(newProblemRecord);
            electronicHealthRecordRepository.save(assignedElectronicHealthRecord);
            return newProblemRecord;
        } catch (Exception ex) {
            throw new UnableToCreateProblemRecordException(ex.getMessage());
        }
    }

    public String deleteProblemRecord(Long electronicHealthRecordId, Long problemRecordId) throws ProblemRecordNotFoundException {
        try {
            Optional<ProblemRecord> problemRecordOptional = problemRecordRepository.findById(problemRecordId);
            if (problemRecordOptional.isPresent()) {
                ProblemRecord problemRecord = problemRecordOptional.get();
                ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
                electronicHealthRecord.getListOfProblemRecords().remove(problemRecord);
                problemRecordRepository.delete(problemRecord);
                return "ProblemRecord with ProblemRecordId " + problemRecordId + " has been deleted successfully.";
            } else {
                throw new ProblemRecordNotFoundException("ProblemRecord with ID: " + problemRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ProblemRecordNotFoundException(ex.getMessage());
        }
    }

    public ProblemRecord updateProblemRecord(Long problemRecordId, ProblemRecord updatedProblemRecord) throws ProblemRecordNotFoundException {
        try {
            Optional<ProblemRecord> problemRecordOptional = problemRecordRepository.findById(problemRecordId);
            if (problemRecordOptional.isPresent()) {
                ProblemRecord problemRecord = problemRecordOptional.get();
                if (updatedProblemRecord.getDescription() != null) problemRecord.setDescription(updatedProblemRecord.getDescription());
                if (updatedProblemRecord.getPriorityEnum() != null) problemRecord.setPriorityEnum(updatedProblemRecord.getPriorityEnum());
                if (updatedProblemRecord.getProblemTypeEnum() != null) problemRecord.setProblemTypeEnum(updatedProblemRecord.getProblemTypeEnum());
                problemRecordRepository.save(problemRecord);
                return problemRecord;
            } else {
                throw new ProblemRecordNotFoundException("ProblemRecord with ID: " + problemRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ProblemRecordNotFoundException(ex.getMessage());
        }
    }

    public List<ProblemRecord> getAllProblemRecordsByElectronicHealthRecordId(Long electronicHealthRecordId) throws ProblemRecordNotFoundException {
        try {
            List<ProblemRecord> problemRecordList = electronicHealthRecordRepository.findById(electronicHealthRecordId).get().getListOfProblemRecords();
            return problemRecordList;
        } catch (Exception ex) {
            throw new ProblemRecordNotFoundException(ex.getMessage());
        }
    }

    public MedicalHistoryRecord resolveProblemRecord(Long electronicHealthRecordId, Long problemRecordId) throws ProblemRecordNotFoundException {
        try {
            Optional<ProblemRecord> problemRecordOptional = problemRecordRepository.findById(problemRecordId);
            if (problemRecordOptional.isPresent()) {
                ProblemRecord problemRecord = problemRecordOptional.get();
                ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
                electronicHealthRecord.getListOfProblemRecords().remove(problemRecord);
                MedicalHistoryRecord newMedicalHistoryRecord = new MedicalHistoryRecord(
                        problemRecord.getDescription(),
                        problemRecord.getCreatedBy(),
                        problemRecord.getCreatedDate(),
                        LocalDateTime.now(),
                        problemRecord.getPriorityEnum(),
                        problemRecord.getProblemTypeEnum()
                );
                problemRecordRepository.delete(problemRecord);
                medicalHistoryRecordService.createMedicalHistoryRecord(electronicHealthRecordId, newMedicalHistoryRecord);
                return newMedicalHistoryRecord;
            } else {
                throw new ProblemRecordNotFoundException("ProblemRecord with ID: " + problemRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ProblemRecordNotFoundException(ex.getMessage());
        }
    }

    public ElectronicHealthRecord deleteAllProblemRecordsFromElectronicHealthRecord(Long electronicHealthRecordId) throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {
                ElectronicHealthRecord existingElectronicHealthRecord = electronicHealthRecordOptional.get();
                existingElectronicHealthRecord.getListOfProblemRecords().clear();
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
