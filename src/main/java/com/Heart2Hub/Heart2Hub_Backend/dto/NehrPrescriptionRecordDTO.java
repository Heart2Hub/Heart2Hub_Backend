package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PrescriptionStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NehrPrescriptionRecordDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private String medicationName;
    private Integer medicationQuantity;
    private Integer dosage;
    private String description;
    private String comments;
    private String prescribedBy;
    private PrescriptionStatusEnum prescriptionStatusEnum;
}
