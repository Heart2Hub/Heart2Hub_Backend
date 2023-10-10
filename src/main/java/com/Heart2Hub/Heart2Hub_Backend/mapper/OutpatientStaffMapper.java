package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.OutpatientStaffDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import java.util.Objects;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OutpatientStaffMapper {

  private final ModelMapper modelMapper;

  public OutpatientStaffMapper() {
    this.modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    modelMapper.createTypeMap(Shift.class, OutpatientStaffDTO.class)
        .addMapping(src -> src.getShiftId(),
            OutpatientStaffDTO::setShiftId)
        .addMapping(src -> src.getStartTime(),
            OutpatientStaffDTO::setStartTime)
        .addMapping(src -> src.getEndTime(),
            OutpatientStaffDTO::setEndTime)
        .addMapping(src -> src.getComments(),
            OutpatientStaffDTO::setComments)
        .addMapping(src -> src.getStaff().getStaffId(),
            OutpatientStaffDTO::setStaffId)
        .addMapping(src -> src.getStaff().getFirstname(),
            OutpatientStaffDTO::setFirstname)
        .addMapping(src -> src.getStaff().getLastname(),
            OutpatientStaffDTO::setLastname)
        .addMapping(src -> src.getStaff().getMobileNumber(),
            OutpatientStaffDTO::setMobileNumber)
        .addMapping(src -> src.getStaff().getIsHead(),
            OutpatientStaffDTO::setIsHead)
        .addMapping(src -> src.getStaff().getStaffRoleEnum(),
            OutpatientStaffDTO::setStaffRoleEnum)
        .addMapping(src -> src.getStaff().getProfilePicture().getImageDocumentId(),
            OutpatientStaffDTO::setImageDocumentId)
        .addMapping(src -> src.getStaff().getProfilePicture().getImageLink(),
            OutpatientStaffDTO::setImageLink)
        .addMapping(src -> src.getFacilityBooking().getFacilityBookingId(),
            OutpatientStaffDTO::setFacilityBookingId)
        .addMapping(src -> src.getFacilityBooking().getStartDateTime(),
            OutpatientStaffDTO::setFacilityBookingStartDateTime)
        .addMapping(src -> src.getFacilityBooking().getEndDateTime(),
            OutpatientStaffDTO::setFacilityBookingEndDateTime)
        .addMapping(src -> src.getFacilityBooking().getComments(),
            OutpatientStaffDTO::setFacilityBookingComments)
        .addMapping(src -> src.getFacilityBooking().getFacility().getFacilityId(),
            OutpatientStaffDTO::setFacilityId)
        .addMapping(src -> src.getFacilityBooking().getFacility().getName(),
            OutpatientStaffDTO::setName)
        .addMapping(src -> src.getFacilityBooking().getFacility().getLocation(),
            OutpatientStaffDTO::setLocation)
        .addMapping(src -> src.getFacilityBooking().getFacility().getDescription(),
            OutpatientStaffDTO::setDescription)
        .addMapping(src -> src.getFacilityBooking().getFacility().getCapacity(),
            OutpatientStaffDTO::setCapacity)
        .addMapping(src -> src.getFacilityBooking().getFacility().getFacilityStatusEnum(),
            OutpatientStaffDTO::setFacilityStatusEnum)
        .addMapping(src -> src.getFacilityBooking().getFacility().getFacilityTypeEnum(),
            OutpatientStaffDTO::setFacilityTypeEnum);
  }

  public OutpatientStaffDTO convertToDto(Shift shift) {
    OutpatientStaffDTO dto = modelMapper.map(shift, OutpatientStaffDTO.class);

    //getting currentAssignedAppointment for its SwimlaneStatusEnum
    for (Appointment appt : shift.getStaff().getListOfAssignedAppointments()) {
      if (Objects.equals(appt.getCurrentAssignedStaff().getStaffId(),
          shift.getStaff().getStaffId())) {
        dto.setCurrentAssignedAppointmentId(appt.getAppointmentId());
        dto.setCurrentAssignedAppointmentSwimlaneStatusEnum(appt.getSwimlaneStatusEnum());
        break;
      }
    }
    return dto;
  }

  public Shift convertToEntity(OutpatientStaffDTO outpatientStaffDTO) {
    return modelMapper.map(outpatientStaffDTO, Shift.class);
  }
}
