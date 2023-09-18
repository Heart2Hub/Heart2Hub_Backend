package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public PatientService(PatientRepository patientRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.patientRepository = patientRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    // TO-DO: CREATE PATIENT OVERLOADED METHOD WHICH JUST TAKES IN NEW PATIENT PULLS FROM NEHR FRONT END HANDLE ERROR CATCHING

    public Patient createPatient(Patient newPatient, ElectronicHealthRecord newElectronicHealthRecord) throws UnableToCreatePatientException {
        try {
            newElectronicHealthRecord.setPatient(newPatient);
            newPatient.setElectronicHealthRecord(newElectronicHealthRecord);
            electronicHealthRecordRepository.save(newElectronicHealthRecord);
            patientRepository.save(newPatient);
            return newPatient;
        } catch (Exception ex) {
            throw new UnableToCreatePatientException(ex.getMessage());
        }
    }

    public List<JSONObject> getAllPatientsWithElectronicHealthRecordSummaryByName(String name) throws PatientNotFoundException {
        try {
            List<JSONObject> patientsWithElectronicHealthRecordSummaryList = new ArrayList<>();
            // By first name
            List<ElectronicHealthRecord> electronicHealthRecordList = new ArrayList<>();
            electronicHealthRecordList.addAll(electronicHealthRecordRepository.findByFirstNameContainsIgnoreCase(name));
            // electronicHealthRecordList.addAll(electronicHealthRecordRepository.findByLastNameContainsIgnoreCase(name));
            for (ElectronicHealthRecord electronicHealthRecord : electronicHealthRecordList) {
                JSONObject patientsWithElectronicHealthRecordSummary = new JSONObject();
                patientsWithElectronicHealthRecordSummary.put("electronicHealthRecordId",electronicHealthRecord.getElectronicHealthRecordId());
                patientsWithElectronicHealthRecordSummary.put("username",electronicHealthRecord.getPatient().getUsername());
                patientsWithElectronicHealthRecordSummary.put("firstName",electronicHealthRecord.getFirstName());
                patientsWithElectronicHealthRecordSummary.put("lastName",electronicHealthRecord.getLastName());
                patientsWithElectronicHealthRecordSummary.put("sex",electronicHealthRecord.getSex());
                patientsWithElectronicHealthRecordSummary.put("nric",electronicHealthRecord.getNric());
                patientsWithElectronicHealthRecordSummary.put("profilePicture",electronicHealthRecord.getPatient().getProfilePicture() != null ? electronicHealthRecord.getPatient().getProfilePicture().getImageLink() : "");
                patientsWithElectronicHealthRecordSummaryList.add(patientsWithElectronicHealthRecordSummary);
            }
            // By username
//            List<Patient> patientList = new ArrayList<>(patientRepository.findByUsernameContainsIgnoreCase(name));
//            for (Patient patient : patientList) {
//                JSONObject patientsWithElectronicHealthRecordSummary = new JSONObject();
//                patientsWithElectronicHealthRecordSummary.put("username",patient.getUsername());
//                patientsWithElectronicHealthRecordSummary.put("firstName",patient.getElectronicHealthRecord().getFirstName());
//                patientsWithElectronicHealthRecordSummary.put("lastName",patient.getElectronicHealthRecord().getLastName());
//                patientsWithElectronicHealthRecordSummary.put("sex",patient.getElectronicHealthRecord().getSex());
//                patientsWithElectronicHealthRecordSummary.put("profilePicture",patient.getProfilePicture() != null ? patient.getProfilePicture().getImageLink() : "");
//                patientsWithElectronicHealthRecordSummaryList.add(patientsWithElectronicHealthRecordSummary);
//            }
            return patientsWithElectronicHealthRecordSummaryList;
        } catch (Exception ex) {
            throw new PatientNotFoundException(ex.getMessage());
        }
    }
}
