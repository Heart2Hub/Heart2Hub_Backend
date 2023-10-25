package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Ward;
import com.Heart2Hub.Heart2Hub_Backend.service.DepartmentService;
import com.Heart2Hub.Heart2Hub_Backend.service.WardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ward")
@RequiredArgsConstructor
public class WardController {

    private final WardService wardService;

//    @PostMapping("/createDepartment")
//    public ResponseEntity<Department> createDepartment(
//            @RequestBody Department department) {
//        return ResponseEntity.ok(
//                departmentService.createDepartment(department)
//        );
//    }

    @GetMapping("/getAllWards")
    public ResponseEntity<List<Ward>> getAllWardsByName(
            @RequestParam("name") String name) {
        return ResponseEntity.ok(
                wardService.getAllWardsByName(name)
        );
    }

    @GetMapping("/getAllWardsByWardClass")
    public ResponseEntity<List<Ward>> getAllWardsByWardClass(@RequestParam("wardClass") String wardClass) {
        return ResponseEntity.ok(wardService.getAllWardsByWardClass(wardClass));
    }

//    @GetMapping("/getCurrentDayAdmissions")
//    public ResponseEntity<Admission> getCurrentDayAdmissions(@RequestParam("ward") String ward) {
//        return ResponseEntity.ok(wardService.getCurrentDayAdmissionsForWard(ward));
//    }


}
