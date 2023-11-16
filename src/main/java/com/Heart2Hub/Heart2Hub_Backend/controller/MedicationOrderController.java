package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicationOrder;
import com.Heart2Hub.Heart2Hub_Backend.service.AppointmentService;
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
    private final AppointmentService appointmentService;

    @PostMapping("/createMedicationOrder")
    public ResponseEntity<MedicationOrder> createMedicationOrder(
            @RequestParam("medicationId") Long medicationId, @RequestParam("admissionId") Long admissionId,
            @RequestBody MedicationOrder medicationOrder) {

        MedicationOrder medicationOrder1 = medicationOrderService.createMedicationOrder(medicationId,admissionId, medicationOrder);
        appointmentService.sendUpdateToClients("swimlane");
        return ResponseEntity.ok(medicationOrder1);
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
        String msg = medicationOrderService.deleteMedicationOrder(medicationOrderId, admissionId);
        appointmentService.sendUpdateToClients("swimlane");
        return ResponseEntity.ok(msg);
    }

    @GetMapping("/getAllMedicationOrders")
    public ResponseEntity<List<MedicationOrder>> getAllMedicationOrders() {
        System.out.println("Checking Order");
        return ResponseEntity.ok(
                medicationOrderService.getAllMedicationOrders()
        );
    }

    @GetMapping("/getMedicationOrderById")
    public ResponseEntity<MedicationOrder> getMedicationOrderById(@RequestParam("medicationOrderId") Long medicationOrderId) {
        //System.out.println("Checking Order");
        return ResponseEntity.ok(medicationOrderService.getMedicationOrderById(medicationOrderId));
    }

    @PutMapping("/updateComplete")
    public ResponseEntity<MedicationOrder> updateComplete(@RequestParam("medicationOrderId") Long medicationOrderId, @RequestParam("admissionId") Long admissionId, @RequestParam("isCompleted") Boolean isCompleted) {
        MedicationOrder medicationOrder1 = medicationOrderService.updateComplete(medicationOrderId, admissionId, isCompleted);
        appointmentService.sendUpdateToClients("swimlane");
        return ResponseEntity.ok(medicationOrder1);
    }

}
