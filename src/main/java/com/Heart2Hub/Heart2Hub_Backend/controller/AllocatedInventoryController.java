package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.AllocatedInventory;
import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.service.AllocatedInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/allocatedInventory")
@RequiredArgsConstructor
public class AllocatedInventoryController {

    private final AllocatedInventoryService allocatedInventoryService;

    @GetMapping("/findAllAllocatedInventoryOfFacility/{id}")
    public ResponseEntity<List<AllocatedInventory>> findAllAllocatedInventoryOfFacility(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(allocatedInventoryService.findAllAllocatedInventoryOfFacility(id));
    }

    @PostMapping("/createAllocatedInventory")
    public ResponseEntity<AllocatedInventory> createAllocatedInventory(@RequestBody Map<String, Object> requestBody) {
        Long inventoryItemId = Long.parseLong(requestBody.get("inventoryItemId").toString());
        Integer quantity = Integer.parseInt(requestBody.get("quantity").toString());
        Integer minQuantity = Integer.parseInt(requestBody.get("minQuantity").toString());
        Long selectedFacilityId = Long.parseLong(requestBody.get("selectedFacilityId").toString());

        return ResponseEntity.ok(allocatedInventoryService.createAllocatedInventory(selectedFacilityId, inventoryItemId, quantity, minQuantity));
    }

    @PutMapping("/updateAllocatedInventory")
    public ResponseEntity<AllocatedInventory> updateAllocatedInventory(@RequestBody Map<String, Object> requestBody) {
        Long inventoryId = Long.parseLong(requestBody.get("allocatedInventoryIdForUpdate").toString());
        Integer newQuantity = Integer.parseInt(requestBody.get("newQuantity").toString());
        return ResponseEntity.ok(allocatedInventoryService.updateAllocatedInventory(inventoryId,newQuantity));
    }

    @DeleteMapping("deleteAllocatedInventory/{id}")
    public ResponseEntity<?> deleteAllocatedInventory(@PathVariable(value = "id") Long id) {
        allocatedInventoryService.deleteAllocatedInventory(id);
        return ResponseEntity.ok().build();

    }
}
