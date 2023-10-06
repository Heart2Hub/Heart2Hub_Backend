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
@RequestMapping("/nextOfKinRecord")
@RequiredArgsConstructor
public class NextOfKinRecordController {

    private final NextOfKinRecordService nextOfKinRecordService;

    @PostMapping("/createNextOfKinRecord")
    public ResponseEntity<NextOfKinRecord> createNextOfKinRecord(
            @RequestParam("ehrId") Long ehrId,
            @RequestBody NextOfKinRecord newNextOfKinRecord) {
        return ResponseEntity.ok(nextOfKinRecordService.createNextOfKinRecord(ehrId, newNextOfKinRecord));
    }
}
