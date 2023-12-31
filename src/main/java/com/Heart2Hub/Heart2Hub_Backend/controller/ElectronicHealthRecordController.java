package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.service.ElectronicHealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/electronicHealthRecord")
@RequiredArgsConstructor
public class ElectronicHealthRecordController {

    private final ElectronicHealthRecordService electronicHealthRecordService;

    @GetMapping("/getElectronicHealthRecordByIdAndDateOfBirth")
    public ResponseEntity<ElectronicHealthRecord> getElectronicHealthRecordByIdAndDateOfBirth(
            @RequestParam("electronicHealthRecordId") Long electronicHealthRecordId,
            @RequestParam("dateOfBirth") LocalDateTime dateOfBirth) {
        return ResponseEntity.ok(
                electronicHealthRecordService.getElectronicHealthRecordByIdAndDateOfBirth(electronicHealthRecordId,dateOfBirth)
        );
    }

    @GetMapping("/getElectronicHealthRecordById")
    public ResponseEntity<ElectronicHealthRecord> getElectronicHealthRecordById(
        @RequestParam("electronicHealthRecordId") Long electronicHealthRecordId) {
        return ResponseEntity.ok(
            electronicHealthRecordService.getElectronicHealthRecordById(electronicHealthRecordId)
        );
    }

    @GetMapping("/getElectronicHealthRecordByNric")
    public ResponseEntity<ElectronicHealthRecord> getElectronicHealthRecordByNric(
            @RequestParam("nric") String nric) {
        return ResponseEntity.ok(
                electronicHealthRecordService.findByNric(nric)
        );
    }

    @GetMapping("/getAllElectronicHealthRecords")
    public ResponseEntity<List<ElectronicHealthRecord>> getAllElectronicHealthRecords() {
        return ResponseEntity.ok(
                electronicHealthRecordService.getAllElectronicHealthRecords()
        );
    }

    @GetMapping("/getElectronicHealthRecordByUsername")
    public ResponseEntity<ElectronicHealthRecord> getElectronicHealthRecordByUsername(@RequestParam("username") String username) {
        return ResponseEntity.ok(
                electronicHealthRecordService.getElectronicHealthRecordByUsername(username)
        );
    }

    @GetMapping("/getNehrRecord")
    public ResponseEntity<ElectronicHealthRecord> getNehrRecord(
            @RequestParam("nric") String nric) {
        return ResponseEntity.ok(
                electronicHealthRecordService.getNehrRecordByNric(nric)
        );
    }


    @PutMapping("/updateElectronicHealthRecord")
    public ResponseEntity<ElectronicHealthRecord> updateElectronicHealthRecord(
            @RequestParam("electronicHealthRecordId") Long electronicHealthRecordId,
            @RequestBody ElectronicHealthRecord newElectronicHealthRecord) {
        return ResponseEntity.ok(
                electronicHealthRecordService.updateElectronicHealthRecord(electronicHealthRecordId, newElectronicHealthRecord)
        );
    }

}



