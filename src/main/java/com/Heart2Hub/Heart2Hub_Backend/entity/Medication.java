package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "medication")
public class Medication extends InventoryItem {
    @NotNull
    private Long medicationId;

    @NotNull
    private String medicationUUID;

    @NotNull
    private BigDecimal retailPricePerQuantity;

    @NotNull
    private BigDecimal restockPricePerQuantity;

    @NotNull
    private Integer quantityInStock;

    public Medication(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, Long medicationId, String medicationUUID, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity, Integer quantityInStock) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
        this.medicationId = medicationId;
        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
    }

    public Medication(Long medicationId, String medicationUUID, BigDecimal retailPricePerQuantity, BigDecimal restockPricePerQuantity, Integer quantityInStock) {
        this.medicationId = medicationId;
        this.medicationUUID = medicationUUID;
        this.retailPricePerQuantity = retailPricePerQuantity;
        this.restockPricePerQuantity = restockPricePerQuantity;
        this.quantityInStock = quantityInStock;
    }

    public Medication() {}
}
