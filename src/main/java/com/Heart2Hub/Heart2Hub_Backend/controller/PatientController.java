package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
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

}
