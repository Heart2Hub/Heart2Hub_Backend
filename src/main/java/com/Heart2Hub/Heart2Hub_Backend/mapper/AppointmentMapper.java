package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

  private final ModelMapper modelMapper;

  public AppointmentMapper(ElectronicHealthRecordMapper electronicHealthRecordMapper) {
    this.modelMapper = new ModelMapper();

    modelMapper.createTypeMap(Appointment.class, AppointmentDTO.class)
        .addMapping(src -> src.getCurrentAssignedStaff().getStaffId(),
            AppointmentDTO::setCurrentAssignedStaffId)
        .addMapping(src -> src.getPatient().getPatientId(), AppointmentDTO::setPatientId)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getElectronicHealthRecordId(), AppointmentDTO::setElectronicHealthRecordId)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getFirstName(), AppointmentDTO::setFirstName)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getLastName(), AppointmentDTO::setLastName)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getNric(), AppointmentDTO::setNric)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getPlaceOfBirth(), AppointmentDTO::setPlaceOfBirth)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getSex(), AppointmentDTO::setSex)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getContactNumber(), AppointmentDTO::setContactNumber)
        .addMapping(src -> src.getPatient().getElectronicHealthRecord().getNationality(), AppointmentDTO::setNationality)
        .addMapping(
        src -> (src.getListOfStaff() != null) ? src.getListOfStaff().stream().map(Staff::getStaffId)
            .collect(Collectors.toList()) : null,
        AppointmentDTO::setListOfStaffsId)
        .addMapping(
            src -> (src.getListOfImageDocuments() != null) ? src.getListOfImageDocuments().stream()
                .map(ImageDocument::getImageDocumentId).collect(Collectors.toList()) : null,
            AppointmentDTO::setListOfImageDocumentsId);
  }

  public AppointmentDTO convertToDto(Appointment appointment) {
    return modelMapper.map(appointment, AppointmentDTO.class);
  }

  public Appointment convertToEntity(AppointmentDTO appointmentDTO) {
    return modelMapper.map(appointmentDTO, Appointment.class);
  }
}
