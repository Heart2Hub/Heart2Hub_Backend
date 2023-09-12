package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "medicationOrder")
public class MedicationOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicationOrderId;

    @NotNull
    private String title;

    @NotNull
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "medication_id")
    private Medication medication;

    @NotNull
    private Integer dosage;

    @NotNull
    private String comments;

    @NotNull
    private Integer frequency;

    @NotNull
    private Boolean isActive;

    @NotNull
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id")
    private Admission admission;

    @NotNull
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "prescription_record_id")
    private PrescriptionRecord prescriptionRecord;

    @NotNull
    @JsonBackReference
    @OneToMany(mappedBy = "medicationOrder")
    private List<MedicationOrderEvent> listOfMedicationOrderEvents;


    public MedicationOrder() {
        this.isActive = true;
        this.listOfMedicationOrderEvents = List.of();
    }

    public MedicationOrder(String title, Medication medication, Integer dosage, String comments, Integer frequency, Admission admission, PrescriptionRecord prescriptionRecord) {
        this();
        this.title = title;
        this.medication = medication;
        this.dosage = dosage;
        this.comments = comments;
        this.frequency = frequency;
        this.admission = admission;
        this.prescriptionRecord = prescriptionRecord;
    }
}
