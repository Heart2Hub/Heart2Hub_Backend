package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Entity
@Data
@Table(name = "transactionItem")
public class TransactionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionItemId;

    @NotNull
    @Column(unique = true)
    private UUID transactionItemNehrId = UUID.randomUUID();

    @NotNull
    private String transactionItemName;

    @NotNull
    private String transactionItemDescription;

    @NotNull
    private Integer transactionItemQuantity;

    @NotNull
    private BigDecimal transactionItemPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "iventory_item_id", nullable = false)
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
