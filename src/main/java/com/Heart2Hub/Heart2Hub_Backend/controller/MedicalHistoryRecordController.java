package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicalHistoryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalHistoryRecord")
@RequiredArgsConstructor
public class MedicalHistoryRecordController {

    private final MedicalHistoryRecordService medicalHistoryRecordService;

    @PostMapping("/createMedicalHistoryRecord")
    public ResponseEntity<MedicalHistoryRecord> createMedicalHistoryRecord(
            @RequestParam Long electronicHealthRecordId,
            @RequestBody MedicalHistoryRecord newMedicalHistoryRecord) {
        return ResponseEntity.ok(
                medicalHistoryRecordService.createMedicalHistoryRecord(electronicHealthRecordId,newMedicalHistoryRecord)
        );
    }

    @GetMapping("/getAllMedicalHistoryRecordsByElectronicHealthRecordId")
    public ResponseEntity<List<MedicalHistoryRecord>> getAllMedicalHistoryRecordsByElectronicHealthRecordId(
            @RequestParam("electronicHealthRecordId") Long electronicHealthRecordId) {
        return ResponseEntity.ok(
                medicalHistoryRecordService.getAllMedicalHistoryRecordsByElectronicHealthRecordId(electronicHealthRecordId)
        );
    }

    @DeleteMapping("/deleteMedicalHistoryRecord")
    public ResponseEntity<String> deleteMedicalHistoryRecord(
            @RequestParam("electronicHealthRecordId") Long electronicHealthRecordId,
            @RequestParam("medicalHistoryRecordId") Long medicalHistoryRecordId) {
        return ResponseEntity.ok(medicalHistoryRecordService.deleteMedicalHistoryRecord(electronicHealthRecordId,medicalHistoryRecordId)
        );
    }

    @PutMapping("/updateMedicalHistoryRecord")
    public ResponseEntity<MedicalHistoryRecord> updateMedicalHistoryRecord(
            @RequestParam("medicalHistoryRecordId") Long medicalHistoryRecordId,
            @RequestBody MedicalHistoryRecord updatedMedicalHistoryRecord) {
        return ResponseEntity.ok(medicalHistoryRecordService.updateMedicalHistoryRecord(medicalHistoryRecordId,updatedMedicalHistoryRecord)
        );
    }

}
