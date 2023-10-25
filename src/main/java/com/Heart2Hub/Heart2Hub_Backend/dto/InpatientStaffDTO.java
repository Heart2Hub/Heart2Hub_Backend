package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InpatientStaffDTO {
    //staff
    private Long staffId;
    private String firstname;
    private String lastname;
    private Long mobileNumber;
    private Boolean isHead = false;
    private StaffRoleEnum staffRoleEnum;
    private Long imageDocumentId;
    private String imageLink;
    //shift
    private Long shiftId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String comments;
}
