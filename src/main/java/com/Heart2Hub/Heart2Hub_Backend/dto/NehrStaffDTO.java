package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import lombok.Data;

import java.util.UUID;

@Data
public class NehrStaffDTO {
    private UUID staffNehrId;
    private String firstname;
    private String lastname;
    private StaffRoleEnum staffRoleEnum;
}
