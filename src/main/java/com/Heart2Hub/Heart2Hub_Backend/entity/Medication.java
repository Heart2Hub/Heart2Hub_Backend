package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.AllergenEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.*;
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
    private BigDecimal retailPricePerQuantity;

    @NotNull
    private BigDecimal restockPricePerQuantity;

    @NotNull
    private Integer quantityInStock;

    @ElementCollection(targetClass = AllergenEnum.class)
    @CollectionTable
    @Enumerated(EnumType.STRING)
    private Collection<AllergenEnum> allergenEnumList;

    public Medication(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, Integer quantityInStock, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity, Collection<AllergenEnum> allergenEnumList) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
//        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
        this.allergenEnumList = allergenEnumList;

    }

    public Medication(Integer quantityInStock, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity, Collection<AllergenEnum> allergenEnumList) {
//        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
        this.allergenEnumList = allergenEnumList;

    }

    public Medication() {}
}
