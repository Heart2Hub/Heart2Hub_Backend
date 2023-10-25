package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AppointmentMapper {

  public AppointmentDTO toDTO(Appointment appointment) {
    if (appointment == null) {
      return null;
    }

    AppointmentDTO dto = new AppointmentDTO();

    dto.setAppointmentId(appointment.getAppointmentId());
    dto.setDescription(appointment.getDescription());
    dto.setComments(appointment.getComments());
    dto.setActualDateTime(appointment.getActualDateTime());
    dto.setBookedDateTime(appointment.getBookedDateTime());
    dto.setEstimatedDuration(appointment.getEstimatedDuration());
    dto.setArrived(appointment.getArrived());
    dto.setPriorityEnum(appointment.getPriorityEnum());
    dto.setSwimlaneStatusEnum(appointment.getSwimlaneStatusEnum());
    //current assigned staff
    if (appointment.getCurrentAssignedStaff() != null) {
      dto.setCurrentAssignedStaffId(appointment.getCurrentAssignedStaff().getStaffId());
    }
    if (appointment.getListOfStaff() != null) {
      List<Long> staffIds = new ArrayList<>();
      for (Staff staff : appointment.getListOfStaff()) {
        staffIds.add(staff.getStaffId());
      }
      dto.setListOfStaffsId(staffIds);
    }
    //Patient
    System.out.println(appointment.getPatient().getPatientId());
    if (appointment.getPatient()!=null) {
      dto.setPatientId(appointment.getPatient().getPatientId());
      dto.setUsername(appointment.getPatient().getUsername());

      if (appointment.getPatient().getProfilePicture() != null) {
        dto.setPatientProfilePicture(appointment.getPatient().getProfilePicture().getImageLink());
      }

      if (appointment.getPatient().getElectronicHealthRecord() != null) {
        dto.setElectronicHealthRecordId(appointment.getPatient().getElectronicHealthRecord().getElectronicHealthRecordId());
        dto.setFirstName(appointment.getPatient().getElectronicHealthRecord().getFirstName());
        dto.setLastName(appointment.getPatient().getElectronicHealthRecord().getLastName());
        dto.setNric(appointment.getPatient().getElectronicHealthRecord().getNric());
        dto.setPlaceOfBirth(appointment.getPatient().getElectronicHealthRecord().getPlaceOfBirth());
        dto.setSex(appointment.getPatient().getElectronicHealthRecord().getSex());
        dto.setContactNumber(appointment.getPatient().getElectronicHealthRecord().getContactNumber());
        dto.setNationality(appointment.getPatient().getElectronicHealthRecord().getNationality());
        dto.setDateOfBirth(appointment.getPatient().getElectronicHealthRecord().getDateOfBirth());
      }

      // Admission fields
      Admission admissionToSchedule = appointment.getPatient().getAdmission();
      if (admissionToSchedule != null) {
        dto.setAdmissionId(admissionToSchedule.getAdmissionId());
        dto.setAdmissionDuration(admissionToSchedule.getDuration());
        dto.setAdmissionReason(admissionToSchedule.getReason());

//        if (admissionToSchedule.getAdmissionDateTime() == null) {
//          dto.setAdmissionScheduled(true);
//        }
      }
    }

    //departmentName
    if (appointment.getDepartment() != null) {
      dto.setDepartmentName(appointment.getDepartment().getName());
    }

    // Convert listOfImageDocuments to listOfImageDocumentsImageLinks
    if (appointment.getListOfImageDocuments() != null) {
      List<String> imageLinks = new ArrayList<>();
      for (ImageDocument imageDoc : appointment.getListOfImageDocuments()) {
        imageLinks.add(imageDoc.getImageLink());
      }
      dto.setListOfImageDocumentsImageLinks(imageLinks);
    }



    // Add other complex mappings if needed...

    return dto;
  }

  //DO NOT USE
  public Appointment toEntity(AppointmentDTO dto) {
    if (dto == null) {
      return null;
    }

    Appointment appointment = new Appointment();

    appointment.setAppointmentId(dto.getAppointmentId());
    appointment.setDescription(dto.getDescription());
    appointment.setComments(dto.getComments());
    // ... set other fields ...

    // Convert back from DTO to entity requires more information and might need services to fetch related entities like 'CurrentAssignedStaff' by id, etc.

    return appointment;
  }

  //DO NOT USE
  public List<AppointmentDTO> toDTOList(List<Appointment> appointments) {
    List<AppointmentDTO> dtos = new ArrayList<>();
    for (Appointment appointment : appointments) {
      dtos.add(toDTO(appointment));
    }
    return dtos;
  }

  //DO NOT USE
  public List<Appointment> toEntityList(List<AppointmentDTO> dtos) {
    List<Appointment> appointments = new ArrayList<>();
    for (AppointmentDTO dto : dtos) {
      appointments.add(toEntity(dto));
    }
    return appointments;
  }
}