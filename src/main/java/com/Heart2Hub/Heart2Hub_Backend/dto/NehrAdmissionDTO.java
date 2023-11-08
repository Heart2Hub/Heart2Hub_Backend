package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NehrAdmissionDTO {
    private UUID admissionNehrId;
    private Integer duration;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime admissionDateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dischargeDateTime;
    private String comments;
    private NehrWardDTO ward;
}
