package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.OutpatientStaffDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.StaffChatDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Admission;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class StaffChatMapper {

  public StaffChatDTO toDTO(Staff staff) {
    if (staff == null) {
      return null;
    }

    StaffChatDTO dto = new StaffChatDTO();

    dto.setStaffId(staff.getStaffId());
    dto.setUsername(staff.getUsername());
    dto.setFirstname(staff.getFirstname());
    dto.setLastname(staff.getLastname());
    dto.setMobileNumber(staff.getMobileNumber());
    dto.setIsHead(staff.getIsHead());
    dto.setStaffRoleEnum(staff.getStaffRoleEnum());
    dto.setImageLink(staff.getProfilePicture().getImageLink());

    //departmentName
    if (staff.getUnit() != null) {
      dto.setDepartmentName(staff.getUnit().getName());
    }

    return dto;
  }

  //DO NOT USE
//  public Staff toEntity(StaffChatDTO dto) {
//    if (dto == null) {
//      return null;
//    }
//    return appointment;
//  }

  //DO NOT USE
  public List<StaffChatDTO> toDTOList(List<Staff> staffs) {
    List<StaffChatDTO> dtos = new ArrayList<>();
    for (Staff staff : staffs) {
      dtos.add(toDTO(staff));
    }
    return dtos;
  }

//  //DO NOT USE
//  public List<Appointment> toEntityList(List<AppointmentDTO> dtos) {
//    List<Appointment> appointments = new ArrayList<>();
//    for (AppointmentDTO dto : dtos) {
//      appointments.add(toEntity(dto));
//    }
//    return appointments;
//  }
}

