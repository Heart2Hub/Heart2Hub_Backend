package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

  private final StaffService staffService;

  @PostMapping("/createStaff")
  public ResponseEntity<Staff> createStaff(
      @RequestParam("username") String username,
      @RequestParam("password") String password,
      @RequestParam("firstname") String firstname,
      @RequestParam("lastname") String lastname,
      @RequestParam("mobileNumber") Long mobileNumber,
      @RequestParam("roleEnum") String roleEnum,
      @RequestParam("isHead") Boolean isHead){
    return ResponseEntity.ok(
        staffService.createStaff(username, password, firstname, lastname, mobileNumber,
            RoleEnum.valueOf(roleEnum), isHead));
  }

  @PostMapping("/staffLogin")
  public ResponseEntity<String> staffLogin(
      @RequestParam("username") String username,
      @RequestParam("password") String password) {

    String jwtToken = staffService.authenticateStaff(username,password);
    return ResponseEntity.ok(jwtToken);
  }

  @GetMapping("/getStaffByUsername")
  public ResponseEntity<Staff> getStaffByUsername(
          @RequestParam("username") String username) {
    return ResponseEntity.ok(staffService.getStaffByUsername(username));
  }

}
