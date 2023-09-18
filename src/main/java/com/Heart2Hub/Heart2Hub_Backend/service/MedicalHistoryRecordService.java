package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicalHistoryRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateSubDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicalHistoryRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
