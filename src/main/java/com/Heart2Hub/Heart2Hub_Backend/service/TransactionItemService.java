package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.AppointmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.InventoryItemRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.PatientRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.TransactionItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TransactionItemService {

    private final TransactionItemRepository transactionItemRepository;
    private final PatientService patientService;
    private final PatientRepository patientRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceService invoiceService;

    public TransactionItemService(TransactionItemRepository transactionItemRepository, PatientService patientService, PatientRepository patientRepository, InventoryItemRepository inventoryItemRepository, AppointmentRepository appointmentRepository, InvoiceService invoiceService) {
        this.transactionItemRepository = transactionItemRepository;
        this.patientService = patientService;
        this.patientRepository = patientRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceService = invoiceService;
    }

    public List<TransactionItem> getAllItems() {
        return transactionItemRepository.findAll();
    }

    public List<TransactionItem> getCartItemsForPatient(Long patientId) {
        Patient p = patientRepository.findById(patientId).get();
        List<TransactionItem> item = new ArrayList<>();

        for(int i = 0; i < p.getListOfTransactionItem().size(); i++) {
            item.add(p.getListOfTransactionItem().get(i));
        }

        return item;
    }

    public TransactionItem addToCart(Long patientId, String inventoryItemName, String inventoryItemDescription,
                                     Integer transactionItemQuantity, BigDecimal transactionItemPrice, Long itemId) {
        TransactionItem transactionItem = new TransactionItem(inventoryItemName, inventoryItemDescription,
                transactionItemQuantity, transactionItemPrice, inventoryItemRepository.findById(itemId).get());
        Patient p = patientRepository.findById(patientId).get();
        transactionItemRepository.save(transactionItem);
        p.getListOfTransactionItem().add(transactionItem);
        patientRepository.save(p);

        InventoryItem item = inventoryItemRepository.
                findById(transactionItem.getInventoryItem()
                        .getInventoryItemId()).get();

        if (item instanceof ServiceItem) {
            ServiceItem serviceItem = (ServiceItem) item;
        } else if (item instanceof Medication) {
            Medication medicationItem = (Medication) item;
            medicationItem.setQuantityInStock(
                    medicationItem.getQuantityInStock()
                            - transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(medicationItem);
        } else if (item instanceof ConsumableEquipment) {
            ConsumableEquipment consumableEquipment = (ConsumableEquipment) item;
            consumableEquipment.setQuantityInStock(
                    consumableEquipment.getQuantityInStock()
                    - transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(consumableEquipment);
        }
        return transactionItem;
    }

    public TransactionItem addToCartDataLoader(Long patientId, TransactionItem transactionItem) {

        Patient p = patientRepository.findById(patientId).get();
        TransactionItem addedItem = transactionItemRepository.save(transactionItem);
        p.getListOfTransactionItem().add(addedItem);
        patientRepository.save(p);

        InventoryItem item = inventoryItemRepository.
                findById(addedItem.getInventoryItem()
                        .getInventoryItemId()).get();

        if (item instanceof ServiceItem) {
            ServiceItem serviceItem = (ServiceItem) item;
        } else if (item instanceof Medication) {
            Medication medicationItem = (Medication) item;
            medicationItem.setQuantityInStock(
                    medicationItem.getQuantityInStock()
                            - transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(medicationItem);
        } else if (item instanceof ConsumableEquipment) {
            ConsumableEquipment consumableEquipment = (ConsumableEquipment) item;
            consumableEquipment.setQuantityInStock(
                    consumableEquipment.getQuantityInStock()
                            - transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(consumableEquipment);
        }
        return addedItem;
    }

    public void removeFromCart(Long patientId, Long transactionItemId) {
        Patient p = patientRepository.findById(patientId).get();
        TransactionItem transactionItem = transactionItemRepository.findById(transactionItemId).get();
        p.getListOfTransactionItem().remove(transactionItem);
        transactionItemRepository.deleteById(transactionItemId);

        patientRepository.save(p);

        InventoryItem item = inventoryItemRepository.
                findById(transactionItem.getInventoryItem()
                        .getInventoryItemId()).get();

        if (item instanceof ServiceItem) {
            ServiceItem serviceItem = (ServiceItem) item;
        } else if (item instanceof Medication) {
            Medication medicationItem = (Medication) item;
            medicationItem.setQuantityInStock(
                    medicationItem.getQuantityInStock()
                            + transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(medicationItem);
        } else if (item instanceof ConsumableEquipment) {
            ConsumableEquipment consumableEquipment = (ConsumableEquipment) item;
            consumableEquipment.setQuantityInStock(
                    consumableEquipment.getQuantityInStock()
                            + transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(consumableEquipment);
        }
    }


    public void clearCart(Long patientId) {
        Patient p = patientRepository.findById(patientId).get();
        transactionItemRepository.deleteAll(p.getListOfTransactionItem());

        for (int i = 0; i < p.getListOfTransactionItem().size(); i++) {
            TransactionItem transactionItem = p.getListOfTransactionItem().get(i);

            InventoryItem item = inventoryItemRepository.
                    findById(transactionItem.getInventoryItem()
                            .getInventoryItemId()).get();

            if (item instanceof ServiceItem) {
                ServiceItem serviceItem = (ServiceItem) item;
            } else if (item instanceof Medication) {
                Medication medicationItem = (Medication) item;
                medicationItem.setQuantityInStock(
                        medicationItem.getQuantityInStock()
                                + transactionItem.getTransactionItemQuantity()
                );
                inventoryItemRepository.save(medicationItem);
            } else if (item instanceof ConsumableEquipment) {
                ConsumableEquipment consumableEquipment = (ConsumableEquipment) item;
                consumableEquipment.setQuantityInStock(
                        consumableEquipment.getQuantityInStock()
                                + transactionItem.getTransactionItemQuantity()
                );
                inventoryItemRepository.save(consumableEquipment);
            }
        }

        p.getListOfTransactionItem().clear();
        patientRepository.save(p);
    }

    public Invoice checkout(Long patientId) {
        Patient p = patientRepository.findById(patientId).get();
        List<TransactionItem> listOfItems = p.getListOfTransactionItem();
        List<Subsidy> listOfSubsidy = p.getElectronicHealthRecord().getListOfSubsidies();

        Subsidy subsidyService = null;
        Subsidy subsidyMedication = null;
        Subsidy subsidyConsumable = null;

        for(Subsidy s : listOfSubsidy) {
            if (s.getItemTypeEnum() == ItemTypeEnum.MEDICINE) {
                if (subsidyMedication == null ||
                        s.getSubsidyRate().compareTo(subsidyMedication.getSubsidyRate()) > 0) {
                    subsidyMedication = s;
                }
            } else if (s.getItemTypeEnum() == ItemTypeEnum.CONSUMABLE) {
                if (subsidyConsumable == null ||
                        s.getSubsidyRate().compareTo(subsidyConsumable.getSubsidyRate()) > 0) {
                    subsidyConsumable = s;
                }
            } else {
                if (subsidyService == null ||
                        s.getSubsidyRate().compareTo(subsidyService.getSubsidyRate()) > 0) {
                    subsidyService = s;
                }
            }
        }

        LocalDateTime invoiceDueDate = LocalDateTime.now().plus(30, ChronoUnit.DAYS);
        StringBuilder breakdown = new StringBuilder();
        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;

        for (TransactionItem item : listOfItems) {
            String itemName = item.getTransactionItemName();
            Integer itemQuantity = item.getTransactionItemQuantity();
            BigDecimal itemPrice = item.getTransactionItemPrice();
            BigDecimal totalCost = itemPrice.multiply(BigDecimal.valueOf(itemQuantity));

            totalInvoiceAmount = totalInvoiceAmount.add(totalCost);

            breakdown.append("Item: ").append(itemName).append(", ");
            breakdown.append("Quantity: ").append(itemQuantity).append(", ");
            breakdown.append("Price per unit: $").append(itemPrice).append(", ");
            breakdown.append("Total Cost: $").append(totalCost).append("\n");

            // Apply subsidies based on item type
            if (item.getInventoryItem().getItemTypeEnum() == ItemTypeEnum.MEDICINE && subsidyMedication != null) {
                BigDecimal subsidyRate = subsidyMedication.getSubsidyRate();
                BigDecimal subsidyAmount = totalCost.multiply(subsidyRate);
                totalInvoiceAmount = totalInvoiceAmount.subtract(subsidyAmount);
                breakdown.append("Medication Subsidy: -$").append(subsidyAmount).append("\n");
            } else if (item.getInventoryItem().getItemTypeEnum() == ItemTypeEnum.CONSUMABLE && subsidyConsumable != null) {
                BigDecimal subsidyRate = subsidyConsumable.getSubsidyRate();
                BigDecimal subsidyAmount = totalCost.multiply(subsidyRate);
                totalInvoiceAmount = totalInvoiceAmount.subtract(subsidyAmount);
                breakdown.append("Consumable Subsidy: -$").append(subsidyAmount).append("\n");
            } else if (subsidyService != null) {
                BigDecimal subsidyRate = subsidyService.getSubsidyRate();
                BigDecimal subsidyAmount = totalCost.multiply(subsidyRate);
                totalInvoiceAmount = totalInvoiceAmount.subtract(subsidyAmount);
                breakdown.append("Service Subsidy: -$").append(subsidyAmount).append("\n");
            }
        }
        breakdown.append("Final Invoice Amount: $").append(totalInvoiceAmount).append("\n");
        String costBreakdown = breakdown.toString();

        List<TransactionItem> invoiceItems = new ArrayList<>(listOfItems); // Create a copy of listOfItems

        Invoice invoice = new Invoice(totalInvoiceAmount, invoiceDueDate, p,
                invoiceItems, costBreakdown);
        invoice = invoiceService.createNewInvoice(invoice);

        p.getListOfInvoices().add(invoice);

        patientRepository.save(p);

        return invoice;
    }

    public void dischargePatient(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).get();
        appointment.setSwimlaneStatusEnum(SwimlaneStatusEnum.DONE);
        appointmentRepository.save(appointment);
    }
}

