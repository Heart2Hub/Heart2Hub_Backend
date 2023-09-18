package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @PostMapping("/createFacility")
    public ResponseEntity<Facility> createFacility(
            @RequestParam Long subDepartmentId,
            @RequestBody Facility facility) {
        return ResponseEntity.ok(
                facilityService.createFacility(subDepartmentId,facility)
        );
    }

    @GetMapping("/getAllFacilitiesByFacilityStatus")
    public ResponseEntity<List<Facility>> getAllFacilitiesByFacilityStatus(
            @RequestParam("facilityStatus") String facilityStatus) {
        return ResponseEntity.ok(
                facilityService.getAllFacilitiesByFacilityStatus(facilityStatus)
        );
    }

    @GetMapping("/getAllFacilitiesByName")
    public ResponseEntity<List<Facility>> getAllFacilitiesByName(
            @RequestParam("name") String name) {
        return ResponseEntity.ok(
                facilityService.getAllFacilitiesByName(name)
        );
    }

    @DeleteMapping("/deleteFacility")
    public ResponseEntity<String> deleteFacility(
            @RequestParam("facilityId") Long facilityId) {
        return ResponseEntity.ok(facilityService.deleteFacility(facilityId)
        );
    }

    @PutMapping("/updateFacility")
    public ResponseEntity<Facility> updateFacility(
            @RequestParam("facilityId") Long facilityId,
            @RequestBody Facility updatedFacility) {
        return ResponseEntity.ok(facilityService.updateFacility(facilityId,updatedFacility)
        );
    }

}
