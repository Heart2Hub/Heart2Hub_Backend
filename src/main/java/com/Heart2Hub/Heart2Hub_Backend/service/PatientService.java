package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder, ElectronicHealthRecordRepository electronicHealthRecordRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // TO-DO: CREATE PATIENT OVERLOADED METHOD WHICH JUST TAKES IN NEW PATIENT PULLS FROM NEHR FRONT END HANDLE ERROR CATCHING

    public Patient createPatient(Patient newPatient, ElectronicHealthRecord newElectronicHealthRecord) throws UnableToCreatePatientException {
        try {
            newElectronicHealthRecord.setPatient(newPatient);
            newPatient.setElectronicHealthRecord(newElectronicHealthRecord);
            newPatient.setPassword(passwordEncoder.encode(newPatient.getPassword()));
            electronicHealthRecordRepository.save(newElectronicHealthRecord);
            patientRepository.save(newPatient);
            return newPatient;
        } catch (Exception ex) {
            throw new UnableToCreatePatientException(ex.getMessage());
        }
    }

    public String authenticatePatient(String username, String password) {
        //authenticate username and password, otherwise fails
        System.out.println("step 1");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        //at this point, user is authenticated
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found"));

        return jwtService.generateToken(patient);
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
