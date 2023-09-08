package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.exception.ShiftNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateShiftConstraintsException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateShiftException;
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
    } catch (UnableToCreateShiftConstraintsException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

}
