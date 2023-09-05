package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long inventoryItemId;
    @NotNull
    private String inventoryItemName;
    @NotNull
    private String inventoryItemDescription;
    @NotNull
    private ItemTypeEnum itemTypeEnum;

    public InventoryItem(String inventoryItemName, String inventoryItemDescription, ItemTypeEnum itemTypeEnum) {
        this.inventoryItemName = inventoryItemName;
        this.inventoryItemDescription = inventoryItemDescription;
        this.itemTypeEnum = itemTypeEnum;
    }

    public InventoryItem(){}
}
