package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateShiftException;
import com.Heart2Hub.Heart2Hub_Backend.service.ShiftService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/shift")
@RequiredArgsConstructor
public class ShiftController {

  private final ShiftService shiftService;

  private final StaffService staffService;

  @PostMapping(value="/createShift/{staffUsername}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity createShift(@PathVariable String staffUsername, @RequestBody Shift shift) {
    try {
      return ResponseEntity.ok(shiftService.createShift(staffUsername, shift));
    } catch (UnableToCreateShiftException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @GetMapping(value="/getAllShifts/{role}", produces={"application/json"})
  public ResponseEntity getAllShifts(@PathVariable String role) {
    try {
      return ResponseEntity.ok(shiftService.getAllShiftsByRole(role));
    } catch (UnableToCreateShiftException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @DeleteMapping(value="/deleteShift/{shiftId}", produces={"application/json"})
  public ResponseEntity deleteShift(@PathVariable Long shiftId) {
    try {
      shiftService.deleteShift(shiftId);
      return ResponseEntity.ok("Shift with shiftId " + shiftId + " has been deleted successfully.");
    } catch (UnableToCreateShiftException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

  @PutMapping(value="/updateShift/{shiftId}", produces={"application/json"})
  public ResponseEntity updateShift(@PathVariable Long shiftId, @RequestBody Shift shift) {
    try {
      return ResponseEntity.ok(shiftService.updateShift(shiftId, shift));
    } catch (UnableToCreateShiftException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

}
