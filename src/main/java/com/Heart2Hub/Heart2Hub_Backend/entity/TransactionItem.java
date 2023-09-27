package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import lombok.Data;

@Entity
@Data
@Table(name = "transactionItem")
public class TransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionItemId;

    @NotNull
    private String transactionItemName;

    @NotNull
    private String transactionItemDescription;

    @NotNull
    private Integer transactionItemQuantity;

    @NotNull
    private BigDecimal transactionItemPrice;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "iventory_Item_Id", nullable = false)
    private InventoryItem inventoryItem;


    public TransactionItem(String transactionItemName, String transactionItemDescription, Integer transactionItemQuantity, BigDecimal transactionItemPrice, InventoryItem inventoryItem) {
        this.transactionItemName = transactionItemName;
        this.transactionItemDescription = transactionItemDescription;
        this.transactionItemQuantity = transactionItemQuantity;
        this.transactionItemPrice = transactionItemPrice;
        this.inventoryItem = inventoryItem;
    }

    public TransactionItem(){}
}
