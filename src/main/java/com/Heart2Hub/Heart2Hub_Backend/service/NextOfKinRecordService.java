package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateNextOfKinRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateSubDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.NextOfKinRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NextOfKinRecordService {

    private final NextOfKinRecordRepository nextOfKinRecordRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public NextOfKinRecordService(NextOfKinRecordRepository nextOfKinRecordRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.nextOfKinRecordRepository = nextOfKinRecordRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    public NextOfKinRecord createNextOfKinRecord(Long electronicHealthRecordId, NextOfKinRecord newNextOfKinRecord) throws UnableToCreateNextOfKinRecordException {
        try {
            ElectronicHealthRecord assignedElectronicHealthRecord = electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
            assignedElectronicHealthRecord.getListOfNextOfKinRecords().add(newNextOfKinRecord);
            nextOfKinRecordRepository.save(newNextOfKinRecord);
            electronicHealthRecordRepository.save(assignedElectronicHealthRecord);
            return newNextOfKinRecord;
        } catch (Exception ex) {
            throw new UnableToCreateNextOfKinRecordException(ex.getMessage());
        }
    }

}
