package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateProblemRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ProblemRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProblemRecordService {

    private final ProblemRecordRepository problemRecordRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public ProblemRecordService(ProblemRecordRepository problemRecordRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.problemRecordRepository = problemRecordRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
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

}
