package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.PatientService;
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
