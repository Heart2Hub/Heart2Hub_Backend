package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import com.Heart2Hub.Heart2Hub_Backend.entity.ServiceItem;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicationService;
import com.Heart2Hub.Heart2Hub_Backend.service.ServiceItemService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/serviceItem")
@RequiredArgsConstructor
public class ServiceItemController {
    private final ServiceItemService serviceItemService;
        private final StaffService staffService;

        // As an admin, I can view all consumable equipments
        @GetMapping("/getAllServiceItem")
        public ResponseEntity<List<ServiceItem>> getAllServiceItem() {
            return ResponseEntity.ok(serviceItemService.getAllServiceItem());
        }

        @PostMapping("/createServiceItem")
        public ResponseEntity<ServiceItem> createServiceItem(@RequestParam Long unitId, @RequestBody ServiceItem serviceItem) {
           System.out.println("unit Id is here " + unitId);
            return ResponseEntity.ok(
                    serviceItemService.createServiceItem(unitId,serviceItem)
            );
        }

        @DeleteMapping("/deleteServiceItem")
        public ResponseEntity<String> deleteServiceItem(
                @RequestParam("inventoryItemId") Long inventoryItemId) {
            return ResponseEntity.ok(serviceItemService.deleteServiceItem(inventoryItemId)
            );
        }

        @PutMapping("/updateServiceItem")
        public ResponseEntity<ServiceItem> updateServiceItem(
                @RequestParam("inventoryItemId") Long inventoryItemId,
                @RequestBody ServiceItem updatedServiceItem) {
            return ResponseEntity.ok(serviceItemService.updateServiceItem(inventoryItemId,updatedServiceItem)
            );
        }

}
