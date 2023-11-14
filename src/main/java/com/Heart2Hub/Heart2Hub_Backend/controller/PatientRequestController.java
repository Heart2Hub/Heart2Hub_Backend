package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.PatientRequest;
import com.Heart2Hub.Heart2Hub_Backend.service.PatientRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patientRequest")
@RequiredArgsConstructor
public class PatientRequestController {

    private final PatientRequestService patientRequestService;

    @GetMapping("/getPatientRequests")
    public ResponseEntity<List<PatientRequest>> getPatientRequests(@RequestParam("username") String username) {
        return ResponseEntity.ok(patientRequestService.getPatientRequests(username));
    }

    @PostMapping("/createPatientRequest")
    public ResponseEntity<PatientRequest> createPatientRequest(
            @RequestParam("requestName") String requestName,
            @RequestParam("username") String username) {
        return ResponseEntity.ok(patientRequestService.createPatientRequest(requestName, username));
    }

    @DeleteMapping("/deletePatientRequest")
    public ResponseEntity<String> deletePatientRequest(
            @RequestParam("requestName") String requestName,
            @RequestParam("username") String username) {
        return ResponseEntity.ok(patientRequestService.deletePatientRequest(requestName, username));
    }
}
