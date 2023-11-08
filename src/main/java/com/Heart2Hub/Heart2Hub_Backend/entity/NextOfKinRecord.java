package com.Heart2Hub.Heart2Hub_Backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "nextOfKinRecord")
public class NextOfKinRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nextOfKinRecordId;

    @NotNull
    @Column(unique = true)
    private UUID nextOfKinRecordNehrId = UUID.randomUUID();

    @NotBlank
    private String relationship;

    @NotBlank
    private String nric;

    public NextOfKinRecord() {
    }

    public NextOfKinRecord(String relationship, String nric) {
        this.relationship = relationship;
        this.nric = nric;
    }

}
