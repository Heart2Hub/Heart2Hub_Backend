package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffRoleNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.ShiftNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateShiftException;
import com.Heart2Hub.Heart2Hub_Backend.service.ShiftService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/shift")
@RequiredArgsConstructor
public class ShiftController {

  private final ShiftService shiftService;

  private final StaffService staffService;

  @PostMapping(value="/createShift/{staffUsername}/{facilityId}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity createShift(@PathVariable String staffUsername, @PathVariable Long facilityId, @RequestBody Shift shift) {
    try {
      return ResponseEntity.ok(shiftService.createShift(staffUsername, facilityId, shift));
    } catch (UnableToCreateShiftException | StaffNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/getAllShifts/{role}", produces={"application/json"})
  public ResponseEntity getAllShifts(@PathVariable String role, @RequestParam("unit") String unit) {
    try {
      return ResponseEntity.ok(shiftService.getAllShiftsByRole(role, unit));
    } catch (UnableToCreateShiftException | StaffRoleNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @DeleteMapping(value="/deleteShift/{shiftId}", produces={"application/json"})
  public ResponseEntity deleteShift(@PathVariable Long shiftId) {
    try {
      shiftService.deleteShift(shiftId);
      return ResponseEntity.ok("Shift with shiftId " + shiftId + " has been deleted successfully.");
    } catch (UnableToCreateShiftException | ShiftNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @PutMapping(value="/updateShift/{shiftId}/{facilityId}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity updateShift(@PathVariable Long shiftId, @PathVariable Long facilityId, @RequestBody Shift shift) {
    try {
      return ResponseEntity.ok(shiftService.updateShift(shiftId, facilityId, shift));
    } catch (UnableToCreateShiftException | ShiftNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/viewMonthlyRoster/{username}", produces={"application/json"})
  public ResponseEntity viewMonthlyRoster(@PathVariable String username, @RequestParam Integer month, @RequestParam Integer year) {
    try {
      return ResponseEntity.ok(shiftService.viewMonthlyRoster(username, month, year));
    } catch (StaffNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/viewWeeklyRoster/{username}", produces={"application/json"})
  public ResponseEntity viewMonthlyRoster(@PathVariable String username, @RequestParam String date) {
    try {
      return ResponseEntity.ok(shiftService.viewWeeklyRoster(username, date));
    } catch (StaffNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/viewOverallRoster/{username}", produces={"application/json"})
  public ResponseEntity viewMonthlyRoster(@PathVariable String username) {
    try {
      return ResponseEntity.ok(shiftService.viewOverallRoster(username));
    } catch (StaffNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/getAllShiftsFromDate/{username}", produces={"application/json"})
  public ResponseEntity getAllShifts(@PathVariable String username, @RequestParam String startDate, @RequestParam String endDate) {
    try {
      return ResponseEntity.ok(shiftService.getAllShiftsForStaffFromDates(username, startDate, endDate));
    } catch (StaffRoleNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @PostMapping(value="/automaticallyCreateShifts", produces={"application/json"})
  public ResponseEntity automaticallyCreateShifts(@RequestParam("startDate") String startDate,
                                     @RequestParam("endDate") String endDate,
                                     @RequestParam("role") String role,
                                     @RequestParam("department") String department,
                                      @RequestParam("shift1") Integer shift1,
                                      @RequestParam("shift2") Integer shift2,
                                      @RequestParam("shift3") Integer shift3) {
    try {
      shiftService.automaticallyAllocateShifts(startDate, endDate, role, department, shift1, shift2, shift3);

      return ResponseEntity.ok("Shifts allocated!");
    } catch (StaffRoleNotFoundException | InterruptedException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

}
