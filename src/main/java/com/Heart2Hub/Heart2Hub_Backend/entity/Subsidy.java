package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Table(name = "subsidy")
public class Subsidy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subsidyId;

    @NotNull
    @Column(unique = true)
    private UUID subsidyNehrId = UUID.randomUUID();

    @NotNull
    private BigDecimal subsidyRate;

    @NotNull
    private ItemTypeEnum itemTypeEnum;

    @NotNull
    private LocalDateTime minDOB;

    @NotNull
    private String sex;

    @NotNull
    private String race;

    @NotNull
    private String nationality;

    private String subsidyName;

    private String subsidyDescription;

    public Subsidy(BigDecimal subsidyRate, ItemTypeEnum itemTypeEnum,
                   LocalDateTime minDOB, String sex, String race, String nationality,
                   String subsidyName, String subsidyDescription) {
        this.subsidyRate = subsidyRate;
        this.itemTypeEnum = itemTypeEnum;
        this.minDOB = minDOB;
        this.sex = sex;
        this.race = race;
        this.nationality = nationality;
        this.subsidyName = subsidyName;
        this.subsidyDescription = subsidyDescription;
    }

    public Subsidy(){}
}
