package com.Heart2Hub.Heart2Hub_Backend.entity;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.PatientRequestsEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    @NotNull
    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iventory_Item_Id")
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
