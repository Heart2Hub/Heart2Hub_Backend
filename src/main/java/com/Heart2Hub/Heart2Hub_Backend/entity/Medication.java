package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

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

    public Medication(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, Integer quantityInStock, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
//        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
    }

    public Medication(Integer quantityInStock, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity) {
//        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
    }

    public Medication() {}
}
