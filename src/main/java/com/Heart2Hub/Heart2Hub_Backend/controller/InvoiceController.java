package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.InventoryItemProfitDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/findInvoiceUsingTransaction/{id}")
    public ResponseEntity<Long> findInvoiceUsingTransaction(@PathVariable Long id) {
        Long invoice = invoiceService.findInvoiceUsingTransaction(id);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @GetMapping("/findProfitByServiceItem")
    public ResponseEntity<List<InventoryItemProfitDTO>> findProfitByServiceItem() {
        List<InventoryItemProfitDTO> list = invoiceService.findProfitByServiceItem();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/findProfitByMedication")
    public ResponseEntity<List<InventoryItemProfitDTO>> findProfitByMedication() {
        List<InventoryItemProfitDTO> list = invoiceService.findProfitByMedication();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/findProfitByInventoryItem")
    public ResponseEntity<List<InventoryItemProfitDTO>> findProfitByInventoryItem() {
        List<InventoryItemProfitDTO> list = invoiceService.findProfitByInventoryItem();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/findMedishieldClaimOfInvoice/{id}")
    public ResponseEntity<MedishieldClaim> findMedishieldClaimOfInvoice(@PathVariable Long id) {
        MedishieldClaim medishieldClaim = invoiceService.findMedishieldClaimOfInvoice(id);
        return new ResponseEntity<>(medishieldClaim, HttpStatus.OK);
    }

    @GetMapping("/findInsuranceClaimOfInvoice/{id}")
    public ResponseEntity<InsuranceClaim> findInsuranceClaimOfInvoice(@PathVariable Long id) {
        InsuranceClaim insuranceClaim = invoiceService.findInsuranceClaimOfInvoice(id);
        return new ResponseEntity<>(insuranceClaim, HttpStatus.OK);
    }

    @GetMapping("/findTransactionItemOfInvoice/{id}")
    public ResponseEntity<List<TransactionItem>> findTransactionItemOfInvoice(@PathVariable Long id) {
        List<TransactionItem> transactionItems = invoiceService.findTransactionItemOfInvoice(id);
        return new ResponseEntity<>(transactionItems, HttpStatus.OK);
    }

    @GetMapping("/findInvoicesOfAPatient/{username}")
    public ResponseEntity<List<Invoice>> findInvoicesOfAPatient(@PathVariable String username) {
        return ResponseEntity.ok(invoiceService.findInvoicesOfAPatient(username));
    }

    @GetMapping("/findInvoicesOfAPatientEarliest/{username}")
    public ResponseEntity<List<Invoice>> findInvoicesOfAPatientEarliest(@PathVariable String username) {
        return ResponseEntity.ok(invoiceService.findInvoicesOfAPatientEarliest(username));
    }

    @GetMapping("/getAllInvoices")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.viewAllInvoices());
    }
    @GetMapping("/findInvoice/{id}")
    public ResponseEntity<Invoice> findInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findInvoice(id));
    }

    @GetMapping("/findPatientOfInvoice/{id}")
    public ResponseEntity<String> findPatientOfInvoice(@PathVariable Long id) {
        ElectronicHealthRecord ehr = invoiceService.findPatientOfInvoice(id);
        String s = ehr.getFirstName() + " " + ehr.getLastName();
        return ResponseEntity.ok(s);
    }

    @GetMapping("/findPatientNRICOfInvoice/{id}")
    public ResponseEntity<String> findPatientNRICOfInvoice(@PathVariable Long id) {
        ElectronicHealthRecord ehr = invoiceService.findPatientOfInvoice(id);
        return ResponseEntity.ok(ehr.getNric());
    }

    @GetMapping("/findItemsOfInvoice/{id}")
    public ResponseEntity<List<TransactionItem>> findItemsOfInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findItemsOfInvoice(id));
    }

    @GetMapping("/getInvoicesByPatientId/{patientId}")
    public ResponseEntity<List<Invoice>> getInvoicesByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(invoiceService.viewAllInvoiceOfPatient(patientId));
    }

    @PostMapping("/createInvoice")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.ok(invoiceService.createNewInvoice(invoice));
    }

    @PutMapping("/updateInvoiceStatus/{invoiceId}")
    public ResponseEntity<Invoice> updateInvoiceStatus(@PathVariable Long invoiceId, @RequestParam InvoiceStatusEnum status) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(invoiceId, status));
    }

    @PostMapping("/createInsuranceClaim/{invoiceId}")
    public ResponseEntity<Invoice> createInsuranceClaim(@PathVariable Long invoiceId, @RequestBody Map<String, Object> requestBody) {
        String insurerName = requestBody.get("insurerName").toString();
        BigDecimal insuranceClaimAmount = BigDecimal.valueOf(Double.parseDouble(requestBody.get("insuranceClaimAmount").toString()));
        boolean isPrivateInsurer = Boolean.parseBoolean(requestBody.get("isPrivateInsurer").toString());

        return ResponseEntity.ok(invoiceService.createInsuranceClaim(invoiceId, insuranceClaimAmount, insurerName, isPrivateInsurer));
    }

    @PostMapping("/createMedishieldClaim/{invoiceId}")
    public ResponseEntity<Invoice> createMedishieldClaim(@PathVariable Long invoiceId, @RequestBody  Map<String, Object> requestBody) {
        BigDecimal insuranceClaimAmount = BigDecimal.valueOf(Double.parseDouble(requestBody.get("medishieldClaimAmount").toString()));

        return ResponseEntity.ok(invoiceService.createMedishieldClaim(invoiceId, insuranceClaimAmount));
    }

    @DeleteMapping("/deleteInsuranceClaim/{claimId}/{invoiceId}")
    public void deleteInsuranceClaim(@PathVariable Long claimId, @PathVariable Long invoiceId) {
         invoiceService.deleteInsuranceClaim(claimId, invoiceId);
    }

    @DeleteMapping("/deleteMedishieldClaim/{claimId}/{invoiceId}")
    public void deleteMedishieldClaim(@PathVariable Long claimId, @PathVariable Long invoiceId) {
        invoiceService.deleteMedishieldClaim(claimId, invoiceId);
    }

    @PutMapping("/approveMedishieldClaim/{medishieldId}/{invoiceId}")
    public ResponseEntity<MedishieldClaim> approveMedishieldClaim(@PathVariable Long medishieldId, @PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.approveMedishieldClaim(medishieldId, invoiceId));
    }

    @PutMapping("/rejectMedishieldClaim/{medishieldId}")
    public ResponseEntity<MedishieldClaim> rejectMedishieldClaim(@PathVariable Long medishieldId) {
        return ResponseEntity.ok(invoiceService.rejectMedishieldClaim(medishieldId));
    }

}

