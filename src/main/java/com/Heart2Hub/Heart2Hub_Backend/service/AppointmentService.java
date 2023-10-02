package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.AppointmentAssignmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.AppointmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffDisabledException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateAppointmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;

  private final PatientService patientService;

  private final DepartmentService departmentService;

  private final StaffService staffService;


  public AppointmentService(AppointmentRepository appointmentRepository,
      PatientService patientService, DepartmentService departmentService,
      StaffService staffService) {
    this.appointmentRepository = appointmentRepository;
    this.patientService = patientService;
    this.departmentService = departmentService;
    this.staffService = staffService;
  }

  public Appointment findAppointmentByAppointmentId(Long appointmentId) {
    return appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new AppointmentNotFoundException("Appointment Does not Exist"));
  }

  public Appointment createNewAppointment(String description,
      String actualDateTimeString, String bookedDateTimeString, String priority,
      String patientUsername, String departmentName) {
    LocalDateTime actualDateTime = LocalDateTime.parse(actualDateTimeString);
    LocalDateTime bookedDateTime = LocalDateTime.parse(bookedDateTimeString);
    //perform checks
    Patient patient = patientService.getPatientByUsername(patientUsername);
    // patient has no disabled field?

    //Get department
    Department department = departmentService.getDepartmentByName(departmentName);

    //check if patient has overlapping appointments
    List<Appointment> listOfAppointments = patient.getListOfCurrentAppointments().stream()
        .filter(appt -> actualDateTime.isEqual(appt.getActualDateTime())).toList();
    if (listOfAppointments.size() != 0) {
      throw new UnableToCreateAppointmentException(
          "Unable to create appointment, overlapping appointment exists.");
    }

    //create appt entity
    Appointment newAppointment = new Appointment(description, actualDateTime,
        bookedDateTime, PriorityEnum.valueOf(priority), patient, department);
    patient.getListOfCurrentAppointments().add(newAppointment);

    appointmentRepository.save(newAppointment);
    return appointmentRepository.save(newAppointment);
  }

  //View All Appointments by day
  public List<Appointment> viewAllAppointmentsByDay(String localDateTimeString) {
    LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString);
    return appointmentRepository.findAllByActualDateTimeBetween(
        localDateTime.toLocalDate().atStartOfDay(),
        localDateTime.plusDays(1).toLocalDate().atStartOfDay());
  }

  //View All Appointments by day
  public List<Appointment> viewAllAppointmentsByRange(Integer startDay, Integer startMonth,
      Integer startYear, Integer endDay, Integer endMonth, Integer endYear, String departmentName,
      Long selectStaffId) {

    LocalDateTime startDate = LocalDateTime.of(startYear, startMonth, startDay, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(endYear, endMonth, endDay, 23, 59, 59);

    List<Appointment> listOfAppointments = appointmentRepository.findAllByActualDateTimeBetweenAndDepartmentName(
        startDate, endDate, departmentName);
    System.out.println("====== WORKING HERE ======");

//    filtering the appointments based on staffId
    if (selectStaffId > 0L) {
      System.out.println("====== NOT ZERO ======");
      listOfAppointments = listOfAppointments.stream().filter(
          appointment -> appointment.getCurrentAssignedStaff() != null
              && Objects.equals(appointment.getCurrentAssignedStaff().getStaffId(), selectStaffId)).collect(
          Collectors.toList());
      System.out.println(listOfAppointments.size());
    }
    return listOfAppointments;
  }

  public Appointment assignAppointmentToStaff(Long appointmentId, Long staffId) {

    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    Staff staff = staffService.findById(staffId);

    //check staff not disabled
    if (staff.getDisabled()) {
      throw new StaffDisabledException("Unable to assign appointment to Disabled Staff");
    }

//    if (appointment.getCurrentAssignedStaff() != null && appointment.getCurrentAssignedStaff()
//        .getStaffId().equals(staffId)) {
//      throw new AppointmentAssignmentException("Staff is already allocated the appointment");
//    }

    //assign new staff to appointment
    appointment.setCurrentAssignedStaff(staff);
    // BIG PROBLEM HERE
//    appointment.getListOfStaff().add(staff);
    staff.getListOfAssignedAppointments().add(appointment);
    return appointment;
  }

  public Appointment updateAppointmentArrival(Long appointmentId, Boolean arrivalStatus) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    appointment.setArrived(arrivalStatus);
    return appointment;
  }

  public Appointment updateAppointmentComments(Long appointmentId, String comments) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    appointment.setComments(comments);
    return appointment;
  }

  public Appointment updateAppointmentSwimlaneStatus(Long appointmentId, SwimlaneStatusEnum swimlaneStatusEnum) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    appointment.setSwimlaneStatusEnum(swimlaneStatusEnum);
    appointment.setArrived(false);
    return appointment;
  }

}
