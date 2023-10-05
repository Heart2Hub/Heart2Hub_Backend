package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.TransactionItem;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.TransactionItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionItemService {

    private final TransactionItemRepository transactionItemRepository;
    private final PatientService patientService;
    private final PatientRepository patientRepository;


    public TransactionItemService(TransactionItemRepository transactionItemRepository, PatientService patientService, PatientRepository patientRepository) {
        this.transactionItemRepository = transactionItemRepository;
        this.patientService = patientService;
        this.patientRepository = patientRepository;
    }

    public List<TransactionItem> getCartItemsForPatient(Long patientId) {
        Patient p = patientRepository.findById(patientId).get();
        return p.getListOfTransactionItem();
    }

    public TransactionItem addToCart(Long patientId, TransactionItem transactionItem) {
        Patient p = patientRepository.findById(patientId).get();
        TransactionItem addedItem = transactionItemRepository.save(transactionItem);
        p.getListOfTransactionItem().add(addedItem);
        patientRepository.save(p);

        return addedItem;
    }


    public void removeFromCart(Long patientId, Long transactionItemId) {
        Patient p = patientRepository.findById(patientId).get();
        TransactionItem transactionItem = transactionItemRepository.findById(transactionItemId).get();
        p.getListOfTransactionItem().remove(transactionItem);
        transactionItemRepository.deleteById(transactionItemId);

        patientRepository.save(p);
    }


    public void clearCart(Long patientId) {
        Patient p = patientRepository.findById(patientId).get();
        transactionItemRepository.deleteAll(p.getListOfTransactionItem());

        p.getListOfTransactionItem().clear();
        patientRepository.save(p);
    }

    //Checkout Method needed
}

