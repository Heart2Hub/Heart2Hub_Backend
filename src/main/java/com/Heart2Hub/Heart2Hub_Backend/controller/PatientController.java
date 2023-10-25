package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.PatientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    private final NextOfKinRecordService nextOfKinRecordService;

    @GetMapping("/validateNric")
    public ResponseEntity<String> validateNric(@RequestParam("nric") String nric) {
        return ResponseEntity.ok(patientService.validateNric(nric));
    }

    @GetMapping("/getNextOfKinRecords")
    public ResponseEntity<List<NextOfKinRecord>> getNextOfKinRecords(@RequestParam("ehrId") Long ehrId) {
        return ResponseEntity.ok(nextOfKinRecordService.getNextOfKinRecordsByEHRId(ehrId));
    }

    @PostMapping("/patientLogin")
    public ResponseEntity<String> patientLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        String jwtToken = patientService.authenticatePatient(username,password);
        return ResponseEntity.ok(jwtToken);
    }

    @GetMapping("/getAllPatientsWithElectronicHealthRecordSummaryByName")
    public ResponseEntity<List<Map<String, Object>>> getAllPatientsWithElectronicHealthRecordSummaryByName(
            @RequestParam("name") String name) throws JSONException {
        List<JSONObject> patientSummaries = patientService.getAllPatientsWithElectronicHealthRecordSummaryByName(name);
        List<Map<String, Object>> result = new ArrayList<>();
        for (JSONObject jsonObject : patientSummaries) {
            Map<String, Object> map = new HashMap<>();
            for (Iterator it = jsonObject.keys(); it.hasNext(); ) {
                Object key = it.next();
                String keyStr = (String) key;
                Object value = jsonObject.get(keyStr);
                map.put(keyStr, value);
            }
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping ("/changePassword")
    public ResponseEntity<Boolean> changePassword(
            @RequestParam("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        return ResponseEntity.ok(patientService.changePassword(username,oldPassword,newPassword));
    }

    @PostMapping("/createPatientWithNehr")
    public ResponseEntity<Patient> createPatientWithNehr(
            @RequestParam String nric,
            @RequestBody Map<String, Object> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        Patient newPatient = objectMapper.convertValue(requestBody.get("newPatient"), Patient.class);
        ImageDocument imageDocument = objectMapper.convertValue(requestBody.get("imageDocument"), ImageDocument.class);
        return ResponseEntity.ok(
                patientService.createPatient(newPatient, nric, imageDocument)
        );
    }

    @PostMapping("/createPatientWithoutNehr")
    public ResponseEntity<Patient> createPatientWithoutNehr(
            @RequestBody Map<String, Object> requestBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        Patient newPatient = objectMapper.readValue(objectMapper.writeValueAsString(requestBody.get("newPatient")), Patient.class);
        ElectronicHealthRecord newElectronicHealthRecord = objectMapper.readValue(objectMapper.writeValueAsString(requestBody.get("newElectronicHealthRecord")), ElectronicHealthRecord.class);
        ImageDocument imageDocument = objectMapper.convertValue(requestBody.get("imageDocument"), ImageDocument.class);
        return ResponseEntity.ok(
                patientService.createPatient(newPatient, newElectronicHealthRecord, imageDocument)
        );
    }

    @GetMapping("/findAllPatientsUsername")
    public ResponseEntity<List<String>> findAllPatientsUsername() {

        return ResponseEntity.ok(patientService.findAllPatientsUsername());
    }

    @GetMapping("/findAllPatients")
    public ResponseEntity<List<Patient>> findAllPatients() {

        return ResponseEntity.ok(patientService.findAllPatients());
    }

    @GetMapping("/getPatientByUsername")
    public ResponseEntity<Patient> getPatientByUsername(@RequestParam String username) {
        return ResponseEntity.ok(patientService.getPatientByUsername(username));
    }

}
