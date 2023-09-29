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
@Table(name = "serviceItem")
public class ServiceItem extends InventoryItem{

    @NotNull
    private BigDecimal retailPricePerQuantity;

    public ServiceItem(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, BigDecimal retailPricePerQuantity) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
        this.retailPricePerQuantity = retailPricePerQuantity;
    }

    public ServiceItem(BigDecimal retailPricePerQuantity) {
        this.retailPricePerQuantity = retailPricePerQuantity;
    }

    public ServiceItem() {}
}
