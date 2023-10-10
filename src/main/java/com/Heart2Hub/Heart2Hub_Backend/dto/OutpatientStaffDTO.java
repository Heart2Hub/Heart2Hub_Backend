package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutpatientStaffDTO {
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
  //facilityBooking
  private Long facilityBookingId;
  private LocalDateTime facilityBookingStartDateTime;
  private LocalDateTime facilityBookingEndDateTime;
  private String facilityBookingComments;
  //Facility
  private Long facilityId;
  private String name;
  private String location;
  private String description;
  private Integer capacity;
  private FacilityStatusEnum facilityStatusEnum;
  private FacilityTypeEnum facilityTypeEnum;
  //appointment
  private Long currentAssignedAppointmentId;
  private SwimlaneStatusEnum currentAssignedAppointmentSwimlaneStatusEnum;
}
