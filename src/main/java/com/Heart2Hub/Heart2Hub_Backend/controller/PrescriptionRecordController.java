// PrescriptionRecordController.java
package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.InventoryItem;
import com.Heart2Hub.Heart2Hub_Backend.entity.PrescriptionRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.TransactionItem;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PrescriptionStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.PrescriptionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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

    @GetMapping("/getAllPrescriptionRecords")
    public ResponseEntity<List<PrescriptionRecord>> getAllPrescriptionRecords() {
        return ResponseEntity.ok().body(prescriptionRecordService.getAllPrescriptionRecords());
    }

    @GetMapping("/getAllInventoryItems")
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems() {
        return ResponseEntity.ok().body(prescriptionRecordService.getAllInventoryItems());
    }

    @PutMapping("/updatePrescriptionRecord/{id}")
    public ResponseEntity<PrescriptionRecord> updatePrescriptionRecord(
            @PathVariable(value = "id") Long prescriptionRecordId,
            @RequestBody Map<String, Object> requestBody) {
        String description = requestBody.get("description").toString();
        String comments = requestBody.get("comments").toString();

        //Integer medicationQuantity = Integer.parseInt(requestBody.get("medicationQuantity").toString());
        Integer dosage = Integer.parseInt(requestBody.get("dosage").toString());
        //PrescriptionStatusEnum prescriptionStatusEnum = PrescriptionStatusEnum.valueOf(requestBody.get("prescriptionStatusEnum").toString());
        LocalDate date = LocalDate.parse(requestBody.get("expirationDate").toString());
        LocalDateTime expirationDate = date.atTime(0,0,0);

        PrescriptionRecord updatedPrescriptionRecord = prescriptionRecordService.updatePrescriptionRecord(prescriptionRecordId,
                dosage, description, comments, expirationDate);
        return ResponseEntity.ok(updatedPrescriptionRecord);
    }

    @PostMapping("/createNewPrescription/{ehrId}/{itemId}")
    public ResponseEntity<PrescriptionRecord> createNewPrescriptionRecord(
            @RequestBody  Map<String, Object> requestBody,
            @PathVariable Long ehrId,
            @PathVariable Long itemId
    ) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss", Locale.ENGLISH);
        LocalDateTime createdDate = LocalDateTime.parse(requestBody.get("createdDate").toString(), inputFormatter);
        LocalDateTime expirationDate = LocalDateTime.parse(requestBody.get("expirationDate").toString(), inputFormatter);


        String medicationName = "";
        //Integer medicationQuantity = Integer.parseInt(requestBody.get("medicationQuantity").toString());
        Integer dosage = Integer.parseInt(requestBody.get("dosage").toString());
        String description = requestBody.get("description").toString();
        String comments = requestBody.get("comments").toString();
        String prescribedBy = requestBody.get("prescribedBy").toString();
        PrescriptionStatusEnum prescriptionStatusEnum = PrescriptionStatusEnum.valueOf(requestBody.get("prescriptionStatusEnum").toString());

        PrescriptionRecord prescriptionRecord = new PrescriptionRecord(createdDate, medicationName, 0, dosage, description, comments,
                prescribedBy, prescriptionStatusEnum, null);
        prescriptionRecord.setExpirationDate(expirationDate);

        PrescriptionRecord createdRecord = prescriptionRecordService.doctorCreateNewPrescription(prescriptionRecord, itemId, ehrId);
        return ResponseEntity.ok(createdRecord);
    }

    @PostMapping("/checkOutPrescription/{prescriptionId}/{ehrId}")
    public ResponseEntity<TransactionItem> checkOutPrescriptionRecord(
            @PathVariable Long prescriptionId,
            @PathVariable Long ehrId
    ) {
        TransactionItem transactionItem = prescriptionRecordService.checkOutPrescription(prescriptionId, ehrId);
        return ResponseEntity.ok(transactionItem);
    }

    @DeleteMapping("/deletePrescriptionRecord/{id}")
    public ResponseEntity<?> deletePrescriptionRecord(@PathVariable Long id) {
        prescriptionRecordService.deletePrescriptionRecord(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPrescriptionRecordsByNric")
    public ResponseEntity<List<PrescriptionRecord>> getPrescriptionRecordsByEHRId(
            @RequestParam("nric") String nric) {
        return ResponseEntity.ok().body(prescriptionRecordService.getPrescriptionRecordByNric(nric));
    }
}
