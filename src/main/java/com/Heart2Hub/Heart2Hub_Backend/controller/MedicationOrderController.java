package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicationOrder;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicationOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicationOrder")
@RequiredArgsConstructor
public class MedicationOrderController {

    private final MedicationOrderService medicationOrderService;

    @PostMapping("/createMedicationOrder")
    public ResponseEntity<MedicationOrder> createMedicationOrder(
            @RequestParam("medicationId") Long medicationId, @RequestParam("admissionId") Long admissionId,
            @RequestBody MedicationOrder medicationOrder) {
        return ResponseEntity.ok(
                medicationOrderService.createMedicationOrder(medicationId,admissionId, medicationOrder)
        );
    }

    @GetMapping("/getAllMedicationOrdersOfAdmission")
    public ResponseEntity<List<MedicationOrder>> getAllMedicationOrdersOfAdmission(
            @RequestParam Long admissionId) {
        System.out.println("Checking Order");
        return ResponseEntity.ok(
                medicationOrderService.getAllMedicationOrdersOfAdmission(admissionId)
        );
    }

    @DeleteMapping("/deleteMedicationOrder")
    public ResponseEntity<String> deleteMedicationOrder(
            @RequestParam("medicationOrderId") Long medicationOrderId, @RequestParam("admissionId") Long admissionId ) {
        return ResponseEntity.ok(medicationOrderService.deleteMedicationOrder(medicationOrderId, admissionId)
        );
    }

    @GetMapping("/getAllMedicationOrders")
    public ResponseEntity<List<MedicationOrder>> getAllMedicationOrders() {
        System.out.println("Checking Order");
        return ResponseEntity.ok(
                medicationOrderService.getAllMedicationOrders()
        );
    }

}
