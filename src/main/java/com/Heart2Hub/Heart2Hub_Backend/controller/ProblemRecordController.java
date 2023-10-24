package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import com.Heart2Hub.Heart2Hub_Backend.service.ProblemRecordService;
import com.Heart2Hub.Heart2Hub_Backend.service.ProblemRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problemRecord")
@RequiredArgsConstructor
public class ProblemRecordController {

    private final ProblemRecordService problemRecordService;

    @PostMapping("/createProblemRecord")
    public ResponseEntity<ProblemRecord> createProblemRecord(
            @RequestParam Long electronicHealthRecordId,
            @RequestBody ProblemRecord newProblemRecord) {
        return ResponseEntity.ok(
                problemRecordService.createProblemRecord(electronicHealthRecordId,newProblemRecord)
        );
    }

    @PostMapping("/createAllergyRecord")
    public ResponseEntity<MedicalHistoryRecord> createAllergyRecord(
        @RequestParam Long electronicHealthRecordId,
        @RequestBody ProblemRecord newProblemRecord) {
        return ResponseEntity.ok(
            problemRecordService.createAllergyRecord(electronicHealthRecordId,newProblemRecord)
        );
    }

    @GetMapping("/getAllProblemRecordsByElectronicHealthRecordId")
    public ResponseEntity<List<ProblemRecord>> getAllProblemRecordsByElectronicHealthRecordId(
            @RequestParam("electronicHealthRecordId") Long electronicHealthRecordId) {
        return ResponseEntity.ok(
                problemRecordService.getAllProblemRecordsByElectronicHealthRecordId(electronicHealthRecordId)
        );
    }

    @DeleteMapping("/deleteProblemRecord")
    public ResponseEntity<String> deleteProblemRecord(
            @RequestParam("electronicHealthRecordId") Long electronicHealthRecordId,
            @RequestParam("problemRecordId") Long problemRecordId) {
        return ResponseEntity.ok(problemRecordService.deleteProblemRecord(electronicHealthRecordId,problemRecordId)
        );
    }

    @PutMapping("/updateProblemRecord")
    public ResponseEntity<ProblemRecord> updateProblemRecord(
            @RequestParam("problemRecordId") Long problemRecordId,
            @RequestBody ProblemRecord updatedProblemRecord) {
        return ResponseEntity.ok(problemRecordService.updateProblemRecord(problemRecordId,updatedProblemRecord)
        );
    }

    @PostMapping("/resolveProblemRecord")
    public ResponseEntity<MedicalHistoryRecord> resolveProblemRecord(
            @RequestParam Long electronicHealthRecordId,
            @RequestParam("problemRecordId") Long problemRecordId) {
        return ResponseEntity.ok(
                problemRecordService.resolveProblemRecord(electronicHealthRecordId,problemRecordId)
        );
    }

}
