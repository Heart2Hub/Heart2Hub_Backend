package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.TreatmentPlanRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateTreatmentPlanRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateNextOfKinRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.TreatmentPlanRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.NextOfKinRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TreatmentPlanRecordService {

    private final TreatmentPlanRecordRepository treatmentPlanRecordRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public TreatmentPlanRecordService(TreatmentPlanRecordRepository treatmentPlanRecordRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.treatmentPlanRecordRepository = treatmentPlanRecordRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    public TreatmentPlanRecord createTreatmentPlanRecord(Long electronicHealthRecordId, TreatmentPlanRecord newTreatmentPlanRecord) throws UnableToCreateTreatmentPlanRecordException {
        try {
            ElectronicHealthRecord assignedElectronicHealthRecord =  electronicHealthRecordRepository.findById(electronicHealthRecordId).get();
            assignedElectronicHealthRecord.getListOfTreatmentPlanRecords().add(newTreatmentPlanRecord);
            treatmentPlanRecordRepository.save(newTreatmentPlanRecord);
            electronicHealthRecordRepository.save(assignedElectronicHealthRecord);
            return newTreatmentPlanRecord;
        } catch (Exception ex) {
            throw new UnableToCreateTreatmentPlanRecordException(ex.getMessage());
        }
    }

}
