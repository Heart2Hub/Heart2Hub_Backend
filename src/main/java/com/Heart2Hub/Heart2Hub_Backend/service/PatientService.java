package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.dto.NehrDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.mapper.NehrMapper;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;
    private final ElectronicHealthRecordService electronicHealthRecordService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ImageDocumentService imageDocumentService;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder, ElectronicHealthRecordRepository electronicHealthRecordRepository, AuthenticationManager authenticationManager, JwtService jwtService, ElectronicHealthRecordService electronicHealthRecordService, ImageDocumentService imageDocumentService) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.electronicHealthRecordService = electronicHealthRecordService;
        this.imageDocumentService = imageDocumentService;
    }

    public String validateNric(String nric) throws UnableToCreatePatientException {
        try {
            Optional<ElectronicHealthRecord> electronicHealthRecordOptional = electronicHealthRecordRepository.findByNric(nric);
            if (electronicHealthRecordOptional.isPresent()) {
                throw new UnableToCreatePatientException("Patient account already exists for " + nric + ". Please login with existing account.");
            } else {
                ElectronicHealthRecord nehrRecord = electronicHealthRecordService.getNehrRecordByNric(nric);
                if (nehrRecord == null) {
                    throw new UnableToCreatePatientException("NEHR Record is not found. Please provide NEHR details.");
                }
                return "NRIC is valid";
            }
        } catch (Exception ex) {
            throw new UnableToCreatePatientException(ex.getMessage());
        }
    }

    public Patient createPatient(Patient newPatient, String nric) {
        return createPatient(newPatient, nric, null);
    }

    public Patient createPatient(Patient newPatient, String nric, ImageDocument imageDocument) throws UnableToCreatePatientException {
        try {
            ElectronicHealthRecord nehrRecord = electronicHealthRecordService.getNehrRecordByNric(nric);
            if (nehrRecord == null) {
                throw new UnableToCreatePatientException("NEHR Record is not found. Please provide NEHR details.");
            }
            newPatient.setPassword(passwordEncoder.encode(newPatient.getPassword()));

            if (imageDocument != null) {
                ImageDocument createdImageDocument = imageDocumentService.createImageDocument(
                        imageDocument);
                newPatient.setProfilePicture(createdImageDocument);
            }

            nehrRecord.setPatient(newPatient);
            newPatient.setElectronicHealthRecord(nehrRecord);
            electronicHealthRecordRepository.save(nehrRecord);
            patientRepository.save(newPatient);
            return newPatient;
        } catch (Exception ex) {
            throw new UnableToCreatePatientException(ex.getMessage());
        }
    }

    public Patient createPatient(Patient newPatient, ElectronicHealthRecord newElectronicHealthRecord) {
        return createPatient(newPatient, newElectronicHealthRecord, null);
    }

    public Patient createPatient(Patient newPatient, ElectronicHealthRecord newElectronicHealthRecord, ImageDocument imageDocument) throws UnableToCreatePatientException {
        try {
            ElectronicHealthRecord nehrRecord = electronicHealthRecordService.getNehrRecordByNric(newElectronicHealthRecord.getNric());
            if (nehrRecord != null) {
                throw new UnableToCreatePatientException("NEHR Record is found. Please do not create a new record.");
            }
            newPatient.setPassword(passwordEncoder.encode(newPatient.getPassword()));

            if (imageDocument != null) {
                ImageDocument createdImageDocument = imageDocumentService.createImageDocument(
                        imageDocument);
                newPatient.setProfilePicture(createdImageDocument);
            }

            NehrMapper nehrMapper = new NehrMapper();
            NehrDTO newNehrDTO = nehrMapper.convertToDto(newElectronicHealthRecord);

            newElectronicHealthRecord.setPatient(newPatient);
            newPatient.setElectronicHealthRecord(newElectronicHealthRecord);
            electronicHealthRecordRepository.save(newElectronicHealthRecord);
            patientRepository.save(newPatient);

            RestTemplate restTemplate = new RestTemplate();
            String endpointUrl = "http://localhost:3002/records";
            HttpHeaders headers = new HttpHeaders();
            headers.set("encoded-message", electronicHealthRecordService.encodeSecretMessage());
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<NehrDTO> requestEntity = new HttpEntity<>(newNehrDTO, headers);
            ResponseEntity<NehrDTO> responseEntity = restTemplate.postForEntity(endpointUrl, requestEntity, NehrDTO.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                NehrDTO nehrResponse = responseEntity.getBody();
                electronicHealthRecordRepository.save(newElectronicHealthRecord);
                patientRepository.save(newPatient);
                return newPatient;
            } else {
                throw new UnableToCreatePatientException("Failed to create patient. Server returned status code: " + responseEntity.getStatusCodeValue());
            }
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
            return patientsWithElectronicHealthRecordSummaryList;
        } catch (Exception ex) {
            throw new PatientNotFoundException(ex.getMessage());
        }
    }

    public Patient getPatientByUsername(String username) {
        return patientRepository.findByUsername(username).orElseThrow(() -> new PatientNotFoundException("Patient does not exist"));
    }

    public Boolean changePassword(String username, String oldPassword, String newPassword) throws UnableToChangePasswordException{
        Patient patient = getPatientByUsername(username);
        if (passwordEncoder.matches(oldPassword, patient.getPassword())) {
            if (newPassword.length() > 6) {
                try {
                    patient.setPassword(passwordEncoder.encode(newPassword));
                    return Boolean.TRUE;
                } catch (Exception ex) {
                    throw new UnableToChangePasswordException("New Password already in use");
                }
            } else {
                throw new UnableToChangePasswordException("New Password provided is too short");
            }
        } else {
            throw new UnableToChangePasswordException("Old Password provided is Incorrect");
        }
    }


    public List<String> findAllPatientsUsername() {
        List<Patient> list = patientRepository.findAll();
        List<String> allPatients = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            allPatients.add(list.get(i).getUsername());
        }
        return allPatients;
    }

    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }
}
