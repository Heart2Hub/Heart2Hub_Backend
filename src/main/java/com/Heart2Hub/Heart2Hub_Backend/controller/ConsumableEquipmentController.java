package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Leave;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.service.ConsumableEquipmentService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consumableEquipment")
@RequiredArgsConstructor
public class ConsumableEquipmentController {

    private final ConsumableEquipmentService consumableEquipmentService;
    private final StaffService staffService;

    // As an admin, I can view all consumable equipments
    @GetMapping("/getAllConsumableEquipment")
    public ResponseEntity<List<ConsumableEquipment>> getAllConsumableEquipment() {
        return ResponseEntity.ok(consumableEquipmentService.getAllConsumableEquipmentByName());
    }

    @PostMapping("/createConsumableEquipment")
    public ResponseEntity<ConsumableEquipment> createConsumableEquipment(@RequestBody ConsumableEquipment consumableEquipment) {
        return ResponseEntity.ok(
                consumableEquipmentService.createConsumableEquipment(consumableEquipment)
        );
    }

    @DeleteMapping("/deleteConsumableEquipment")
    public ResponseEntity<String> deleteConsumableEquipment(
            @RequestParam("inventoryItemId") Long inventoryItemId) {
        return ResponseEntity.ok(consumableEquipmentService.deleteConsumableEquipment(inventoryItemId)
        );
    }

    @PutMapping("/updateConsumableEquipment")
    public ResponseEntity<ConsumableEquipment> updateConsumableEquipment(
            @RequestParam("inventoryItemId") Long inventoryItemId,
            @RequestBody ConsumableEquipment updatedConsumableEquipment) {
        return ResponseEntity.ok(consumableEquipmentService.updateConsumableEquipment(inventoryItemId,updatedConsumableEquipment)
        );
    }

}
