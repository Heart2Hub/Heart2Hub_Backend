package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.PatientService;
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

    @GetMapping("/validateNric")
    public ResponseEntity<String> validateNric(@RequestParam("nric") String nric) {
        return ResponseEntity.ok(patientService.validateNric(nric));
    }

    @PostMapping(value = "/createPatient", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Patient> createPatient(@RequestBody Map<String, Object> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        Patient patient = objectMapper.convertValue(requestBody.get("patient"), Patient.class);
        ElectronicHealthRecord ehr = objectMapper.convertValue(requestBody.get("ehr"), ElectronicHealthRecord.class);
        return ResponseEntity.ok(patientService.createPatient(patient, ehr));
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

    @PostMapping("/createPatientWithNehr")
    public ResponseEntity<Patient> createPatientWithNehr(
            @RequestParam String nric,
            @RequestBody Patient newPatient) {
        return ResponseEntity.ok(
                patientService.createPatient(newPatient,nric)
        );
    }

    @PostMapping("/createPatientWithoutNehr")
    public ResponseEntity<Patient> createPatientWithoutNehr(
            @RequestBody Map<String, Object> newPatientRequest) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Patient newPatient = objectMapper.readValue(objectMapper.writeValueAsString(newPatientRequest.get("newPatient")), Patient.class);
        ElectronicHealthRecord newElectronicHealthRecord = objectMapper.readValue(objectMapper.writeValueAsString(newPatientRequest.get("newElectronicHealthRecord")), ElectronicHealthRecord.class);
        return ResponseEntity.ok(
                patientService.createPatient(newPatient,newElectronicHealthRecord)
        );
    }

}
