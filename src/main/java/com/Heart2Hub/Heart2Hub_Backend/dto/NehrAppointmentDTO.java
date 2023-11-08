package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class NehrAppointmentDTO {
    private UUID appointmentNehrId;
    private String description;
    private String comments = "";
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime bookedDateTime;
    private NehrDepartmentDTO department;
    private List<NehrStaffDTO> listOfStaff;
}
