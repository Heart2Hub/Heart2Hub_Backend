package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.mapper.AppointmentMapper;
import com.Heart2Hub.Heart2Hub_Backend.service.AppointmentService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;

  private final AppointmentMapper appointmentMapper;

  @GetMapping("/findAppointmentByAppointmentId")
  public ResponseEntity<Appointment> findAppointmentByAppointmentId(
      @RequestParam("appointmentId") Long appointmentId) {

    return ResponseEntity.ok(appointmentService.findAppointmentByAppointmentId(appointmentId));
  }

  @GetMapping("/viewAllAppointmentsByDay")
  public ResponseEntity<List<Appointment>> viewAllAppointmentsByDay(
      @RequestParam("date") String localDateTimeString) {

    return ResponseEntity.ok(appointmentService.viewAllAppointmentsByDay(localDateTimeString));
  }

  @PostMapping("/createNewAppointment")
  public ResponseEntity<Appointment> createNewAppointment(
      @RequestParam("description") String description,
      @RequestParam("actualDateTime") String actualDateTime,
      @RequestParam("bookedDateTime") String bookedDateTime,
      @RequestParam("priority") String priority,
      @RequestParam("patientUsername") String patientUsername,
      @RequestParam("departmentName") String departmentName) {
    return ResponseEntity.ok(appointmentService.createNewAppointment(description,
        actualDateTime, bookedDateTime, priority, patientUsername, departmentName));
  }

  @PostMapping("/assignAppointmentToStaff")
  public ResponseEntity<Appointment> assignAppointmentToStaff(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("staffId") Long staffId) {
    return ResponseEntity.ok(appointmentService.assignAppointmentToStaff(appointmentId, staffId));
  }

  @GetMapping("/viewAllAppointmentsByRange")
  public ResponseEntity<List<AppointmentDTO>> viewAllAppointmentsByMonth(
      @RequestParam("startDay") Integer startDay, @RequestParam("startMonth") Integer startMonth,
      @RequestParam("startYear") Integer startYear, @RequestParam("endDay") Integer endDay,
      @RequestParam("endMonth") Integer endMonth,
      @RequestParam("endYear") Integer endYear,
      @RequestParam("departmentName") String departmentName) {

    List<Appointment> listOfAppts = appointmentService.viewAllAppointmentsByRange(startDay,
        startMonth, startYear, endDay, endMonth, endYear, departmentName);
    List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
        .map(appointmentMapper::convertToDto).collect(Collectors.toList());
    return ResponseEntity.ok(listOfApptsDTO);
  }

  @PostMapping("/updateAppointmentArrival")
  public ResponseEntity<AppointmentDTO> updateAppointmentArrival(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("arrivalStatus") Boolean arrivalStatus) {
    return ResponseEntity.ok(appointmentMapper.convertToDto(
        appointmentService.updateAppointmentArrival(appointmentId, arrivalStatus)));
  }

  @PostMapping("/updateAppointmentComments")
  public ResponseEntity<AppointmentDTO> updateAppointmentComments(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("comments") String comments) {
    return ResponseEntity.ok(appointmentMapper.convertToDto(
        appointmentService.updateAppointmentComments(appointmentId, comments)));
  }

//  @GetMapping("/viewAllAppointmentsByRange")
//  public ResponseEntity<List<AppointmentDTO>> viewAllAppointmentsByMonth(
//      @RequestParam("startDay") Integer startDay, @RequestParam("startMonth") Integer startMonth,
//      @RequestParam("startYear") Integer startYear, @RequestParam("endDay") Integer endDay,
//      @RequestParam("endMonth") Integer endMonth,
//      @RequestParam("endYear") Integer endYear) {
//
//    List<Appointment> listOfAppts = appointmentService.viewAllAppointmentsByRange(startDay,
//        startMonth, startYear, endDay, endMonth, endYear);
//    List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
//        .map(appointmentMapper::convertToDto).collect(Collectors.toList());
//    return ResponseEntity.ok(listOfApptsDTO);
//  }
}
