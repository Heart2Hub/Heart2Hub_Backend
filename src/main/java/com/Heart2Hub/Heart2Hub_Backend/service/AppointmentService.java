package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.AppointmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffDisabledException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateAppointmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToUpdateAppointmentArrival;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToUpdateAppointmentComments;
import com.Heart2Hub.Heart2Hub_Backend.repository.AppointmentRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
  private final ElectronicHealthRecordService electronicHealthRecordService;
  private final StaffService staffService;


  public AppointmentService(AppointmentRepository appointmentRepository,
      PatientService patientService, DepartmentService departmentService,
      ElectronicHealthRecordService electronicHealthRecordService, StaffService staffService) {
    this.appointmentRepository = appointmentRepository;
    this.patientService = patientService;
    this.departmentService = departmentService;
    this.electronicHealthRecordService = electronicHealthRecordService;
    this.staffService = staffService;
  }

  public Appointment findAppointmentByAppointmentId(Long appointmentId) {
    return appointmentRepository.findById(appointmentId)
        .orElseThrow(() -> new AppointmentNotFoundException("Appointment Does not Exist"));
  }

  public Appointment createNewAppointment(String description,
//      String actualDateTimeString,
      String bookedDateTimeString, String priority,
      String nric, String departmentName) {
//    LocalDateTime actualDateTime = LocalDateTime.parse(actualDateTimeString);
    LocalDateTime bookedDateTime = LocalDateTime.parse(bookedDateTimeString);

    ElectronicHealthRecord ehr = electronicHealthRecordService.findByNric(nric);

    Patient patient = ehr.getPatient();

    //Get department
    Department department = departmentService.getDepartmentByName(departmentName);

    //check if patient has overlapping appointments
    List<Appointment> listOfAppointments = patient.getListOfCurrentAppointments().stream()
        .filter(appt -> bookedDateTime.isEqual(appt.getBookedDateTime())).toList();
    if (listOfAppointments.size() != 0) {
      throw new UnableToCreateAppointmentException(
          "Unable to create appointment, overlapping appointment exists.");
    }

    //create appt entity
    Appointment newAppointment = new Appointment(description,
//        actualDateTime,
        bookedDateTime, PriorityEnum.valueOf(priority), patient, department);
    patient.getListOfCurrentAppointments().add(newAppointment);
    //newAppointment.setArrived(true);
//    appointmentRepository.save(newAppointment);
    return appointmentRepository.save(newAppointment);
  }

  public Appointment createNewAppointmentOnWeb(String description,
//                                          String actualDateTimeString,
                                          String bookedDateTimeString, String priority,
                                          String nric, String departmentName) {
//    LocalDateTime actualDateTime = LocalDateTime.parse(actualDateTimeString);
    LocalDateTime bookedDateTime = LocalDateTime.parse(bookedDateTimeString);

    ElectronicHealthRecord ehr = electronicHealthRecordService.findByNric(nric);

    Patient patient = ehr.getPatient();

    //Get department
    Department department = departmentService.getDepartmentByName(departmentName);

    //check if patient has overlapping appointments
    List<Appointment> listOfAppointments = patient.getListOfCurrentAppointments().stream()
        .filter(appt -> bookedDateTime.isEqual(appt.getBookedDateTime())).toList();
    if (listOfAppointments.size() != 0) {
      throw new UnableToCreateAppointmentException(
          "Unable to create appointment, overlapping appointment exists.");
    }

    //create appt entity
    Appointment newAppointment = new Appointment(description,
//        actualDateTime,
        bookedDateTime, PriorityEnum.valueOf(priority), patient, department);
    patient.getListOfCurrentAppointments().add(newAppointment);
    //newAppointment.setArrived(true);
//    appointmentRepository.save(newAppointment);
    return appointmentRepository.save(newAppointment);
  }

  //View All Appointments by day
  public List<Appointment> viewAllAppointmentsByDay(String localDateTimeString) {
    LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeString);
    return appointmentRepository.findAllByBookedDateTimeBetween(
        localDateTime.toLocalDate().atStartOfDay(),
        localDateTime.plusDays(1).toLocalDate().atStartOfDay());
  }

  //View All Appointments by day
  public List<Appointment> viewAllAppointmentsByRange(Integer startDay, Integer startMonth,
      Integer startYear, Integer endDay, Integer endMonth, Integer endYear, String departmentName,
      Long selectStaffId) {

    LocalDateTime startDate = LocalDateTime.of(startYear, startMonth, startDay, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(endYear, endMonth, endDay, 23, 59, 59);

    List<Appointment> listOfAppointments = appointmentRepository.findAllByBookedDateTimeBetweenAndDepartmentName(
        startDate, endDate, departmentName);
    System.out.println("====== WORKING HERE ======");

//    filtering the appointments based on staffId
    if (selectStaffId > 0L) {
      System.out.println("====== NOT ZERO ======");
      listOfAppointments = listOfAppointments.stream().filter(
              appointment -> appointment.getCurrentAssignedStaff() != null
                  && Objects.equals(appointment.getCurrentAssignedStaff().getStaffId(), selectStaffId))
          .collect(
              Collectors.toList());
      System.out.println(listOfAppointments.size());
    }
    return listOfAppointments;
  }

  public List<Appointment> viewPatientAppointments(String patientUsername) {
    return appointmentRepository.findAllByPatientUsername(patientUsername);
  }

  public List<Appointment> viewStaffAppointments(Integer startDay, Integer startMonth,
      Integer startYear, Integer endDay, Integer endMonth, Integer endYear, String username) {
    LocalDateTime startDate = LocalDateTime.of(startYear, startMonth, startDay, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(endYear, endMonth, endDay, 23, 59, 59);
    return appointmentRepository.findAllByActualDateTimeBetweenAndCurrentAssignedStaffUsername(
        startDate, endDate, username);
  }

  public Appointment createNewAppointmentWithStaff(String description,
//      String actualDateTimeString,
      String bookedDateTimeString, String priority,
      String patientUsername, String departmentName, String staffUsername) {
//    LocalDateTime actualDateTime = LocalDateTime.parse(actualDateTimeString);
    LocalDateTime bookedDateTime = LocalDateTime.parse(bookedDateTimeString);
    //perform checks
    Patient patient = patientService.getPatientByUsername(patientUsername);
    // patient has no disabled field?

    //Get department
    Department department = departmentService.getDepartmentByName(departmentName);

    //check if patient has overlapping appointments
    List<Appointment> listOfAppointments = patient.getListOfCurrentAppointments().stream()
        .filter(appt -> bookedDateTime.isEqual(appt.getBookedDateTime())).toList();
    if (listOfAppointments.size() != 0) {
      throw new UnableToCreateAppointmentException(
          "Unable to create appointment, overlapping appointment exists.");
    }

    //create appt entity
    Appointment newAppointment = new Appointment(description,
//        actualDateTime,
        bookedDateTime, PriorityEnum.valueOf(priority), patient, department);
    patient.getListOfCurrentAppointments().add(newAppointment);

    if (staffUsername != null && !staffUsername.isEmpty()) {
      Staff staff = staffService.getStaffByUsername(staffUsername);
      newAppointment.setComments(
          "To be assigned to Dr." + staff.getFirstname() + " " + staff.getLastname());
      newAppointment.setCurrentAssignedStaff(staff);
      staff.getListOfAssignedAppointments().add(newAppointment);
      newAppointment.setCurrentAssignedStaff(null);
    }

    return appointmentRepository.save(newAppointment);
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
//    if (!appointment.getListOfStaff().contains(staff)) {
//      appointment.getListOfStaff().add(staff);
//    }
    staff.getListOfAssignedAppointments().add(appointment);
    return appointment;
  }

  public Appointment updateAppointmentArrival(Long appointmentId, Boolean arrivalStatus,
      Long staffId) {

    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    //check if appointment is assigned to you first, or else you should not be able to check arrived
    if (appointment.getCurrentAssignedStaff() == null || !Objects.equals(
        appointment.getCurrentAssignedStaff().getStaffId(), staffId)) {
      throw new UnableToUpdateAppointmentArrival(
          "Unable to edit a appointment that is not assigned to you");
    }

    appointment.setArrived(arrivalStatus);
    appointment.setActualDateTime(LocalDateTime.now());
    return appointment;
  }

  public Appointment updateAppointmentComments(Long appointmentId, String comments, Long staffId) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);

    //check if appointment is assigned to you first, or else you should not be able to update comments
    if (appointment.getCurrentAssignedStaff() == null || !Objects.equals(
        appointment.getCurrentAssignedStaff().getStaffId(), staffId)) {
      throw new UnableToUpdateAppointmentComments(
          "Unable to edit a appointment that is not assigned to you");
    }

    Staff staff = staffService.getStaffById(staffId);
    String newComment =
        comments + " (" + staff.getStaffRoleEnum() + " " + staff.getFirstname() + " "
            + staff.getLastname() + ")";
    if (!appointment.getComments().equals("")) {
      newComment = "\n" + newComment;
    }
    newComment = appointment.getComments() + newComment;
    appointment.setComments(newComment);
    return appointment;
  }

  public Appointment updateAppointmentSwimlaneStatus(Long appointmentId,
      SwimlaneStatusEnum swimlaneStatusEnum) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    appointment.setSwimlaneStatusEnum(swimlaneStatusEnum);
    appointment.setArrived(false);
    return appointment;
  }

  public Appointment updateAppointment(Long appointmentId, String patientUsername,
      String newTimeString,
      String newDescription, String staffUsername) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    LocalDateTime newTime = LocalDateTime.parse(newTimeString);
    Patient patient = patientService.getPatientByUsername(patientUsername);
    if (!newTime.isEqual(appointment.getActualDateTime())) {
      List<Appointment> listOfAppointments = patient.getListOfCurrentAppointments().stream()
          .filter(appt -> newTime.isEqual(appt.getActualDateTime())).toList();
      if (listOfAppointments.size() != 0) {
        throw new UnableToCreateAppointmentException(
            "Unable to update appointment, overlapping appointment exists.");
      }
    }

    appointment.setActualDateTime(newTime);
    appointment.setDescription(newDescription);
    if (staffUsername != null && !staffUsername.isEmpty()) {
      Staff staff = staffService.getStaffByUsername(staffUsername);
      appointment.setCurrentAssignedStaff(staff);
    }

    return appointment;
  }

  public String cancelAppointment(Long appointmentId) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    if (appointment.getArrived()
        || appointment.getSwimlaneStatusEnum() != SwimlaneStatusEnum.REGISTRATION) {
      throw new UnableToCreateAppointmentException(
          "Unable to delete appointment, patient has already arrived at the clinic.");
    }
    appointment.setCurrentAssignedStaff(null);
    appointmentRepository.delete(appointment);
    return "Appointment " + appointmentId + " has been deleted successfully!";
  }

  public Appointment addImageAttachmentToAppointment(Long appointmentId, String imageLink,
      String createdDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
    LocalDateTime createdDateTime = LocalDateTime.parse(createdDate, formatter);
    ImageDocument imageDocument = new ImageDocument(imageLink, createdDateTime);

    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    appointment.getListOfImageDocuments().add(imageDocument);

    return appointment;
  }

  public List<ImageDocument> viewAppointmentAttachments(Long appointmentId) {
    Appointment appointment = findAppointmentByAppointmentId(appointmentId);
    return appointment.getListOfImageDocuments();
  }

}
