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
  private final ElectronicHealthRecordMapper electronicHealthRecordMapper;

  public AppointmentMapper(ElectronicHealthRecordMapper electronicHealthRecordMapper) {
    this.modelMapper = new ModelMapper();
    this.electronicHealthRecordMapper = electronicHealthRecordMapper;
    System.out.println("electronicHealthRecordMapper: ");
    System.out.println(electronicHealthRecordMapper);

    modelMapper.createTypeMap(Appointment.class, AppointmentDTO.class)
        .addMapping(src -> src.getCurrentAssignedStaff().getStaffId(),
            AppointmentDTO::setCurrentAssignedStaffId)
        .addMapping(src -> src.getPatient().getPatientId(), AppointmentDTO::setPatientId)
        //TODO its this one lil shit thats not working, unsure if its lazy fetching thats causing the mapping to not work
        // across relationships during mapping or @JSONIgnore thats blocking the mapper from proceding
        .addMapping(src -> {
//          System.out.println(src.getPatient().getElectronicHealthRecord());
          return electronicHealthRecordMapper.convertToDto(src.getPatient().getElectronicHealthRecord());
        }, AppointmentDTO::setElectronicHealthRecord)
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
