package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.TreatmentPlanTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class NehrTreatmentPlanRecordDTO {
    private UUID treatmentPlanRecordNehrId;
    private String description;
    private String primaryDoctor;
    private List<String> secondaryDoctors;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private TreatmentPlanTypeEnum treatmentPlanTypeEnum;
}
