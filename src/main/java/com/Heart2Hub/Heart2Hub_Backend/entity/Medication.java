package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.AllergenEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "medication")
public class Medication extends InventoryItem {

//    @NotNull
//    private String medicationUUID;

    @NotNull
    @Min(0)
    private BigDecimal retailPricePerQuantity;

    @NotNull
    @Min(0)
    private BigDecimal restockPricePerQuantity;

    @NotNull
    @Min(0)
    private Integer quantityInStock;

    @ElementCollection(targetClass = AllergenEnum.class)
    @CollectionTable
    @Enumerated(EnumType.STRING)
    private Collection<AllergenEnum> allergenEnumList;

    @NotNull
    private String comments;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = true)
    private List<DrugRestriction> drugRestrictions;

    public Medication() {
        this.drugRestrictions = new ArrayList<>();
    }
    public Medication(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, Integer quantityInStock, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity, Collection<AllergenEnum> allergenEnumList, String comments, List<DrugRestriction> drugRestrictions) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
//        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
        this.allergenEnumList = allergenEnumList;
        this.comments = comments;
        this.drugRestrictions =  drugRestrictions;
    }

    public Medication(Integer quantityInStock, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity, Collection<AllergenEnum> allergenEnumList, String comments, List<DrugRestriction> drugRestrictions) {
//        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
        this.allergenEnumList = allergenEnumList;
        this.comments = comments;
        this.drugRestrictions =  drugRestrictions;

    }

//    public Medication() {}
}
