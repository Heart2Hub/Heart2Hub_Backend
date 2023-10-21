package com.Heart2Hub.Heart2Hub_Backend.dto;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NehrSubsidyDTO {
    private UUID subsidyNehrId;
    private BigDecimal subsidyRate;
    private ItemTypeEnum itemTypeEnum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime minDOB;
    private String sex;
    private String race;
    private String nationality;
    private String subsidyName;
    private String subsidyDescription;
}
