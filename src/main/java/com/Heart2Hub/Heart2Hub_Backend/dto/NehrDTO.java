package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// TO-DO: Include default values for non-mapped not null fields. So when pulled from NEHR can just create a new instance.
@Data
public class NehrDTO {
    private UUID electronicHealthRecordNehrId;
    private String nric;
    private String firstName;
    private String lastName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

