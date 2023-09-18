package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.SubDepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.service.SubDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sub-department")
@RequiredArgsConstructor
public class SubDepartmentController {

    private final SubDepartmentService subDepartmentService;

    @GetMapping("/getAllSubDepartments")
    public ResponseEntity<List<SubDepartment>> getAllSubDepartments() {
        return ResponseEntity.ok(subDepartmentService.getAllSubDepartments());
    }

    @GetMapping("/getSubDepartmentsByDepartment/{departmentName}")
    public ResponseEntity getSubDepartmentsByDepartment(@PathVariable String departmentName) {
        try {
            return ResponseEntity.ok(subDepartmentService.getSubDepartmentsByDepartment(departmentName));
        } catch (DepartmentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
