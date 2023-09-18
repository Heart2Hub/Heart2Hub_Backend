package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping("/createDepartment")
    public ResponseEntity<Department> createDepartment(
            @RequestBody Department department) {
        return ResponseEntity.ok(
                departmentService.createDepartment(department)
        );
    }

    @GetMapping("/getAllDepartments")
    public ResponseEntity<List<Department>> getAllDepartmentsByName(
            @RequestParam("name") String name) {
        return ResponseEntity.ok(
                departmentService.getAllDepartmentsByName(name)
        );
    }

    @DeleteMapping("/deleteDepartment")
    public ResponseEntity<String> deleteDepartment(
            @RequestParam("departmentId") Long departmentId) {
        return ResponseEntity.ok(departmentService.deleteDepartment(departmentId)
        );
    }

    @PutMapping("/updateDepartment")
    public ResponseEntity<Department> updateDepartment(
            @RequestParam("departmentId") Long departmentId,
            @RequestBody Department updatedDepartment) {
        return ResponseEntity.ok(departmentService.updateDepartment(departmentId,updatedDepartment)
        );
    }

}
