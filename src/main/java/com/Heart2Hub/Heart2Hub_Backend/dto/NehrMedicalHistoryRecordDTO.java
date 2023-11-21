package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ProblemTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NehrMedicalHistoryRecordDTO {
    private UUID medicalHistoryRecordNehrId;
    private String description;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime resolvedDate;
    private PriorityEnum priorityEnum;
    private ProblemTypeEnum problemTypeEnum;
}
