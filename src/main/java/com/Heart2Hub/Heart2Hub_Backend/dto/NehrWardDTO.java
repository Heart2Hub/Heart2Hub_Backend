package com.Heart2Hub.Heart2Hub_Backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class NehrWardDTO {
    private UUID unitNehrId;
    private String name;
    private String location;
    private NehrWardClassDTO wardClass;
}
