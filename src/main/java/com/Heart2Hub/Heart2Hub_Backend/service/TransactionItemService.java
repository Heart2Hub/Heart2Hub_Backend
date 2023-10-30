package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.AllergenEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.SwimlaneStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.InsufficientInventoryException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToAssignAppointmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
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
    private final InvoiceRepository invoiceRepository;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public TransactionItemService(TransactionItemRepository transactionItemRepository, PatientService patientService, PatientRepository patientRepository, InventoryItemRepository inventoryItemRepository, AppointmentRepository appointmentRepository, InvoiceService invoiceService, InvoiceRepository invoiceRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.transactionItemRepository = transactionItemRepository;
        this.patientService = patientService;
        this.patientRepository = patientRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceService = invoiceService;
        this.invoiceRepository = invoiceRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
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


        //Get the list of all current Meds in Patient's Cart
        List<TransactionItem> patientCart = p.getListOfTransactionItem();
        List<Medication> patientMedicationInCart = new ArrayList<>();
        for (TransactionItem value : patientCart) {
            if (value.getInventoryItem() instanceof Medication) {
                if (value.getInventoryItem().getInventoryItemId() == itemId) {
                    value.setTransactionItemQuantity(value.getTransactionItemQuantity() + transactionItemQuantity);
                    InventoryItem i = inventoryItemRepository.findById(itemId).get();
                    Medication med = (Medication) i;
                    transactionItem = value;
                    if (med.getQuantityInStock() - transactionItem.getTransactionItemQuantity() < 0) {
                        throw new InsufficientInventoryException("Insufficient Inventory for Medication");
                    }
                }
                patientMedicationInCart.add((Medication) value.getInventoryItem());
            }
        }

        InventoryItem item = inventoryItemRepository.
                findById(transactionItem.getInventoryItem()
                        .getInventoryItemId()).get();

        if (item instanceof ServiceItem) {
            ServiceItem serviceItem = (ServiceItem) item;
        } else if (item instanceof Medication) {
            Medication medicationItem = (Medication) item;
            if (medicationItem.getQuantityInStock() - transactionItem.getTransactionItemQuantity() < 0) {
                throw new InsufficientInventoryException("Insufficient Inventory for Medication");
            }

            for (Medication medication : patientMedicationInCart) {
                //Get the current Medication item's drug restrictions
                List<DrugRestriction> drugRestriction = medication.getDrugRestrictions();

                //Loop through that item's drug restrictions
                for (DrugRestriction restriction : drugRestriction) {
                    String drugType = restriction.getDrugName();
                    String checkDrug = medicationItem.getInventoryItemName();
                    if (checkDrug.contains(drugType)) {
                        throw new InsufficientInventoryException("Patient can't be administered this drug due to " + drugType + "'s Drug Restrictions");
                    }
                }
            }

            medicationItem.setQuantityInStock(
                    medicationItem.getQuantityInStock()
                            - transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(medicationItem);
        } else if (item instanceof ConsumableEquipment) {
            ConsumableEquipment consumableEquipment = (ConsumableEquipment) item;
            if (consumableEquipment.getQuantityInStock() - transactionItem.getTransactionItemQuantity() < 0) {
                throw new InsufficientInventoryException("Insufficient Inventory for Consumable");
            }
            consumableEquipment.setQuantityInStock(
                    consumableEquipment.getQuantityInStock()
                    - transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(consumableEquipment);
        }
        transactionItemRepository.save(transactionItem);
        p.getListOfTransactionItem().add(transactionItem);
        patientRepository.save(p);
        return transactionItem;
    }

    public TransactionItem addToCartDataLoader(Long patientId, TransactionItem transactionItem) {

        Patient p = patientRepository.findById(patientId).get();
        TransactionItem addedItem = transactionItemRepository.save(transactionItem);
        p.getListOfTransactionItem().add(addedItem);
        patientRepository.save(p);

//        List<TransactionItem> patientCart = p.getListOfTransactionItem();
//        List<Medication> patientMedicationInCart = new ArrayList<>();
//        for (int i = 0; i < patientCart.size(); i++) {
//            if (patientCart.get(i).getInventoryItem() instanceof Medication) {
//                patientMedicationInCart.add((Medication) patientCart.get(i).getInventoryItem());
//            }
//        }

        InventoryItem item = inventoryItemRepository.
                findById(addedItem.getInventoryItem()
                        .getInventoryItemId()).get();

        if (item instanceof ServiceItem) {
            ServiceItem serviceItem = (ServiceItem) item;
        } else if (item instanceof Medication) {
            Medication medicationItem = (Medication) item;
            if (medicationItem.getQuantityInStock() - transactionItem.getTransactionItemQuantity() < 0) {
                throw new InsufficientInventoryException("Insufficient Inventory for Medication");
            }
//
//            for (int i = 0; i < patientMedicationInCart.size(); i++) {
//                //Current Item
//                Medication cartItem = patientMedicationInCart.get(i);
//                List<DrugRestriction> drugRestriction = patientMedicationInCart.get(i).getDrugRestrictions();
//
//                //Loop through for restrictions
//                for (int j = 0; j < drugRestriction.size(); j++) {
//                    String drugType = drugRestriction.get(j).getDrugName();
//                    String checkDrug = cartItem.getInventoryItemName();
//                    if (checkDrug.contains(drugType)) {
//                        throw new InsufficientInventoryException("Patient can't be administered this drug due to " + checkDrug);
//                    }
//                }
//            }
//
////            for (int i = 0; i < patientMedicationInCart.size(); i++) {
////                List<AllergenEnum> allergenEnumList = (List<AllergenEnum>) patientMedicationInCart.get(i).getAllergenEnumList();
////                for (int j = 0; j < allergenEnumList.size(); j++) {
////                    String allergyType = allergenEnumList.get(j);
////                }
////            }

            medicationItem.setQuantityInStock(
                    medicationItem.getQuantityInStock()
                            - transactionItem.getTransactionItemQuantity()
            );
            inventoryItemRepository.save(medicationItem);
        } else if (item instanceof ConsumableEquipment) {
            ConsumableEquipment consumableEquipment = (ConsumableEquipment) item;
            if (consumableEquipment.getQuantityInStock() - transactionItem.getTransactionItemQuantity() < 0) {
                throw new InsufficientInventoryException("Insufficient Inventory for Medication");
            }
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
        List<TransactionItem> subsidyItemMedicationList = new ArrayList<>();
//        List<TransactionItem> subsidyItemConsumableList = new ArrayList<>();
        List<TransactionItem> subsidyItemServiceList = new ArrayList<>();

        for (TransactionItem item : listOfItems) {
            String itemName = item.getTransactionItemName();
            Integer itemQuantity = item.getTransactionItemQuantity();
            BigDecimal itemPrice = item.getTransactionItemPrice();
            BigDecimal totalCost = itemPrice.multiply(BigDecimal.valueOf(itemQuantity));

            totalInvoiceAmount = totalInvoiceAmount.add(totalCost);

            breakdown.append("Item: ").append(itemName).append(", ");
            breakdown.append("Quantity: ").append(itemQuantity).append(", ");
            breakdown.append("Price per unit: $").append(itemPrice).append(", ");
            breakdown.append("Total Cost for Item: $").append(totalCost).append("\n");

            // Apply subsidies based on item type
            if (item.getInventoryItem().getItemTypeEnum() == ItemTypeEnum.MEDICINE && subsidyMedication != null) {
                BigDecimal subsidyRate = subsidyMedication.getSubsidyRate();
                BigDecimal subsidyAmount = totalCost.multiply(subsidyRate);
                totalInvoiceAmount = totalInvoiceAmount.subtract(subsidyAmount);
                breakdown.append("Medication Subsidy: -$").append(subsidyAmount).append("\n");
                TransactionItem subsidyItemMedication= new TransactionItem(item.getTransactionItemName() + "("+ subsidyMedication.getSubsidyName() + ")"
                        , subsidyMedication.getSubsidyDescription(), 1,
                        subsidyAmount.multiply(BigDecimal.valueOf(-1)), null);
                transactionItemRepository.save(subsidyItemMedication);
                subsidyItemMedicationList.add(subsidyItemMedication);

//                //item.setTransactionItemName(item.getTransactionItemName() + " (Medication Subsidy of $" + subsidyAmount + ")");
//            } else if (item.getInventoryItem().getItemTypeEnum() == ItemTypeEnum.CONSUMABLE && subsidyConsumable != null) {
//                BigDecimal subsidyRate = subsidyConsumable.getSubsidyRate();
//                BigDecimal subsidyAmount = totalCost.multiply(subsidyRate);
//                totalInvoiceAmount = totalInvoiceAmount.subtract(subsidyAmount);
//                breakdown.append("Consumable Subsidy: -$").append(subsidyAmount).append("\n");
//                TransactionItem subsidyItemConsumable = new TransactionItem(subsidyConsumable.getSubsidyName()
//                        , subsidyConsumable.getSubsidyDescription(), 1,
//                        subsidyAmount.multiply(BigDecimal.valueOf(-1)), null);
//                transactionItemRepository.save(subsidyItemConsumable);
//                subsidyItemConsumableList.add(subsidyItemConsumable);
//                //item.setTransactionItemName(item.getTransactionItemName() + " (Consumable Subsidy of $" + subsidyAmount + ")");

            } else if (subsidyService != null) {
                BigDecimal subsidyRate = subsidyService.getSubsidyRate();
                BigDecimal subsidyAmount = totalCost.multiply(subsidyRate);
                totalInvoiceAmount = totalInvoiceAmount.subtract(subsidyAmount);
                breakdown.append("Service Subsidy: -$").append(subsidyAmount).append("\n");
                TransactionItem subsidyItemService = new TransactionItem(item.getTransactionItemName() + "("+ subsidyMedication.getSubsidyName() + ")"
                        , subsidyService.getSubsidyDescription(), 1,
                        subsidyAmount.multiply(BigDecimal.valueOf(-1)), null);
                transactionItemRepository.save(subsidyItemService);
                subsidyItemServiceList.add(subsidyItemService);
                //item.setTransactionItemName(item.getTransactionItemName() + " (Service Subsidy of $" + subsidyAmount + ")");

            }
        }
        breakdown.append("Final Invoice Amount: $").append(totalInvoiceAmount).append("\n");
        String costBreakdown = breakdown.toString();

        listOfItems.addAll(subsidyItemServiceList);
//        listOfItems.addAll(subsidyItemConsumableList);
        listOfItems.addAll(subsidyItemMedicationList);


        List<TransactionItem> invoiceItems = new ArrayList<>(listOfItems); // Create a copy of listOfItems

        Invoice invoice = new Invoice(totalInvoiceAmount, invoiceDueDate, p,
                invoiceItems, costBreakdown);
        invoice = invoiceService.createNewInvoice(invoice);

        p.getListOfInvoices().add(invoice);
        p.getListOfTransactionItem().clear();
        patientRepository.save(p);

        return invoice;
    }

    public void dischargePatient(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).get();
        appointment.setSwimlaneStatusEnum(SwimlaneStatusEnum.DONE);
        if (appointment.getCurrentAssignedStaff() == null) {
            throw new UnableToAssignAppointmentException("Please assign a staff");
        }
        if (!appointment.getArrived()) {
            throw new UnableToAssignAppointmentException("Please check that patient has arrived");
        }

        //ASSOCIATION --> SHOULD REFACTOR TO BE IN APPOINTMENT SERVICE
        appointment.getCurrentAssignedStaff().getListOfAssignedAppointments().remove(appointment);
        appointment.setCurrentAssignedStaff(null);
        Patient patient = appointment.getPatient();
        patient.getListOfCurrentAppointments().remove(appointment);
        patient.getElectronicHealthRecord().getListOfPastAppointments().add(appointment);

        appointmentRepository.save(appointment);
    }

    public TransactionItem updateTransactionItem(Long transactionItemId, Integer transactionItemQuantity) {
        TransactionItem transactionItem = transactionItemRepository.findById(transactionItemId).get();

        InventoryItem item = transactionItem.getInventoryItem();
        int oldQuantity = transactionItem.getTransactionItemQuantity();
        Medication med = (Medication) item;
        if (transactionItemQuantity > oldQuantity) {
            if (med.getQuantityInStock() - (transactionItemQuantity - oldQuantity) < 0) {
                throw new InsufficientInventoryException("Insufficient Inventory for Medication");
            }
            med.setQuantityInStock(med.getQuantityInStock() - (transactionItemQuantity - oldQuantity));
        } else if (transactionItemQuantity < oldQuantity){
            med.setQuantityInStock(med.getQuantityInStock() + (oldQuantity - transactionItemQuantity));
        }
        transactionItem.setTransactionItemQuantity(transactionItemQuantity);
        return transactionItemRepository.save(transactionItem);
    }
}

