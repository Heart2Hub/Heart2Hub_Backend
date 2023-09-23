package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PatientRequestEnum;
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
    private PatientRequestEnum patientRequestEnum;

    public PatientRequest(PatientRequestEnum patientRequestEnum) {
        this.patientRequestEnum = patientRequestEnum;
    }

    public PatientRequest(){}
}
