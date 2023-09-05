package com.Heart2Hub.Heart2Hub_Backend.entity;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "allocatedInventory")
public class AllocatedInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocatedInventoryId;

    @NotNull
    private Integer allocatedInventoryCurrentQuantity;

    @NotNull
    private Integer minimumQuantityBeforeRestock;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "consumble_Equipment_Id")
    private ConsumableEquipment consumableEquipment;

    public AllocatedInventory(Integer allocatedInventoryCurrentQuantity, Integer minimumQuantityBeforeRestock) {
        this.allocatedInventoryCurrentQuantity = allocatedInventoryCurrentQuantity;
        this.minimumQuantityBeforeRestock = minimumQuantityBeforeRestock;
    }

    public AllocatedInventory(){}
}
