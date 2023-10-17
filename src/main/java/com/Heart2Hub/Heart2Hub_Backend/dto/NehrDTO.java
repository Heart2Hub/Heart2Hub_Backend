package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// TO-DO: Include default values for non-mapped not null fields. So when pulled from NEHR can just create a new instance.
@Data
public class NehrDTO {
    private String nric;
    private String firstname;
    private String lastname;
    private LocalDateTime dateOfBirth;
    private String placeOfBirth;
    private String sex;
    private String race;
    private String nationality;
    private String address;
    private String contactNumber;
    private List<NehrSubsidyDTO> listOfSubsidies;
    private List<NehrAdmissionDTO> listOfPastAdmissions;
    private List<NehrAppointmentDTO> listOfPastAppointments;
    private List<NehrNextOfKinRecordDTO> listOfNextOfKinRecords;
    private List<NehrPrescriptionRecordDTO> listOfPrescriptionRecords;
    private List<NehrProblemRecordDTO> listOfProblemRecords;
    private List<NehrMedicalHistoryRecordDTO> listOfMedicalHistoryRecords;
    private List<NehrTreatmentPlanRecordDTO> listOfTreatmentPlanRecords;
}

@Data
public class NehrSubsidyDTO {
    private BigDecimal subsidyRate;
    private ItemTypeEnum itemTypeEnum;
    private LocalDateTime minDOB;
    private String sex;
    private String race;
    private String nationality;
    private String subsidyName;
    private String subsidyDescription;
}

@Data
public class NehrAdmissionDTO {
    private Integer duration;
    private LocalDateTime admissionDateTime;
    private LocalDateTime dischargeDateTime;
    private String comments;
    private NehrWardDTO ward;
}

@Data
public class NehrWardDTO {
    private String name;
    private String location;
    private Integer capacity;
    private NehrWardClassDTO wardClass;
}

@Data
public class NehrWardClassDTO {
    private String wardClassName;
}

@Data
public class NehrAppointmentDTO {
    private String description;
    private String comments = "";
    private LocalDateTime actualDateTime;
    private LocalDateTime bookedDateTime;
}


