package com.Heart2Hub.Heart2Hub_Backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ElectronicHealthRecordDTO {
  private Long electronicHealthRecordId;
  private String firstname;
  private String lastname;
  private String nric;
//  private LocalDateTime dateOfBirth;
  private String placeOfBirth;
  private String sex;
  private String race;
  private String nationality;
  private String address;
//  private String contactNumber;
  private List<Long> listOfSubsidiesId;
  private List<Long> listOfPastAdmissionsId;
  private List<Long> listOfPastAppointmentsId;
  private List<Long> listOfNextOfKinRecordsId;
  private List<Long> listOfPrescriptionRecordsId;
  private List<Long> listOfProblemRecordsId;
  private List<Long> listOfMedicalHistoryRecordsId;
  private List<Long> listOfTreatmentPlanRecordsId;
}
