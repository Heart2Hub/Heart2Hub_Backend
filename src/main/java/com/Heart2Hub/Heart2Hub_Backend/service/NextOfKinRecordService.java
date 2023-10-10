package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.NextOfKinRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateNextOfKinRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateSubDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.NextOfKinRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
            throw new UnableToCreateNextOfKinRecordException("Relationship and/or NRIC field should not be empty");
        }
    }

    public List<NextOfKinRecord> getNextOfKinRecordsByEHRId(Long electronicHealthRecordId) throws ElectronicHealthRecordNotFoundException {
        try {
            ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
            return electronicHealthRecord.getListOfNextOfKinRecords();
        } catch (Exception ex) {
            throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
        }
    }

    public String deleteNextOfKinRecord(Long nextOfKinRecordId) throws NextOfKinRecordNotFoundException {
        Optional<NextOfKinRecord> nextOfKinRecordOptional = nextOfKinRecordRepository.findById(nextOfKinRecordId);

        if (nextOfKinRecordOptional.isPresent()) {
            nextOfKinRecordRepository.deleteById(nextOfKinRecordId);
            return "Next of Kin Record " + nextOfKinRecordId + " has been successfully deleted.";
        } else {
            throw new NextOfKinRecordNotFoundException("Next of Kin Record " + nextOfKinRecordId + " does not exist.");
        }

    }

}
