package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long inventoryItemId;

    @NotNull
    @Column(unique = true)
    private UUID inventoryItemNehrId = UUID.randomUUID();

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

    public void setInventoryItemName(String inventoryItemName) {
        this.inventoryItemName = inventoryItemName;
    }

    public void setInventoryItemDescription(String inventoryItemDescription) {
        this.inventoryItemDescription = inventoryItemDescription;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public void setItemTypeEnum(ItemTypeEnum itemTypeEnum) {
        this.itemTypeEnum = itemTypeEnum;
    }

    public InventoryItem(){}

}
