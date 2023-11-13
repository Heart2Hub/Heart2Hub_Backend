package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Transaction;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
//import com.Heart2Hub.Heart2Hub_Backend.service.JasperReportService;
import com.Heart2Hub.Heart2Hub_Backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
//import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
//    private final JasperReportService jasperReportService;
//
//    @GetMapping("/report/{format}")
//    public ResponseEntity<String> generateReport(@PathVariable String format) throws JRException, FileNotFoundException {
//        return ResponseEntity.ok(jasperReportService.exportReport(format));
//    }

    @GetMapping("/getTotalSumOfTransactionsForCurrentYearByMonth/")
    public ResponseEntity<List<BigDecimal>> getTotalSumOfTransactionsForCurrentYearByMonth() {
        return ResponseEntity.ok(transactionService.getTotalSumOfTransactionsForCurrentYearByMonth());
    }

    @GetMapping("/findTransactionWithInvoice/{invoiceId}")
    public ResponseEntity<Transaction> findTransactionWithInvoice(@PathVariable Long invoiceId) {
        Transaction transactions = transactionService.findTransactionWithInvoice(invoiceId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/getAllTransactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.viewAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/getAllTransactionsOfPatient/{patientId}")
    public ResponseEntity<List<Transaction>> getAllTransactionsOfPatient(@PathVariable Long patientId) {
        List<Transaction> transactions = transactionService.viewAllTransactionOfPatient(patientId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/getAllTransactionsOfPatientMobileEarliest/{username}")
    public ResponseEntity<List<Transaction>> getAllTransactionsOfPatientMobileEarliest(@PathVariable String username) {
        List<Transaction> transactions = transactionService.viewAllTransactionOfPatientMobileEarliest(username);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/getAllTransactionsOfPatientMobile/{username}")
    public ResponseEntity<List<Transaction>> getAllTransactionsOfPatientMobile(@PathVariable String username) {
        List<Transaction> transactions = transactionService.viewAllTransactionOfPatientMobile(username);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/createTransaction/{invoiceId}/{amount}")
    public ResponseEntity<Transaction> createTransaction(@PathVariable Long invoiceId,
                                                         @PathVariable Double amount) {
        BigDecimal transactionAmount = BigDecimal.valueOf(amount);
        Transaction transaction = transactionService.createTransaction(invoiceId,
                transactionAmount, ApprovalStatusEnum.APPROVED);
        return ResponseEntity.ok(transaction);
    }
}
