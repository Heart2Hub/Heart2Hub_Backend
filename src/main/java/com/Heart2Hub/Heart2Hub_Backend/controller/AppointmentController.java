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
      @RequestParam("patientUsername") String patientUsername) {
    return ResponseEntity.ok(appointmentService.createNewAppointment(description,
        actualDateTime, bookedDateTime, priority, patientUsername));
  }

  @PostMapping("/assignAppointmentToStaff")
  public ResponseEntity<Appointment> assignAppointmentToStaff(
      @RequestParam("appointmentId") Long appointmentId,
      @RequestParam("staffId") Long staffId) {
    return ResponseEntity.ok(appointmentService.assignAppointmentToStaff(appointmentId, staffId));
  }

  @GetMapping("/viewAllAppointmentsByMonth")
  public ResponseEntity<List<AppointmentDTO>> viewAllAppointmentsByMonth(
      @RequestParam("month") Integer month,
      @RequestParam("year") Integer year) {

    try {
      List<Appointment> listOfAppts = appointmentService.viewAllAppointmentsByMonth(month, year);
      List<AppointmentDTO> listOfApptsDTO = listOfAppts.stream()
          .map(appointment -> {
            appointment.getPatient().getElectronicHealthRecord().getElectronicHealthRecordId();
                appointment.getPatient().getElectronicHealthRecord().getListOfPastAppointments().size();
                appointment.getPatient().getElectronicHealthRecord().getListOfNextOfKinRecords().size();
                appointment.getPatient().getElectronicHealthRecord().getListOfSubsidies().size();
                appointment.getPatient().getElectronicHealthRecord().getListOfPrescriptionRecords().size();
                appointment.getPatient().getElectronicHealthRecord().getListOfTreatmentPlanRecords().size();
                appointment.getPatient().getElectronicHealthRecord().getListOfMedicalHistoryRecords().size();
                appointment.getPatient().getElectronicHealthRecord().getListOfPastAdmissions().size();
                appointment.getPatient().getElectronicHealthRecord().getListOfProblemRecords().size();
                return appointmentMapper.convertToDto(appointment);
              }

          ).collect(
              Collectors.toList());
      System.out.println("Successfully mapped appts!!!!");
      System.out.println(listOfApptsDTO);

      System.out.println("number of appts to send: " + listOfApptsDTO.size());
      return ResponseEntity.ok(listOfApptsDTO);
    } catch (Exception e) {
      e.printStackTrace();
      // Or log the exception if you have a logger
      // logger.error("Error during mapping", e);
      throw e;
    }
  }
}
