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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/shift")
@RequiredArgsConstructor
public class ShiftController {

  private final ShiftService shiftService;

  @PostMapping(value="/createShift/{staffUsername}", consumes={"application/json"}, produces={"application/json"})
  public ResponseEntity createShift(@PathVariable String staffUsername, @RequestBody Shift shift) {
    System.out.println(shift);
    try {
      return ResponseEntity.ok(shiftService.createShift(staffUsername, shift));
    } catch (UnableToCreateShiftException ex) {
      System.out.println(ex.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
  }

}
