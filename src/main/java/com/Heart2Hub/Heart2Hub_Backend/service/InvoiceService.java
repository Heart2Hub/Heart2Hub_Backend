package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.InsuranceClaim;
import com.Heart2Hub.Heart2Hub_Backend.entity.Invoice;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedishieldClaim;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.InsuranceClaimRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.InvoiceRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedishieldClaimRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final MedishieldClaimRepository medishieldClaimRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, PatientRepository patientRepository, MedishieldClaimRepository medishieldClaimRepository, InsuranceClaimRepository insuranceClaimRepository) {
        this.invoiceRepository = invoiceRepository;
        this.patientRepository = patientRepository;
        this.medishieldClaimRepository = medishieldClaimRepository;
        this.insuranceClaimRepository = insuranceClaimRepository;
    }

    public List<Invoice> viewAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> viewAllInvoiceOfPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .get().getListOfInvoices();
    }

    public Invoice createNewInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoiceStatus(Long invoiceId, InvoiceStatusEnum invoiceStatusEnum) {
        Invoice invoice = invoiceRepository.findById(invoiceId).get();
        invoice.setInvoiceStatusEnum(invoiceStatusEnum);

        invoiceRepository.save(invoice);
        return invoice;
    }

    public Invoice createInsuranceClaim(Long invoiceId, BigDecimal insuranceClaimAmount,
                                        String insurerName, Boolean isPrivateInsurer) {

        InsuranceClaim claim = new InsuranceClaim(LocalDateTime.now(),insuranceClaimAmount, insurerName, isPrivateInsurer);
        insuranceClaimRepository.save(claim);

        Invoice i = invoiceRepository.findById(invoiceId).get();
        i.setInvoiceAmount(i.getInvoiceAmount().subtract(insuranceClaimAmount));
        String costBreakdown = i.getInvoiceBreakdown() +
                "\nInsurance Claim: " + insurerName + ", Amount: -$" + insuranceClaimAmount +
                "\nFinal Total: $" + i.getInvoiceAmount();

        i.setInvoiceBreakdown(costBreakdown);
        i.setInsuranceClaim(claim);
        invoiceRepository.save(i);
        return i;
    }

    public Invoice createMedishieldClaim(Long invoiceId, BigDecimal insuranceClaimAmount) {
        Invoice i = invoiceRepository.findById(invoiceId).get();

        MedishieldClaim claim = new MedishieldClaim(LocalDateTime.now(),insuranceClaimAmount, ApprovalStatusEnum.PENDING);
        medishieldClaimRepository.save(claim);

        i.setInvoiceAmount(i.getInvoiceAmount().subtract(insuranceClaimAmount));
        String costBreakdown = i.getInvoiceBreakdown() +
                "\nMedishield Claim, Amount: -$" + insuranceClaimAmount +
                "\nFinal Total: $" + i.getInvoiceAmount();;

        i.setInvoiceBreakdown(costBreakdown);
        i.setMedishieldClaim(claim);
        invoiceRepository.save(i);
        return i;
    }
}
