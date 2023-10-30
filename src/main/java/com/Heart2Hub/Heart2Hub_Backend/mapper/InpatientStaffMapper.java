package com.Heart2Hub.Heart2Hub_Backend.mapper;

import com.Heart2Hub.Heart2Hub_Backend.dto.InpatientStaffDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.OutpatientStaffDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.Appointment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InpatientStaffMapper {

    private final ModelMapper modelMapper;

    public InpatientStaffMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.createTypeMap(Shift.class, InpatientStaffDTO.class)
                .addMapping(src -> src.getShiftId(),
                        InpatientStaffDTO::setShiftId)
                .addMapping(src -> src.getStartTime(),
                        InpatientStaffDTO::setStartTime)
                .addMapping(src -> src.getEndTime(),
                        InpatientStaffDTO::setEndTime)
                .addMapping(src -> src.getComments(),
                        InpatientStaffDTO::setComments)
                .addMapping(src -> src.getStaff().getStaffId(),
                        InpatientStaffDTO::setStaffId)
                .addMapping(src -> src.getStaff().getFirstname(),
                        InpatientStaffDTO::setFirstname)
                .addMapping(src -> src.getStaff().getLastname(),
                        InpatientStaffDTO::setLastname)
                .addMapping(src -> src.getStaff().getMobileNumber(),
                        InpatientStaffDTO::setMobileNumber)
                .addMapping(src -> src.getStaff().getIsHead(),
                        InpatientStaffDTO::setIsHead)
                .addMapping(src -> src.getStaff().getStaffRoleEnum(),
                        InpatientStaffDTO::setStaffRoleEnum)
                .addMapping(src -> src.getStaff().getProfilePicture().getImageDocumentId(),
                        InpatientStaffDTO::setImageDocumentId)
                .addMapping(src -> src.getStaff().getProfilePicture().getImageLink(),
                        InpatientStaffDTO::setImageLink);

    }

    public InpatientStaffDTO convertToDto(Shift shift) {
        InpatientStaffDTO dto = modelMapper.map(shift, InpatientStaffDTO.class);

        return dto;
    }

//    public Shift convertToEntity(OutpatientStaffDTO outpatientStaffDTO) {
//        return modelMapper.map(outpatientStaffDTO, Shift.class);
//    }
}