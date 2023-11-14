package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Invoice;
import com.Heart2Hub.Heart2Hub_Backend.entity.Transaction;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.InvoiceRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;

    private final PatientRepository patientRepository;
    private final InvoiceRepository invoiceRepository;

    public TransactionService(TransactionRepository transactionRepository, PatientRepository patientRepository, InvoiceRepository invoiceRepository) {
        this.transactionRepository = transactionRepository;
        this.patientRepository = patientRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public Transaction findTransactionWithInvoice(Long invoiceId) {
        return invoiceRepository.findById(invoiceId).get().getTransaction();
    }

    public List<Transaction> viewAllTransactionOfPatient(Long patientId) {
        List<Invoice> listOfInvoices = patientRepository.findById(patientId)
                .get().getListOfInvoices();

        List<Transaction> transactionList = new ArrayList<>();

        for (int i = 0; i < listOfInvoices.size(); i++) {
            if (listOfInvoices.get(i) != null) {
                transactionList.add(listOfInvoices.get(i).getTransaction());
            }
        }
        return transactionList;
    }

    public List<Transaction> viewAllTransactionOfPatientMobile(String username) {
        List<Invoice> listOfInvoices = patientRepository.findByUsername(username)
                .get().getListOfInvoices();

        List<Transaction> transactionList = new ArrayList<>();

        for (int i = 0; i < listOfInvoices.size(); i++) {
            if (listOfInvoices.get(i) != null) {
                transactionList.add(listOfInvoices.get(i).getTransaction());
            }
        }

        Collections.sort(transactionList, new Comparator<Transaction>() {
            public int compare(Transaction t1, Transaction t2) {
                return t2.getTransactionDate().compareTo(t1.getTransactionDate());
            }
        });

        return transactionList;
    }

    public List<Transaction> viewAllTransactionOfPatientMobileEarliest(String username) {
        List<Invoice> listOfInvoices = patientRepository.findByUsername(username)
                .get().getListOfInvoices();

        List<Transaction> transactionList = new ArrayList<>();

        for (int i = 0; i < listOfInvoices.size(); i++) {
            if (listOfInvoices.get(i) != null) {
                transactionList.add(listOfInvoices.get(i).getTransaction());
            }
        }

        // Sort transactions by the earliest date first
        Collections.sort(transactionList, new Comparator<Transaction>() {
            public int compare(Transaction t1, Transaction t2) {
                return t1.getTransactionDate().compareTo(t2.getTransactionDate());
            }
        });

        return transactionList;
    }

    public List<Transaction> viewAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction createTransaction(Long invoiceId, BigDecimal transactionAmount, ApprovalStatusEnum approvalStatusEnum) {
        Transaction transaction = new Transaction(transactionAmount, approvalStatusEnum);
        Invoice invoice = invoiceRepository.findById(invoiceId).get();

        LocalDate currentDate = LocalDate.now();
        LocalDateTime todayNoon = LocalDateTime.of(currentDate, LocalTime.NOON);
        LocalDateTime localDateTime = todayNoon.plusNanos(10000);

        transaction.setTransactionDate(localDateTime);

        if (invoice.getTransaction() != null) {
            // Existing transaction found, handle rejected transaction
            Transaction rejectedTransaction = transactionRepository.findById(invoice.getTransaction().getTransactionId()).get();

            if (approvalStatusEnum == ApprovalStatusEnum.APPROVED) {
                // Create a new transaction for the approved status
                rejectedTransaction.setApprovalStatusEnum(ApprovalStatusEnum.APPROVED);
                //transactionRepository.save(approvedTransaction);
                invoice.setTransaction(rejectedTransaction);
            }

        } else {
            // No existing transaction, create a new one
            transactionRepository.save(transaction);
            invoice.setTransaction(transaction);
        }

        invoice.setInvoiceStatusEnum(InvoiceStatusEnum.PAID);
        invoiceRepository.save(invoice);

        return transaction;
    }

    public Transaction createFailedTransaction(Long invoiceId, BigDecimal transactionAmount, ApprovalStatusEnum approvalStatusEnum) {
        Transaction transaction = new Transaction(transactionAmount, approvalStatusEnum);

        LocalDate currentDate = LocalDate.now();
        LocalDateTime todayNoon = LocalDateTime.of(currentDate, LocalTime.NOON);
        LocalDateTime localDateTime = todayNoon.plusNanos(10000);

        transaction.setTransactionDate(localDateTime);

        Invoice invoice = invoiceRepository.findById(invoiceId).get();

        transactionRepository.save(transaction);
        invoice.setTransaction(transaction);
        invoiceRepository.save(invoice);
        return transaction;
    }

    public Transaction createTransactionDataLoaderMonths(Long invoiceId, BigDecimal transactionAmount, ApprovalStatusEnum approvalStatusEnum, int month) {
        Transaction transaction = new Transaction(transactionAmount, approvalStatusEnum);
        LocalDateTime firstLocalDateTime = LocalDateTime.of(2023, month, 10, 12, 0, 0, 0);
        LocalDateTime localDateTime = firstLocalDateTime.plusNanos(10000);
        transaction.setTransactionDate(localDateTime);

        Invoice invoice = invoiceRepository.findById(invoiceId).get();

        transactionRepository.save(transaction);
        invoice.setTransaction(transaction);
        invoice.setInvoiceStatusEnum(InvoiceStatusEnum.PAID);
        invoiceRepository.save(invoice);
        return transaction;
    }

    public List<Transaction> getAllTransactionsForCurrentYear() {
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfYear = LocalDateTime.now().withDayOfYear(1).plusYears(1).withHour(0).withMinute(0).withSecond(0);

        return transactionRepository.findByTransactionDateBetween(startOfYear, endOfYear);
    }

    public List<BigDecimal> getTotalSumOfTransactionsForCurrentYearByMonth() {
        List<Transaction> transactions = getAllTransactionsForCurrentYear();

        Map<Integer, BigDecimal> monthlySumMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate().getMonthValue(),
                        Collectors.mapping(Transaction::getTransactionAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        List<BigDecimal> monthlySums = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            BigDecimal sum = monthlySumMap.getOrDefault(i, BigDecimal.ZERO);
            monthlySums.add(sum);
        }
        return monthlySums;
    }
}
