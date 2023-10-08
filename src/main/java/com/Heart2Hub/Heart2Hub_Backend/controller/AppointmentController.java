package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.mapper.AppointmentMapper;
import com.Heart2Hub.Heart2Hub_Backend.service.AppointmentService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    return ResponseEntity.ok(appointmentService.createNewWalkInAppointment(description,
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
      @RequestParam("departmentName") String departmentName,
      @RequestParam("selectStaffId") Long selectStaffId) {

    List<Appointment> listOfAppts = appointmentService.viewAllAppointmentsByRange(startDay,
        startMonth, startYear, endDay, endMonth, endYear, departmentName, selectStaffId);
    List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
        .map(appointmentMapper::convertToDto).collect(Collectors.toList());
    return ResponseEntity.ok(listOfApptsDTO);
  }

  @GetMapping("/viewPatientAppointments")
  public ResponseEntity<List<AppointmentDTO>> viewAllAppointmentsByMonth(
      @RequestParam("patientUsername") String patientUsername) {

    List<Appointment> listOfAppts = appointmentService.viewPatientAppointments(patientUsername);
    List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
        .map(appointmentMapper::convertToDto).collect(Collectors.toList());
    return ResponseEntity.ok(listOfApptsDTO);
  }

  @PostMapping("/updateAppointmentArrival")
  public ResponseEntity<AppointmentDTO> updateAppointmentArrival(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("arrivalStatus") Boolean arrivalStatus,
      @RequestParam("staffId") Long staffId) {
    return ResponseEntity.ok(appointmentMapper.convertToDto(
        appointmentService.updateAppointmentArrival(appointmentId, arrivalStatus, staffId)));
  }

  @PostMapping("/updateAppointmentComments")
  public ResponseEntity<AppointmentDTO> updateAppointmentComments(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("comments") String comments,
      @RequestParam("staffId") Long staffId) {

    return ResponseEntity.ok(appointmentMapper.convertToDto(
        appointmentService.updateAppointmentComments(appointmentId, comments,staffId)));
  }

  @PostMapping("/updateAppointmentSwimlaneStatus")
  public ResponseEntity<AppointmentDTO> updateAppointmentSwimlaneStatus(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("swimlaneStatus") String swimlaneStatus) {
    return ResponseEntity.ok(appointmentMapper.convertToDto(
        appointmentService.updateAppointmentSwimlaneStatus(appointmentId,
            SwimlaneStatusEnum.valueOf(swimlaneStatus.toUpperCase()))));
  }

  @PostMapping("/createNewAppointmentWithStaff")
  public ResponseEntity<Appointment> createNewAppointmentWithStaff(
      @RequestParam("description") String description,
      @RequestParam("actualDateTime") String actualDateTime,
      @RequestParam("bookedDateTime") String bookedDateTime,
      @RequestParam("priority") String priority,
      @RequestParam("patientUsername") String patientUsername,
      @RequestParam("departmentName") String departmentName,
      @RequestParam("staffUsername") String staffUsername) {
    return ResponseEntity.ok(appointmentService.createNewAppointmentWithStaff(description,
        actualDateTime, bookedDateTime, priority, patientUsername, departmentName, staffUsername));
  }

  @PutMapping("/updateAppointment")
  public ResponseEntity<Appointment> updateAppointment(
      @RequestParam("appointmentId") Long id,
      @RequestParam("description") String description,
      @RequestParam("actualDateTime") String actualDateTime,
      @RequestParam("patientUsername") String patientUsername,
      @RequestParam("staffUsername") String staffUsername) {
    return ResponseEntity.ok(appointmentService.updateAppointment(id, patientUsername,
        actualDateTime, description, staffUsername));
  }

  @DeleteMapping("/cancelAppointment")
  public ResponseEntity<String> cancelAppointment(
      @RequestParam("appointmentId") Long id) {
    return ResponseEntity.ok(appointmentService.cancelAppointment(id));
  }
}
