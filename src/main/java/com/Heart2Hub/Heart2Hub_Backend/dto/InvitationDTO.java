package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InvitationDTO {

  private Long invitationId;
  private LocalDateTime createdDate;
  private String invitedBy;
  private Boolean isPrimary;
  private Boolean isRead;
  private Boolean isApproved;

  //TreatmentPlanRecord
  private Long treatmentPlanRecordId;
  //EHR
  private Long electronicHealthRecordId;
  //Staff
  private Long staffId;
  private String username;
  private String firstname;
  private String lastname;
  private Long mobileNumber;
  private Boolean isHead = false;
  private StaffRoleEnum staffRoleEnum;
}
