package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.*;
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

    @ManyToOne(fetch = FetchType.EAGER,optional = true)
    @JoinColumn(name = "unit_id", nullable = true)
    private Unit unit;

    public ServiceItem(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum, BigDecimal retailPricePerQuantity) {
        super(inventoryItemName, inventoryItemDescription, itemTypeEnum);
        this.retailPricePerQuantity = retailPricePerQuantity;
    }

    public ServiceItem(BigDecimal retailPricePerQuantity) {
        this.retailPricePerQuantity = retailPricePerQuantity;
    }

    public ServiceItem() {}
}
