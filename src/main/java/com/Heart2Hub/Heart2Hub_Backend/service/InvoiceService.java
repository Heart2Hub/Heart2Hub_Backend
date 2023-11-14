package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.dto.InventoryItemProfitDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.ClaimErrorException;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final MedishieldClaimRepository medishieldClaimRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final TransactionRepository transactionRepository;

    private final TransactionService transactionService;

    public InvoiceService(InvoiceRepository invoiceRepository, PatientRepository patientRepository, MedishieldClaimRepository medishieldClaimRepository, InsuranceClaimRepository insuranceClaimRepository,
                          TransactionItemRepository transactionItemRepository,
                          TransactionRepository transactionRepository, TransactionService transactionService) {
        this.invoiceRepository = invoiceRepository;
        this.patientRepository = patientRepository;
        this.medishieldClaimRepository = medishieldClaimRepository;
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    public List<Invoice> findInvoicesOfAPatient(String username) {
        Patient p = patientRepository.findByUsername(username).get();
        List<Invoice> invoiceList = invoiceRepository.findInvoiceByPatient(p);

        LocalDateTime current = LocalDateTime.now();
        for (int i = 0; i < invoiceList.size(); i ++) {
            LocalDateTime invoiceDueDate = invoiceList.get(i).getInvoiceDueDate();
            if (current.isAfter(invoiceDueDate)) {
                Invoice invoice = invoiceList.get(i);
                invoice.setInvoiceStatusEnum(InvoiceStatusEnum.OVERDUE);
            }
        }

        // Sort invoices by the latest date
        Collections.sort(invoiceList, Comparator.comparing(Invoice::getInvoiceDueDate).reversed());

        invoiceRepository.saveAll(invoiceList);
        return invoiceList;
    }

    public List<Invoice> findInvoicesOfAPatientEarliest(String username) {
        Patient p = patientRepository.findByUsername(username).get();
        List<Invoice> invoiceList = invoiceRepository.findInvoiceByPatient(p);

        LocalDateTime current = LocalDateTime.now();
        for (int i = 0; i < invoiceList.size(); i++) {
            LocalDateTime invoiceDueDate = invoiceList.get(i).getInvoiceDueDate();
            if (current.isAfter(invoiceDueDate)) {
                Invoice invoice = invoiceList.get(i);
                invoice.setInvoiceStatusEnum(InvoiceStatusEnum.OVERDUE);
            }
        }

        // Sort invoices by the earliest date
        Collections.sort(invoiceList, Comparator.comparing(Invoice::getInvoiceDueDate));

        invoiceRepository.saveAll(invoiceList);
        return invoiceList;
    }

    public Long findInvoiceUsingTransaction(Long id) {
        Transaction t = transactionRepository.findById(id).get();
        return invoiceRepository.findInvoiceByTransaction(t).getInvoiceId();
    }

    public MedishieldClaim findMedishieldClaimOfInvoice(Long id) {
        return invoiceRepository.findById(id).get().getMedishieldClaim();
    }

    public InsuranceClaim findInsuranceClaimOfInvoice(Long id) {
        return invoiceRepository.findById(id).get().getInsuranceClaim();
    }

    public List<TransactionItem> findTransactionItemOfInvoice(Long id) {
        return invoiceRepository.findById(id).get().getListOfTransactionItem();
    }

    public Invoice findInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId).get();
    }

    public List<Invoice> viewAllInvoices() {
        List<Invoice> invoiceList = invoiceRepository.findAll();
        LocalDateTime current = LocalDateTime.now();
        for (int i = 0; i < invoiceList.size(); i ++){
            LocalDateTime invoiceDueDate = invoiceList.get(i).getInvoiceDueDate();
            if (current.isAfter(invoiceDueDate)) {
                Invoice invoice = invoiceList.get(i);
                invoice.setInvoiceStatusEnum(InvoiceStatusEnum.OVERDUE);

                Transaction t = transactionService.createTransaction(invoice.getInvoiceId(), invoice.getInvoiceAmount(), ApprovalStatusEnum.REJECTED);
                invoice.setInvoiceStatusEnum(InvoiceStatusEnum.OVERDUE);
            }
        }

        invoiceRepository.saveAll(invoiceList);
        return invoiceRepository.findAll();
    }

    public List<Invoice> viewAllInvoiceOfPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .get().getListOfInvoices();
    }

    public ElectronicHealthRecord findPatientOfInvoice(Long invoiceId) {
        Patient p = invoiceRepository.findById(invoiceId).get().getPatient();
        return p.getElectronicHealthRecord();
    }

    public List<TransactionItem> findItemsOfInvoice(Long invoiceId) {
        Invoice i = invoiceRepository.findById(invoiceId).get();
        return i.getListOfTransactionItem();
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
        if (insuranceClaimAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ClaimErrorException("Insurance claim amount must be greater than zero.");
        }

        if (insurerName == null || insurerName.isEmpty()) {
            throw new ClaimErrorException("Insurer name must be provided.");
        }
        InsuranceClaim claim = new InsuranceClaim(LocalDateTime.now(),insuranceClaimAmount, insurerName, isPrivateInsurer);
        insuranceClaimRepository.save(claim);

        Invoice i = invoiceRepository.findById(invoiceId).get();

        if (insuranceClaimAmount.compareTo(i.getInvoiceAmount()) > 0) {
            throw new ClaimErrorException("Insurance claim cannot exceed Invoice Amount!");
        }

        TransactionItem item = new TransactionItem("(Insurance Claim: " + insurerName + ")", insurerName, 1,
                insuranceClaimAmount.multiply(BigDecimal.valueOf(-1)), null);

        i.setInvoiceAmount(i.getInvoiceAmount().subtract(insuranceClaimAmount));
        String costBreakdown = i.getInvoiceBreakdown() +
                "\nInsurance Claim: " + insurerName + ", Amount: -$" + insuranceClaimAmount +
                "\nFinal Total: $" + i.getInvoiceAmount();

        i.setInvoiceBreakdown(costBreakdown);
        i.setInsuranceClaim(claim);

        if (i.getInvoiceAmount().compareTo(BigDecimal.ZERO) == 0) {
            i.setInvoiceStatusEnum(InvoiceStatusEnum.PAID);
        }
        transactionItemRepository.save(item);
        i.getListOfTransactionItem().add(item);
        invoiceRepository.save(i);
        return i;
    }

    public MedishieldClaim approveMedishieldClaim(Long medishieldId, Long invoiceId) {
        MedishieldClaim claim = medishieldClaimRepository.findById(medishieldId).get();
        Invoice invoice = invoiceRepository.findById(invoiceId).get();
        claim.setApprovalStatusEnum(ApprovalStatusEnum.APPROVED);

        TransactionItem item = new TransactionItem("(Medishield Claim) " , "Medishield", 1,
                claim.getMedishieldClaimAmount().multiply(BigDecimal.valueOf(-1)), null);

        invoice.setInvoiceAmount(invoice.getInvoiceAmount().subtract(claim.getMedishieldClaimAmount()));
        if (invoice.getInvoiceAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setInvoiceStatusEnum(InvoiceStatusEnum.PAID);
        } else {
            invoice.setInvoiceStatusEnum(InvoiceStatusEnum.UNPAID);
        }

        transactionItemRepository.save(item);
        invoice.getListOfTransactionItem().add(item);
        invoiceRepository.save(invoice);
        medishieldClaimRepository.save(claim);
        return claim;
    }

    public MedishieldClaim rejectMedishieldClaim(Long medishieldId) {
        MedishieldClaim claim = medishieldClaimRepository.findById(medishieldId).get();
        Invoice i = invoiceRepository.findInvoiceByMedishieldClaim(claim);
        i.setInvoiceStatusEnum(InvoiceStatusEnum.UNPAID);
        invoiceRepository.save(i);
        claim.setApprovalStatusEnum(ApprovalStatusEnum.REJECTED);
        medishieldClaimRepository.save(claim);
        return claim;
    }

    public void deleteInsuranceClaim(Long claimId, Long invoiceId) {
        InsuranceClaim claim = insuranceClaimRepository.findById(claimId).get();
        Invoice invoice = invoiceRepository.findById(invoiceId).get();

        List<TransactionItem> items = invoice.getListOfTransactionItem();
        TransactionItem deleteItem = null;
        for (int i = 0; i < items.size(); i++) {
            String description = items.get(i).getTransactionItemDescription();
            if (description.contains(claim.getInsurerName())) {
                deleteItem = items.get(i);
            }
        }
        invoice.getListOfTransactionItem().remove(deleteItem);
        invoice.setInsuranceClaim(null);
        if (deleteItem != null) {
            transactionItemRepository.delete(deleteItem);
        }
            invoice.setInvoiceStatusEnum(InvoiceStatusEnum.UNPAID);


        BigDecimal newAmount = invoice.getInvoiceAmount().add(claim.getInsuranceClaimAmount());
        invoice.setInvoiceAmount(newAmount);
        invoiceRepository.save(invoice);
        insuranceClaimRepository.delete(claim);

    }

    public void deleteMedishieldClaim(Long claimId, Long invoiceId) {
        MedishieldClaim claim = medishieldClaimRepository.findById(claimId).get();
        Invoice invoice = invoiceRepository.findById(invoiceId).get();

        List<TransactionItem> items = invoice.getListOfTransactionItem();
        TransactionItem deleteItem = null;

        if (claim.getApprovalStatusEnum().equals(ApprovalStatusEnum.APPROVED)) {
            for (int i = 0; i < items.size(); i++) {
                String description = items.get(i).getTransactionItemDescription();
                if (description.contains("Medishield")) {
                    deleteItem = items.get(i);
                }
            }
        }
        invoice.getListOfTransactionItem().remove(deleteItem);
        invoice.setMedishieldClaim(null);
        if (deleteItem != null) {
            transactionItemRepository.delete(deleteItem);
        }

        if (claim.getApprovalStatusEnum().equals(ApprovalStatusEnum.APPROVED)) {
            BigDecimal newAmount = invoice.getInvoiceAmount().add(claim.getMedishieldClaimAmount());
            invoice.setInvoiceAmount(newAmount);
        }

            invoice.setInvoiceStatusEnum(InvoiceStatusEnum.UNPAID);

        invoiceRepository.save(invoice);
        medishieldClaimRepository.delete(claim);

    }


    public Invoice createMedishieldClaim(Long invoiceId, BigDecimal insuranceClaimAmount) {
        Invoice i = invoiceRepository.findById(invoiceId).get();

        if (insuranceClaimAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ClaimErrorException("Insurance claim amount must be greater than zero.");
        }

        if (insuranceClaimAmount.compareTo(i.getInvoiceAmount()) > 0) {
            throw new ClaimErrorException("Medishield claim cannot exceed Invoice Amount! ");
        }

        MedishieldClaim claim = new MedishieldClaim(LocalDateTime.now(),insuranceClaimAmount, ApprovalStatusEnum.PENDING);
        medishieldClaimRepository.save(claim);

        i.setMedishieldClaim(claim);
        i.setInvoiceStatusEnum(InvoiceStatusEnum.CLAIMS_IN_PROCESS);
        invoiceRepository.save(i);
        return i;
    }

    public List<InventoryItemProfitDTO> findProfitByInventoryItem() {
        List<Invoice> invoiceList = invoiceRepository.findInvoiceByInvoiceStatusEnum(InvoiceStatusEnum.PAID);

        // Group invoices by InventoryItem and sum the profits, ignoring null InventoryItems
        Map<String, BigDecimal> profitByInventoryItem = invoiceList.stream()
                .flatMap(invoice -> invoice.getListOfTransactionItem().stream())
                .filter(transactionItem -> transactionItem.getInventoryItem() != null)
                .collect(Collectors.groupingBy(transactionItem -> transactionItem.getInventoryItem().getInventoryItemName(),
                        Collectors.mapping(transactionItem -> transactionItem.getTransactionItemPrice()
                                        .multiply(BigDecimal.valueOf(transactionItem.getTransactionItemQuantity())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // Create a list of InventoryItemProfitDTO objects
        List<InventoryItemProfitDTO> inventoryItemProfits = profitByInventoryItem.entrySet().stream()
                .map(entry -> new InventoryItemProfitDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Sort the list by totalProfit in descending order
        inventoryItemProfits.sort(Comparator.comparing(InventoryItemProfitDTO::getTotalProfit).reversed());

        return inventoryItemProfits;
    }

    public List<InventoryItemProfitDTO> findProfitByServiceItem() {
        List<Invoice> invoiceList = invoiceRepository.findInvoiceByInvoiceStatusEnum(InvoiceStatusEnum.PAID);

        // Group invoices by ServiceItem and sum the profits, ignoring null ServiceItems
        Map<String, BigDecimal> profitByServiceItem = invoiceList.stream()
                .flatMap(invoice -> invoice.getListOfTransactionItem().stream())
                .filter(transactionItem -> transactionItem.getInventoryItem() instanceof ServiceItem)
                .collect(Collectors.groupingBy(
                        transactionItem -> transactionItem.getInventoryItem().getInventoryItemName(),
                        Collectors.mapping(transactionItem ->
                                        transactionItem.getTransactionItemPrice()
                                                .multiply(BigDecimal.valueOf(transactionItem.getTransactionItemQuantity())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // Create a list of InventoryItemProfitDTO objects for ServiceItems
        List<InventoryItemProfitDTO> serviceItemProfits = profitByServiceItem.entrySet().stream()
                .map(entry -> new InventoryItemProfitDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Sort the list by totalProfit in descending order
        serviceItemProfits.sort(Comparator.comparing(InventoryItemProfitDTO::getTotalProfit).reversed());

        return serviceItemProfits;
    }

    public List<InventoryItemProfitDTO> findProfitByMedication() {
        List<Invoice> invoiceList = invoiceRepository.findInvoiceByInvoiceStatusEnum(InvoiceStatusEnum.PAID);

        // Group invoices by Medication and sum the profits, ignoring null Medications
        Map<String, BigDecimal> profitByMedication = invoiceList.stream()
                .flatMap(invoice -> invoice.getListOfTransactionItem().stream())
                .filter(transactionItem -> transactionItem.getInventoryItem() instanceof Medication)
                .collect(Collectors.groupingBy(
                        transactionItem -> transactionItem.getInventoryItem().getInventoryItemName(),
                        Collectors.mapping(transactionItem ->
                                        transactionItem.getTransactionItemPrice()
                                                .multiply(BigDecimal.valueOf(transactionItem.getTransactionItemQuantity())),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        // Create a list of InventoryItemProfitDTO objects for Medications
        List<InventoryItemProfitDTO> medicationProfits = profitByMedication.entrySet().stream()
                .map(entry -> new InventoryItemProfitDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        // Sort the list by totalProfit in descending order
        medicationProfits.sort(Comparator.comparing(InventoryItemProfitDTO::getTotalProfit).reversed());

        return medicationProfits;
    }
}
