package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "serviceItem")
public class ServiceItem extends InventoryItem{
    @NotNull
    private Long serviceItemId;

    @NotNull
    private BigDecimal retailPricePerQuantity;

    public ServiceItem(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, Long serviceItemId, BigDecimal retailPricePerQuantity) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
        this.serviceItemId = serviceItemId;
        this.retailPricePerQuantity = retailPricePerQuantity;
    }

    public ServiceItem(Long serviceItemId, BigDecimal retailPricePerQuantity) {
        this.serviceItemId = serviceItemId;
        this.retailPricePerQuantity = retailPricePerQuantity;
    }

    public ServiceItem() {}
}
