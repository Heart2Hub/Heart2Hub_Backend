package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Invoice;
import com.Heart2Hub.Heart2Hub_Backend.entity.TransactionItem;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/getAllInvoices")
    public List<Invoice> getAllInvoices() {
        return invoiceService.viewAllInvoices();
    }

    @GetMapping("/findPatientOfInvoice/{id}")
    public String findPatientOfInvoice(@PathVariable Long id) {
        ElectronicHealthRecord ehr = invoiceService.findPatientOfInvoice(id);
        String s = ehr.getFirstName() + " " + ehr.getLastName();
        return s;
    }

    @GetMapping("/findItemsOfInvoice/{id}")
    public List<TransactionItem> findItemsOfInvoice(@PathVariable Long id) {
        return invoiceService.findItemsOfInvoice(id);
    }

    @GetMapping("/getInvoicesByPatientId/{patientId}")
    public List<Invoice> getInvoicesByPatientId(@PathVariable Long patientId) {
        return invoiceService.viewAllInvoiceOfPatient(patientId);
    }

    @PostMapping("/createInvoice")
    public Invoice createInvoice(@RequestBody Invoice invoice) {
        return invoiceService.createNewInvoice(invoice);
    }

    @PutMapping("/updateInvoiceStatus/{invoiceId}")
    public Invoice updateInvoiceStatus(@PathVariable Long invoiceId, @RequestParam InvoiceStatusEnum status) {
        return invoiceService.updateInvoiceStatus(invoiceId, status);
    }

    @PostMapping("/createInsuranceClaim/{invoiceId}")
    public Invoice createInsuranceClaim(@PathVariable Long invoiceId, @RequestBody Map<String, Object> requestBody) {
        String insurerName = requestBody.get("insurerName").toString();
        BigDecimal insuranceClaimAmount = BigDecimal.valueOf(Double.parseDouble(requestBody.get("insuranceClaimAmount").toString()));
        boolean isPrivateInsurer = Boolean.parseBoolean(requestBody.get("isPrivateInsurer").toString());

        return invoiceService.createInsuranceClaim(invoiceId, insuranceClaimAmount, insurerName, isPrivateInsurer);
    }

    @PostMapping("/createMedishieldClaim/{invoiceId}")
    public Invoice createMedishieldClaim(@PathVariable Long invoiceId, @RequestBody  Map<String, Object> requestBody) {
        BigDecimal insuranceClaimAmount = BigDecimal.valueOf(Double.parseDouble(requestBody.get("insurerName").toString()));

        return invoiceService.createMedishieldClaim(invoiceId, insuranceClaimAmount);
    }
}

