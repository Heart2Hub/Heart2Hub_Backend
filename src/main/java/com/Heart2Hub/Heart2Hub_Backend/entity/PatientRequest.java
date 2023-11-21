package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PatientRequestEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "patientRequest")
public class PatientRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientRequestId;

    @NotNull
    @Column(unique = true)
    private UUID patientRequestNehrId = UUID.randomUUID();

    @NotNull
    private PatientRequestEnum patientRequestEnum;

    public PatientRequest(PatientRequestEnum patientRequestEnum) {
        this.patientRequestEnum = patientRequestEnum;
    }

    public PatientRequest(){}
}
