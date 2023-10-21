package com.Heart2Hub.Heart2Hub_Backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class NehrNextOfKinRecordDTO {
    private UUID nextOfKinRecordNehrId;
    private String relationship;
    private String nric;
}
