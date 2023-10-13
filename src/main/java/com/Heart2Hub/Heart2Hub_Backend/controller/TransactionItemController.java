package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.InventoryItem;
import com.Heart2Hub.Heart2Hub_Backend.entity.Invoice;
import com.Heart2Hub.Heart2Hub_Backend.entity.TransactionItem;
import com.Heart2Hub.Heart2Hub_Backend.service.TransactionItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactionItem")
@RequiredArgsConstructor
public class TransactionItemController {

    private final TransactionItemService transactionItemService;

    @GetMapping("/getCartItems/{patientId}")
    public ResponseEntity<List<TransactionItem>> getCartItems(@PathVariable Long patientId) {
        List<TransactionItem> cartItems = transactionItemService.getCartItemsForPatient(patientId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/getAllItems")
    public ResponseEntity<List<TransactionItem>> getAllItems() {
        List<TransactionItem> cartItems = transactionItemService.getAllItems();
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/addToCart/{patientId}")
    public ResponseEntity<TransactionItem> addToCart(@PathVariable Long patientId, @RequestBody Map<String, Object> requestBody) {
        String inventoryItemName = requestBody.get("transactionItemName").toString();
        String inventoryItemDescription = requestBody.get("transactionItemDescription").toString();
        Integer transactionItemQuantity = Integer.parseInt(requestBody.get("transactionItemQuantity").toString());
        BigDecimal transactionItemPrice = BigDecimal.valueOf(Integer.parseInt(requestBody.get("transactionItemPrice").toString()));
        Long itemId = Long.parseLong(requestBody.get("inventoryItem").toString());
        TransactionItem addedItem = transactionItemService.addToCart(patientId, inventoryItemName,
                inventoryItemDescription, transactionItemQuantity, transactionItemPrice, itemId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItem);
    }

    @DeleteMapping("/removeFromCart/{patientId}/{transactionItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long patientId, @PathVariable Long transactionItemId) {
        transactionItemService.removeFromCart(patientId, transactionItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clearCart/{patientId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long patientId) {
        transactionItemService.clearCart(patientId);
        return ResponseEntity.noContent().build();
    }

    // Implement a checkout endpoint as needed
    @PostMapping("/checkout/{patientId}")
    public Invoice checkout(@PathVariable Long patientId) {
        return transactionItemService.checkout(patientId);
    }

}
