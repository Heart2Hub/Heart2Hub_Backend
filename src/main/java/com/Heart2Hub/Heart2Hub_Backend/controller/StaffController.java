package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.SubDepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

  private final StaffService staffService;

//  @PostMapping("/createStaff")
//  public ResponseEntity<Staff> createStaff(
//      @RequestParam("username") String username,
//      @RequestParam("password") String password,
//      @RequestParam("firstname") String firstname,
//      @RequestParam("lastname") String lastname,
//      @RequestParam("mobileNumber") Long mobileNumber,
//<<<<<<< HEAD
//      @RequestParam("staffRoleEnum") String staffRoleEnum,
//      @RequestParam("departmentName") String departmentName) {
//    return ResponseEntity.ok(
//        staffService.createStaff(username, password, firstname, lastname, mobileNumber,
//            StaffRoleEnum.valueOf(staffRoleEnum), departmentName));
//=======
//      @RequestParam("roleEnum") String roleEnum,
//      @RequestParam("isHead") Boolean isHead){
//    return ResponseEntity.ok(
//        staffService.createStaff(username, password, firstname, lastname, mobileNumber,
//            RoleEnum.valueOf(roleEnum), isHead));
//>>>>>>> feature/IHIS-53-manage-shifts
//  }
//  @PostMapping(value="/createStaff/{subDepartmentName}", consumes={"application/json"}, produces={"application/json"})
//  public ResponseEntity createStaff(@PathVariable String subDepartmentName, @RequestBody Staff staff) {
//    try {
//      return ResponseEntity.ok(staffService.createStaff(staff, subDepartmentName));
//    } catch (SubDepartmentNotFoundException ex) {
//      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//    }
//  }

  @PostMapping(value="/createStaff/{subDepartmentName}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity<Staff> createStaff(@PathVariable String subDepartmentName,@RequestBody Staff staff) {
      return ResponseEntity.ok(staffService.createStaff(staff, subDepartmentName));
  }

  @PutMapping(value="/updateStaff/{subDepartmentName}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity<Staff> updateStaff(@PathVariable String subDepartmentName, @RequestBody Staff staff) {
    return ResponseEntity.ok(staffService.updateStaff(staff, subDepartmentName));
  }

  @PutMapping("/disableStaff/{username}")
  public ResponseEntity<Staff> disableStaff(@PathVariable String username) {
    return ResponseEntity.ok(staffService.disableStaff(username));
  }

  @PostMapping("/staffLogin")
  public ResponseEntity<String> staffLogin(
      @RequestParam("username") String username,
      @RequestParam("password") String password) {

    System.out.println(username);
    System.out.println(password);
    String jwtToken = staffService.authenticateStaff(username,password);
    System.out.println("working fine");
    return ResponseEntity.ok(jwtToken);
  }

  @GetMapping("/getAllStaff")
  public ResponseEntity<List<Staff>> getAllStaff() {
    return ResponseEntity.ok(staffService.getAllStaff());
  }

  @GetMapping("/getStaffByUsername")
  public ResponseEntity<Staff> getStaffByUsername(
    @RequestParam("username") String username) {
    return ResponseEntity.ok(staffService.getStaffByUsername(username));
  }

  @GetMapping("/getStaffRoles")
  public ResponseEntity<List<String>> getStaffRoles() {
    return ResponseEntity.ok(staffService.getStaffRoles());
  }

}
