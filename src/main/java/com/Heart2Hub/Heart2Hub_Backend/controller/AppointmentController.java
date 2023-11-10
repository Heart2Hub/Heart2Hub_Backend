package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.DispensaryStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.mapper.AppointmentMapper;
import com.Heart2Hub.Heart2Hub_Backend.service.AppointmentService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

  private final AppointmentService appointmentService;

  private final AppointmentMapper appointmentMapper;
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  @GetMapping("/findAppointmentByAppointmentId")
  public ResponseEntity<Appointment> findAppointmentByAppointmentId(
      @RequestParam("appointmentId") Long appointmentId) {

    return ResponseEntity.ok(appointmentService.findAppointmentByAppointmentId(appointmentId));
  }

  @GetMapping("/viewAllAppointmentsByDay")
//  public ResponseEntity<List<Appointment>> viewAllAppointmentsByDay(
   public ResponseEntity<List<AppointmentDTO>> viewAllAppointmentsByDay(
      @RequestParam("date") String localDateTimeString) {
    List<Appointment> listOfAppts = appointmentService.viewAllAppointmentsByDay(localDateTimeString);
    List<AppointmentDTO> listOfApptDTOs = listOfAppts.stream().map(appointment -> appointmentMapper.toDTO(appointment)).collect(
        Collectors.toList());
    return ResponseEntity.ok(listOfApptDTOs);
//    return ResponseEntity.ok(appointmentService.viewAllAppointmentsByDay(localDateTimeString));
  }

  //Uses NRIC to search, Creates an Appointment with arrival = false
  @PostMapping("/createNewAppointment")
  public ResponseEntity<Appointment> createNewAppointment(
      @RequestParam("description") String description,
//      @RequestParam("actualDateTime") String actualDateTime,
      @RequestParam("bookedDateTime") String bookedDateTime,
      @RequestParam("priority") String priority,
      @RequestParam("patientUsername") String patientUsername,
      @RequestParam("departmentName") String departmentName) {
    return ResponseEntity.ok(appointmentService.createNewAppointment(description,
//        actualDateTime,
        bookedDateTime, priority, patientUsername, departmentName));
  }

  //Uses NRIC to search, Creates an Appointment with arrival = true
  @PostMapping("/createNewAppointmentOnWeb")
  public ResponseEntity<Appointment> createNewAppointmentOnWeb(
          @RequestParam("description") String description,
//          @RequestParam("actualDateTime") String actualDateTime,
          @RequestParam("bookedDateTime") String bookedDateTime,
          @RequestParam("priority") String priority,
          @RequestParam("patientUsername") String patientUsername,
          @RequestParam("departmentName") String departmentName) {
    return ResponseEntity.ok(appointmentService.createNewAppointmentOnWeb(description,
//            actualDateTime,
        bookedDateTime, priority, patientUsername, departmentName));
  }

  @PostMapping("/assignAppointmentToStaff")
  public ResponseEntity<Appointment> assignAppointmentToStaff(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("toStaffId") Long toStaffId,
      @RequestParam("fromStaffId") Long fromStaffId) {
    Appointment appointment = appointmentService.assignAppointmentToStaff(appointmentId, toStaffId, fromStaffId);
    sendUpdateToClients("assign");
    return ResponseEntity.ok(appointment);
  }

  @GetMapping("/viewAllAppointmentsByRange")
  public ResponseEntity<List<AppointmentDTO>> viewAllAppointmentsByMonth(
//      public ResponseEntity<List<Appointment>> viewAllAppointmentsByMonth(
      @RequestParam("startDay") Integer startDay, @RequestParam("startMonth") Integer startMonth,
      @RequestParam("startYear") Integer startYear, @RequestParam("endDay") Integer endDay,
      @RequestParam("endMonth") Integer endMonth,
      @RequestParam("endYear") Integer endYear,
      @RequestParam("departmentName") String departmentName,
      @RequestParam("selectStaffId") Long selectStaffId) {

    List<Appointment> listOfAppts = appointmentService.viewAllAppointmentsByRange(startDay,
        startMonth, startYear, endDay, endMonth, endYear, departmentName, selectStaffId);

    List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
        .map(appointmentMapper::toDTO).collect(Collectors.toList());

    return ResponseEntity.ok(listOfApptsDTO);
  }

  @GetMapping("/viewPatientAppointments")
  public ResponseEntity<List<AppointmentDTO>> viewAllAppointmentsByMonth(
      @RequestParam("patientUsername") String patientUsername) {

    List<Appointment> listOfAppts = appointmentService.viewPatientAppointments(patientUsername);
    List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
//        .map(appointmentMapper::convertToDto).collect(Collectors.toList());
        .map(appointmentMapper::toDTO).collect(Collectors.toList());
    return ResponseEntity.ok(listOfApptsDTO);
  }

  @PostMapping("/updateAppointmentArrival")
  public ResponseEntity<AppointmentDTO> updateAppointmentArrival(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("arrivalStatus") Boolean arrivalStatus,
      @RequestParam("staffId") Long staffId) {
    AppointmentDTO appointmentDTO = appointmentMapper.toDTO(
            appointmentService.updateAppointmentArrival(appointmentId, arrivalStatus, staffId));
    sendUpdateToClients("arrive");
    return ResponseEntity.ok(appointmentDTO);
//    return ResponseEntity.ok(appointmentMapper.convertToDto(
//        appointmentService.updateAppointmentArrival(appointmentId, arrivalStatus, staffId)));
  }

  @PostMapping("/updateAppointmentComments")
  public ResponseEntity<AppointmentDTO> updateAppointmentComments(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("comments") String comments,
      @RequestParam("staffId") Long staffId) {

    return ResponseEntity.ok(appointmentMapper.toDTO(
        appointmentService.updateAppointmentComments(appointmentId, comments, staffId)));
//    return ResponseEntity.ok(appointmentMapper.convertToDto(
//        appointmentService.updateAppointmentComments(appointmentId, comments, staffId)));
  }

  @PostMapping("/updateAppointmentSwimlaneStatus")
  public ResponseEntity<AppointmentDTO> updateAppointmentSwimlaneStatus(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("swimlaneStatus") String swimlaneStatus) {
//    return ResponseEntity.ok(appointmentMapper.convertToDto(
//        appointmentService.updateAppointmentSwimlaneStatus(appointmentId,
//            SwimlaneStatusEnum.valueOf(swimlaneStatus.toUpperCase()))));
    AppointmentDTO appointmentDTO = appointmentMapper.toDTO(
            appointmentService.updateAppointmentSwimlaneStatus(appointmentId,
                    SwimlaneStatusEnum.valueOf(swimlaneStatus.toUpperCase())));
    sendUpdateToClients("swimlane");
    return ResponseEntity.ok(appointmentDTO);
  }

  @PostMapping("/createNewAppointmentWithStaff")
  public ResponseEntity<Appointment> createNewAppointmentWithStaff(
      @RequestParam("description") String description,
//      @RequestParam("actualDateTime") String actualDateTime,
      @RequestParam("bookedDateTime") String bookedDateTime,
      @RequestParam("priority") String priority,
      @RequestParam("patientUsername") String patientUsername,
      @RequestParam("departmentName") String departmentName,
      @RequestParam("staffUsername") String staffUsername) {
    return ResponseEntity.ok(appointmentService.createNewAppointmentWithStaff(description,
//        actualDateTime,
        bookedDateTime, priority, patientUsername, departmentName, staffUsername));
  }

  @PutMapping("/updateAppointment")
  public ResponseEntity<Appointment> updateAppointment(
      @RequestParam("appointmentId") Long id,
      @RequestParam("description") String description,
      @RequestParam("bookedDateTime") String bookedDateTime,
      @RequestParam("patientUsername") String patientUsername,
      @RequestParam("staffUsername") String staffUsername) {
    return ResponseEntity.ok(appointmentService.updateAppointment(id, patientUsername,
            bookedDateTime, description, staffUsername));
  }

  @DeleteMapping("/cancelAppointment")
  public ResponseEntity<String> cancelAppointment(
      @RequestParam("appointmentId") Long id) {
    return ResponseEntity.ok(appointmentService.cancelAppointment(id));
  }

  @PostMapping("/addImageAttachmentToAppointment")
  public ResponseEntity<AppointmentDTO> addImageAttachmentToAppointment(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("imageLink") String imageLink,
      @RequestParam("createdDate") String createdDate,
      @RequestParam("staffId") Long staffId) {

//    return ResponseEntity.ok(appointmentMapper.convertToDto(
//        appointmentService.addImageAttachmentToAppointment(appointmentId, imageLink, createdDate)));
    return ResponseEntity.ok(appointmentMapper.toDTO(
        appointmentService.addImageAttachmentToAppointment(appointmentId, imageLink, createdDate, staffId)));
  }

  @GetMapping("/viewAppointmentAttachments")
  public ResponseEntity<List<ImageDocument>> viewAppointmentAttachments(
      @RequestParam("appointmentId") Long appointmentId) {
    return ResponseEntity.ok(appointmentService.viewAppointmentAttachments(appointmentId));

  }

  @PostMapping("/createReferral")
  public ResponseEntity<Appointment> createReferral(
          @RequestParam("description") String description,
          @RequestParam("bookedDate") String bookedDate,
          @RequestParam("patientUsername") String patientUsername,
          @RequestParam("departmentName") String departmentName,
          @RequestParam("staffUsername") String staffUsername
          ) {
    return ResponseEntity.ok(
            appointmentService.createReferral(description, bookedDate, patientUsername, departmentName, staffUsername)
    );

  }

  @GetMapping("/viewPharmacyTickets")
  public ResponseEntity<List<AppointmentDTO>> viewPharmacyTickets() {
    List<Appointment> listOfAppts = appointmentService.getAllPharmacyTickets();
    List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
            .map(appointmentMapper::toDTO).collect(Collectors.toList());
    return ResponseEntity.ok(listOfApptsDTO);
  }

  @PostMapping("/updateAppointmentDispensaryStatus")
  public ResponseEntity<AppointmentDTO> updateAppointmentDispensaryStatus(
          @RequestParam("appointmentId") Long appointmentId,
          @RequestParam("dispensaryStatus") String dispensaryStatus) {
    AppointmentDTO appointmentDTO = appointmentMapper.toDTO(
            appointmentService.updateAppointmentDispensaryStatus(appointmentId,
                    DispensaryStatusEnum.valueOf(dispensaryStatus.toUpperCase())));
    sendUpdateToClients("dispensary");
    return ResponseEntity.ok(appointmentDTO);
  }

  @GetMapping("/findAppointmentTimeDiff/{apppointmentId}")
  public ResponseEntity<Integer> findAppointmentTimeDiff(
          @PathVariable("apppointmentId") long apppointmentId) {
    return ResponseEntity.ok(appointmentService.findAppointmentTimeDiff(apppointmentId));
  }

  @PostMapping("/createNewPharmacyTicket")
  public ResponseEntity<Appointment> createNewPharmacyTicket(
          @RequestParam("description") String description,
          @RequestParam("bookedDateTime") String bookedDateTime,
          @RequestParam("priority") String priority,
          @RequestParam("patientUsername") String patientUsername,
          @RequestParam("departmentName") String departmentName) {
    return ResponseEntity.ok(appointmentService.createNewPharmacyTicket(description,
            bookedDateTime, priority, patientUsername, departmentName));
  }

  @GetMapping("/sse")
  public SseEmitter eventEmitter() {
    SseEmitter emitter = new SseEmitter();
    String key = UUID.randomUUID().toString();
    emitters.put(key, emitter);
    emitter.onCompletion(() -> emitters.remove(key));
    emitter.onTimeout(() -> emitters.remove(key));
    return emitter;
  }

  public void sendUpdateToClients(String message) {
    emitters.values().forEach(emitter -> {
      try {
        emitter.send(SseEmitter.event().data(message));
      } catch (IOException e) {
        emitter.complete();
      }
    });
  }

}
