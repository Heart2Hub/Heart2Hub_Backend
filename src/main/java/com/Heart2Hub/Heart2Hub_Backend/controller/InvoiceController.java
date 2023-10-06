package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Invoice;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/getAllInvoices")
    public List<Invoice> getAllInvoices() {
        return invoiceService.viewAllInvoices();
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
    public Invoice createInsuranceClaim(@PathVariable Long invoiceId, @RequestParam BigDecimal insuranceClaimAmount,
                                        @RequestParam String insurerName, @RequestParam Boolean isPrivateInsurer) {
        return invoiceService.createInsuranceClaim(invoiceId, insuranceClaimAmount, insurerName, isPrivateInsurer);
    }

    @PostMapping("/createMedishieldClaim/{invoiceId}")
    public Invoice createMedishieldClaim(@PathVariable Long invoiceId, @RequestParam BigDecimal insuranceClaimAmount) {
        return invoiceService.createMedishieldClaim(invoiceId, insuranceClaimAmount);
    }
}

