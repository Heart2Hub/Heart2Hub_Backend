package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppointmentDTO {
  private Long appointmentId;
  private String description;
  private String comments;
  private LocalDateTime actualDateTime;
  private LocalDateTime bookedDateTime;
  private Time estimatedDuration;
  private Boolean arrived;
  private Time elapsedTime ;
  private PriorityEnum priorityEnum;
  private Long currentAssignedStaffId ;
  private Long patientId;
  private ElectronicHealthRecordDTO electronicHealthRecord;
  private List<Long> listOfStaffsId;
  private List<Long> listOfImageDocumentsId;
}
