package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateSubDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.Heart2Hub.Heart2Hub_Backend.service.SubDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subDepartment")
@RequiredArgsConstructor
public class SubDepartmentController {

    private final SubDepartmentService subDepartmentService;

    @PostMapping("/createSubDepartment")
    public ResponseEntity<SubDepartment> createSubDepartment(
            @RequestParam Long departmentId,
            @RequestBody SubDepartment subDepartment) {
        return ResponseEntity.ok(
                subDepartmentService.createSubDepartment(departmentId,subDepartment)
        );
    }

    @GetMapping("/getAllSubDepartments")
    public ResponseEntity<List<SubDepartment>> getAllSubDepartmentsByName(
            @RequestParam("name") String name) {
        return ResponseEntity.ok(
                subDepartmentService.getAllSubDepartmentsByName(name)
        );
    }

    @DeleteMapping("/deleteSubDepartment")
    public ResponseEntity<String> deleteSubDepartment(
            @RequestParam("subDepartmentId") Long subDepartmentId) {
        return ResponseEntity.ok(subDepartmentService.deleteSubDepartment(subDepartmentId)
        );
    }

    @PutMapping("/updateSubDepartment")
    public ResponseEntity<SubDepartment> updateSubDepartment(
            @RequestParam("subDepartmentId") Long subDepartmentId,
            @RequestBody SubDepartment updatedSubDepartment) {
        return ResponseEntity.ok(subDepartmentService.updateSubDepartment(subDepartmentId,updatedSubDepartment)
        );
    }

}
