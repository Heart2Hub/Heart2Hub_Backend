// PrescriptionRecordController.java
package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.service.PrescriptionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescriptionRecord")
public class PrescriptionRecordController {

    @Autowired
    private PrescriptionRecordService prescriptionRecordService;


    @GetMapping("/getPrescriptionRecordsByEHRId/{id}")
    public ResponseEntity<List<PrescriptionRecord>> getPrescriptionRecordsByEHRId(@PathVariable Long id) {
        return ResponseEntity.ok().body(prescriptionRecordService.getPrescriptionRecordsByEHRId(id));
    }

    @PutMapping("/updatePrescriptionRecord/{id}")
    public ResponseEntity<PrescriptionRecord> updatePrescriptionRecord(
            @PathVariable(value = "id") Long prescriptionRecordId,
            @RequestBody Map<String, Object> requestBody) {
        String description = requestBody.get("description").toString();
        String comments = requestBody.get("comments").toString();

        Integer medicationQuantity = Integer.parseInt(requestBody.get("medicationQuantity").toString());
        Integer dosage = Integer.parseInt(requestBody.get("dosage").toString());


        PrescriptionRecord updatedPrescriptionRecord = prescriptionRecordService.updatePrescriptionRecord(prescriptionRecordId, medicationQuantity,
                dosage, description, comments);
        return ResponseEntity.ok(updatedPrescriptionRecord);
    }

//    @PostMapping
//    public PrescriptionRecord createPrescriptionRecord(@RequestBody PrescriptionRecord prescriptionRecord) {
//        return prescriptionRecordService.createPrescriptionRecord(prescriptionRecord);
//    }


    @DeleteMapping("/deletePrescriptionRecord/{id}")
    public ResponseEntity<?> deletePrescriptionRecord(@PathVariable Long id) {
        prescriptionRecordService.deletePrescriptionRecord(id);
        return ResponseEntity.ok().build();
    }
}
