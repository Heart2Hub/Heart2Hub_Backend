package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PatientRequestsEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "patientRequest")
public class PatientRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientRequestId;

    @NotNull
    private PatientRequestsEnum patientRequestEnum;

    @NotNull
    private boolean hasAttended = false;

    public PatientRequest(PatientRequestsEnum patientRequestEnum, boolean hasAttened) {
        this.patientRequestEnum = patientRequestEnum;
        this.hasAttended = hasAttened;
    }

    public PatientRequest(){}
}
