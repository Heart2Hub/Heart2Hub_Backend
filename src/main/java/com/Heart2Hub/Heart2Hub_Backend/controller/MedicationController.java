package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import com.Heart2Hub.Heart2Hub_Backend.service.ConsumableEquipmentService;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicationService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medication")
@RequiredArgsConstructor
public class MedicationController {
    private final MedicationService medicationService;
    private final StaffService staffService;

    // As an admin, I can view all consumable equipments
    @GetMapping("/getAllMedication")
    public ResponseEntity<List<Medication>> getAllMedication() {
        return ResponseEntity.ok(medicationService.getAllMedicationByName());
    }

    @PostMapping("/createMedication")
    public ResponseEntity<Medication> createMedication(@RequestBody Medication medication) {
        return ResponseEntity.ok(
                medicationService.createMedication(medication)
        );
    }

    @DeleteMapping("/deleteMedication")
    public ResponseEntity<String> deleteMedication(
            @RequestParam("inventoryItemId") Long inventoryItemId) {
        return ResponseEntity.ok(medicationService.deleteMedication(inventoryItemId)
        );
    }

    @PutMapping("/updateMedication")
    public ResponseEntity<Medication> updateMedication(
            @RequestParam("inventoryItemId") Long inventoryItemId,
            @RequestBody Medication updatedMedication) {
        return ResponseEntity.ok(medicationService.updateMedication(inventoryItemId,updatedMedication)
        );
    }

    @GetMapping("/getAllergenEnums")
    public ResponseEntity<List<String>> getAllergenEnums() {
        return ResponseEntity.ok(medicationService.getAllergenEnums());
    }

    @GetMapping("/findMedicationByInventoryItemId")
    public ResponseEntity<Medication> findMedicationByInventoryItemId(
            @RequestParam("inventoryItemId") Long inventoryItemId) {

        return ResponseEntity.ok(medicationService.findMedicationByInventoryItemId(inventoryItemId));
    }

    @GetMapping("/getAllMedicationsByAllergy/{pId}")
    public ResponseEntity<List<Medication>> getAllMedicationsByAllergy(@PathVariable Long pId) {

        return ResponseEntity.ok(medicationService.getAllMedicationsByAllergy(pId));
    }

}
