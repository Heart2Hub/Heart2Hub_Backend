package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class AdmissionDTO {
    private Long admissionId;
    private String reason;
    private String comments;
    private Integer duration;
    private Integer room;
    private Integer bed;
    private LocalDateTime admissionDateTime;
    private LocalDateTime dischargeDateTime;

    private Long assignedNurseId;
    private Long assignedDoctorId;
    //private List<Long> listOfStaffsId;

    private Long patientId;

    //private List<String> listOfImageDocumentsImageLinks;
    private Long electronicHealthRecordId;
    private String firstName;
    private String lastName;
    private String username;
    private String nric;
    private String placeOfBirth;
    private String sex;
    private String contactNumber;
    private String nationality;
    private LocalDateTime dateOfBirth;
    //private String wardName;
    private String patientProfilePicture;

}
