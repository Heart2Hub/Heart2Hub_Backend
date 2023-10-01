package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("facilityBooking")
@RequiredArgsConstructor
public class FacilityBookingController {
    private final FacilityBookingService facilityBookingService;

    @GetMapping("/getAllFacilityBookings")
    public List<FacilityBooking> getAllFacilityBookings() {
        return facilityBookingService.getAllFacilityBookings();
    }

    @GetMapping("/getAllBookingsOfAFacility/{id}")
    public List<FacilityBooking> getAllBookingsOfAFacility(@PathVariable(value = "id") Long id) {
        return facilityBookingService.getAllBookingsOfAFacility(id);
    }

    @GetMapping("/getFacilityBookingById/{id}")
    public ResponseEntity<FacilityBooking> getFacilityBookingById(@PathVariable(value = "id") Long id) {
        FacilityBooking facilityBooking = facilityBookingService.getFacilityBookingById(id);
        return ResponseEntity.ok().body(facilityBooking);
    }

    @GetMapping("/getAllBookingsOfAStaff/{username}")
    public ResponseEntity <List<FacilityBooking>> getAllBookingsOfAStaff(@PathVariable(value = "username") String username) {
        List<FacilityBooking> facilityBooking = facilityBookingService.getAllBookingsOfAStaff(username);
        return ResponseEntity.ok(facilityBooking);
    }

    @PostMapping("/createFacilityBooking")
    public ResponseEntity<FacilityBooking> createFacilityBooking(@RequestBody Map<String, Object> requestBody) throws ParseException {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss", Locale.ENGLISH);

        LocalDateTime start = LocalDateTime.parse(requestBody.get("startDateTime").toString(), inputFormatter);
        LocalDateTime end = LocalDateTime.parse(requestBody.get("endDateTime").toString(), inputFormatter);


        // targetTimeZoneId = "Asia/Singapore";
        ZoneId sourceZoneId = ZoneId.systemDefault(); // Assuming the source time zone is the system default.

        LocalDateTime startDateTime = LocalDateTime.ofInstant(
                start.atZone(ZoneId.systemDefault()).toInstant(),
                sourceZoneId
        );

        LocalDateTime endDateTime = LocalDateTime.ofInstant(
                end.atZone(ZoneId.systemDefault()).toInstant(),
                sourceZoneId
        );

        String comments = requestBody.get("comments").toString();
        String staffUsername = requestBody.get("staffUsername").toString();
        Long facilityId = Long.parseLong(requestBody.get("facilityId").toString());

        FacilityBooking fb = new FacilityBooking(startDateTime,endDateTime,comments);
        fb.setStaffUsername(staffUsername);

        FacilityBooking createdBooking = facilityBookingService.createFacilityBooking(fb, facilityId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
    }

    @PutMapping("/updateFacilityBooking")
    public ResponseEntity<FacilityBooking> updateFacilityBooking(
            @RequestBody  Map<String, Object> requestBody) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss", Locale.ENGLISH);
        LocalDateTime start = LocalDateTime.parse(requestBody.get("start").toString(), inputFormatter);
        LocalDateTime end = LocalDateTime.parse(requestBody.get("end").toString(), inputFormatter);
        ZoneId sourceZoneId = ZoneId.systemDefault();
        LocalDateTime startDateTime = LocalDateTime.ofInstant(
                start.atZone(ZoneId.systemDefault()).toInstant(),
                sourceZoneId
        );
        LocalDateTime endDateTime = LocalDateTime.ofInstant(
                end.atZone(ZoneId.systemDefault()).toInstant(),
                sourceZoneId
        );
        String comments = requestBody.get("comments").toString();
        Long bookingId = Long.parseLong(requestBody.get("facilityBookingId").toString());


        FacilityBooking updatedFacilityBooking = facilityBookingService.updateFacilityBooking(startDateTime, endDateTime, comments, bookingId);
        return ResponseEntity.ok().body(updatedFacilityBooking);
    }

    @DeleteMapping("/deleteFacilityBooking/{id}")
    public ResponseEntity<?> deleteFacilityBooking(@PathVariable(value = "id") Long id) {
        facilityBookingService.deleteFacilityBooking(id);
        return ResponseEntity.ok().build();
    }

}
