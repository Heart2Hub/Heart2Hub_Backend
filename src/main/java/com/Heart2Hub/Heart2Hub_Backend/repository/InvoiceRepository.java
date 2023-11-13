package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.Invoice;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedishieldClaim;
import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.Transaction;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.InvoiceStatusEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findInvoiceByPatient(Patient p);

    List<Invoice> findInvoiceByInvoiceStatusEnum(@NotNull InvoiceStatusEnum invoiceStatusEnum);
    Invoice findInvoiceByMedishieldClaim(MedishieldClaim m);

    Invoice findInvoiceByTransaction(Transaction transaction);

}
