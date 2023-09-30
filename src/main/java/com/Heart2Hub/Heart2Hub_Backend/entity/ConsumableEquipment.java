package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "consumableEquipment")
public class ConsumableEquipment extends InventoryItem{

    @Min(value = 1, message = "Quantity cannot be less than 1")
    @NotNull
    private Integer quantityInStock;

    @Min(value = 0, message = "Price cannot be less than 0")
    @NotNull
    private BigDecimal restockPricePerQuantity;

    public ConsumableEquipment(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, Integer quantityInStock, BigDecimal restockPricePerQuantity) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
//        this.consumableEquipmentId = consumableEquipmentId;
        this.quantityInStock = quantityInStock;
        this.restockPricePerQuantity = restockPricePerQuantity;
    }

    public ConsumableEquipment(Integer quantityInStock, BigDecimal restockPricePerQuantity) {
//        this.consumableEquipmentId = consumableEquipmentId;
        this.quantityInStock = quantityInStock;
        this.restockPricePerQuantity = restockPricePerQuantity;
    }

    public ConsumableEquipment() {}


}
