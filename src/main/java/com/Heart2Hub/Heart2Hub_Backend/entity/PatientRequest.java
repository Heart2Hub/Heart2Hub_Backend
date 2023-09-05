package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PatientRequestsEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
