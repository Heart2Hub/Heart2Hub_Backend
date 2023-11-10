package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.InpatientStaffDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.OutpatientStaffDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.SubDepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.mapper.AppointmentMapper;
import com.Heart2Hub.Heart2Hub_Backend.mapper.InpatientStaffMapper;
import com.Heart2Hub.Heart2Hub_Backend.mapper.OutpatientStaffMapper;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

  private final StaffService staffService;

  private final OutpatientStaffMapper outpatientStaffMapper;

  private final InpatientStaffMapper inpatientStaffMapper;


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

  @PostMapping(value="/createStaff/{unitName}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity<Staff> createStaff(@PathVariable String unitName,@RequestBody Staff staff) {
      return ResponseEntity.ok(staffService.createStaff(staff, unitName));
  }

  @PostMapping(value = "/createStaffWithImage/{unitName}", consumes = {"application/json"}, produces = {"application/json"})
  public ResponseEntity<Staff> createStaffWithImage(@PathVariable String unitName, @RequestBody Map<String, Object> requestBody) {
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
    Staff staff = objectMapper.convertValue(requestBody.get("staff"), Staff.class);
    ImageDocument imageDocument = objectMapper.convertValue(requestBody.get("imageDocument"), ImageDocument.class);
    return ResponseEntity.ok(staffService.createStaff(staff, unitName, imageDocument));
  }

  @PutMapping(value="/updateStaff/{unitName}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity<Staff> updateStaff(@PathVariable String unitName, @RequestBody Staff staff) {
    return ResponseEntity.ok(staffService.updateStaff(staff, unitName));
  }

  @PutMapping(value = "/updateStaffWithImage/{unitName}", consumes = {"application/json"}, produces = {"application/json"})
  public ResponseEntity<Staff> updateStaffWithImage(@PathVariable String unitName, @RequestBody Map<String, Object> requestBody) {
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
    Staff staff = objectMapper.convertValue(requestBody.get("staff"), Staff.class);
    ImageDocument imageDocument = objectMapper.convertValue(requestBody.get("imageDocument"), ImageDocument.class);
    return ResponseEntity.ok(staffService.updateStaff(staff, unitName, imageDocument));
  }

  @PutMapping("/disableStaff/{username}")
  public ResponseEntity<Staff> disableStaff(@PathVariable String username) {
    return ResponseEntity.ok(staffService.disableStaff(username));
  }

  @PostMapping("/staffLogin")
  public ResponseEntity<String> staffLogin(
      @RequestParam("username") String username,
      @RequestParam("password") String password) {

    String jwtToken = staffService.authenticateStaff(username,password);
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

  @GetMapping("/getStaffByStaffId")
  public ResponseEntity<Staff> getStaffByStaffId(
      @RequestParam("staffId") Long staffId) {
    return ResponseEntity.ok(staffService.findById(staffId));
  }

  @GetMapping("/getAllHeadStaff")
  public ResponseEntity<List<Staff>> getAllHeadStaff() {
    return ResponseEntity.ok(staffService.getAllHeadStaff());
  }
  @GetMapping("/getStaffRoles")
  public ResponseEntity<List<String>> getStaffRoles() {
    return ResponseEntity.ok(staffService.getStaffRoles());
  }

  @GetMapping("/getStaffByRole")
  public ResponseEntity<List<Staff>> getStaffByRole(
          @RequestParam("role") String role, @RequestParam("unit") String unit) {
    return ResponseEntity.ok(staffService.getStaffByRole(role, unit));
  }

  @PutMapping ("/changePassword")
  public ResponseEntity<Boolean> changePassword(
      @RequestParam("username") String username,@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword) {
    return ResponseEntity.ok(staffService.changePassword(username,oldPassword,newPassword));
  }

  @GetMapping("/getStaffsWorkingInCurrentShiftAndDepartment")
  public ResponseEntity<List<OutpatientStaffDTO>> getStaffByRole(
      @RequestParam("departmentName") String departmentName) {

    List<OutpatientStaffDTO> listOfOutpatientStaff = staffService.getStaffsWorkingInCurrentShiftAndDepartment(
        departmentName).stream().map(outpatientStaffMapper::convertToDto).toList();
    return ResponseEntity.ok(listOfOutpatientStaff);
  }



  @GetMapping("/getStaffsWorkingInCurrentShiftAndWard")
  public ResponseEntity<List<InpatientStaffDTO>> getNurses(
          @RequestParam("wardName") String wardName) {

    List<InpatientStaffDTO> listOfNurses = staffService.getStaffsWorkingInCurrentShiftAndDepartment(
            wardName).stream().map(inpatientStaffMapper::convertToDto).toList();
    return ResponseEntity.ok(listOfNurses);
  }

  @GetMapping("/getStaffById")
  public ResponseEntity<Staff> getStaffById(
          @RequestParam("id") Long id) {
    return ResponseEntity.ok(staffService.getStaffById(id));
  }

  @GetMapping("/getStaffsInUnit")
  public ResponseEntity<List<Staff>> getStaffsInUnit(
          @RequestParam("unit") String unit) {
    return ResponseEntity.ok(staffService.getAllStaffByUnit(unit));
  }
}
