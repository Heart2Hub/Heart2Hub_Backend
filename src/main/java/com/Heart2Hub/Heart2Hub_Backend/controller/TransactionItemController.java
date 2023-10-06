package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Invoice;
import com.Heart2Hub.Heart2Hub_Backend.entity.TransactionItem;
import com.Heart2Hub.Heart2Hub_Backend.service.TransactionItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactionItem")
public class TransactionItemController {

    private final TransactionItemService transactionItemService;

    public TransactionItemController(TransactionItemService transactionItemService) {
        this.transactionItemService = transactionItemService;
    }

    @GetMapping("/getCartItems/{patientId}")
    public ResponseEntity<List<TransactionItem>> getCartItems(@PathVariable Long patientId) {
        List<TransactionItem> cartItems = transactionItemService.getCartItemsForPatient(patientId);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/addToCart/{patientId}")
    public ResponseEntity<TransactionItem> addToCart(@PathVariable Long patientId, @RequestBody TransactionItem transactionItem) {
        TransactionItem addedItem = transactionItemService.addToCart(patientId, transactionItem);
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
