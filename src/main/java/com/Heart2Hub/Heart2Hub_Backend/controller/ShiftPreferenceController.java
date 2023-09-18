package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftPreference;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.service.ShiftConstraintsService;
import com.Heart2Hub.Heart2Hub_Backend.service.ShiftPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shiftPreference")
@RequiredArgsConstructor
public class ShiftPreferenceController {

  private final ShiftPreferenceService shiftPreferenceService;

  @PostMapping(value="/createShiftPreference", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity createShiftPreference(@RequestBody ShiftPreference shiftPreference) {
    try {
      return ResponseEntity.ok(shiftPreferenceService.createShiftPreference(shiftPreference));
    } catch (UnableToCreateShiftConstraintsException | StaffNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/getShiftPreference/{username}", produces={"application/json"})
  public ResponseEntity createShiftPreference(@PathVariable String username) {
    try {
      return ResponseEntity.ok(shiftPreferenceService.getShiftPreference(username));
    } catch (ShiftPreferenceNotFoundException | StaffNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @DeleteMapping(value="/deleteShiftPreference/{shiftPreferenceId}", produces={"application/json"})
  public ResponseEntity createShiftPreference(@PathVariable Long shiftPreferenceId) {
    try {
      shiftPreferenceService.deleteShiftPreference(shiftPreferenceId);
      return ResponseEntity.ok("Shift preference with id " + shiftPreferenceId + " has been successfully deleted!");
    } catch (ShiftPreferenceNotFoundException | StaffNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

}
