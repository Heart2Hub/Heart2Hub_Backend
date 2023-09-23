package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "consumableEquipment")
public class ConsumableEquipment extends InventoryItem{

    @NotNull
    private Long consumableEquipment;

    @NotNull
    private Integer quantityInStock;

    @NotNull
    private BigDecimal restockPricePerQuantity;

    public ConsumableEquipment(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, Long consumableEquipment, Integer quantityInStock, BigDecimal restockPricePerQuantity) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
        this.consumableEquipment = consumableEquipment;
        this.quantityInStock = quantityInStock;
        this.restockPricePerQuantity = restockPricePerQuantity;
    }

    public ConsumableEquipment(Long consumableEquipment, Integer quantityInStock, BigDecimal restockPricePerQuantity) {
        this.consumableEquipment = consumableEquipment;
        this.quantityInStock = quantityInStock;
        this.restockPricePerQuantity = restockPricePerQuantity;
    }

    public ConsumableEquipment() {}
}
