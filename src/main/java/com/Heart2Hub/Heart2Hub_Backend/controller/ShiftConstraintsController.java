package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.service.ShiftConstraintsService;
import com.Heart2Hub.Heart2Hub_Backend.service.ShiftService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shiftConstraints")
@RequiredArgsConstructor
public class ShiftConstraintsController {

  private final ShiftConstraintsService shiftConstraintsService;

  @PostMapping(value="/createShiftConstraints", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity createShiftConstraints(@RequestBody ShiftConstraints shiftConstraints) {
    try {
      return ResponseEntity.ok(shiftConstraintsService.createShiftConstraints(shiftConstraints));
    } catch (UnableToCreateShiftConstraintsException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/getAllShiftConstraints/{role}", produces={"application/json"})
  public ResponseEntity getAllShiftConstraints(@PathVariable String role) {
    try {
      return ResponseEntity.ok(shiftConstraintsService.getAllShiftConstraintsByRole(role));
    } catch (RoleNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @DeleteMapping(value="/deleteShiftConstraints/{shiftConstraintsId}", produces={"application/json"})
  public ResponseEntity deleteShiftConstraints(@PathVariable Long shiftConstraintsId) {
    try {
      shiftConstraintsService.deleteShiftConstraints(shiftConstraintsId);
      return ResponseEntity.ok("Shift constraints with id: " + shiftConstraintsId + " has been deleted successfully!");
    } catch (ShiftConstraintsNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @PutMapping(value="/updateShiftConstraints/{shiftConstraintsId}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity updateShiftConstraints(@PathVariable Long shiftConstraintsId, @RequestBody ShiftConstraints shiftConstraints) {
    try {
      return ResponseEntity.ok(shiftConstraintsService.updateShiftConstraints(shiftConstraintsId, shiftConstraints));
    } catch (UnableToCreateShiftConstraintsException | ShiftConstraintsNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/checkIsValidWorkday", produces={"application/json"})
  public ResponseEntity checkIsValidWorkday(@RequestParam String role, @RequestParam String date) {
    try {
      return ResponseEntity.ok(shiftConstraintsService.isValidWorkDay(role, date));
    } catch (RoleNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

}
