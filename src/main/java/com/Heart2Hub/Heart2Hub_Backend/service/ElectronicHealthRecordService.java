package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateElectronicHealthRecordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateSubDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ElectronicHealthRecordService {

    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public ElectronicHealthRecordService(PatientRepository patientRepository, StaffRepository staffRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    public ElectronicHealthRecord getElectronicHealthRecordByIdAndDateOfBirth(Long electronicHealthRecordId, LocalDateTime dateOfBirth) throws ElectronicHealthRecordNotFoundException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findById(electronicHealthRecordId);

            if (electronicHealthRecordOptional.isPresent()) {
                ElectronicHealthRecord electronicHealthRecord = electronicHealthRecordOptional.get();
                if (!dateOfBirth.equals(electronicHealthRecord.getDateOfBirth())) {
                    throw new ElectronicHealthRecordNotFoundException("Date of Birth is invalid");
                } else {
                    return electronicHealthRecord;
                }
            } else {
                throw new ElectronicHealthRecordNotFoundException("Electronic Health Record with Id: " + electronicHealthRecordId + " is not found");
            }
        } catch (Exception ex) {
            throw new ElectronicHealthRecordNotFoundException(ex.getMessage());
        }
    }

    public ElectronicHealthRecord getNehrRecordByNric(String nric){
        try {
            final String uri = "http://localhost:3002/records/" + nric;
            RestTemplate restTemplate = new RestTemplate();
            ElectronicHealthRecord result = restTemplate.getForObject(uri, ElectronicHealthRecord.class);
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

}
