package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.MedicationNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicationException;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class MedicationService {
    private final StaffRepository staffRepository;
    private final MedicationRepository medicationRepository;

    public MedicationService(StaffRepository staffRepository, MedicationRepository medicationRepository) {
        this.staffRepository = staffRepository;
        this.medicationRepository = medicationRepository;
    }

    public boolean isLoggedInUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
            if (currStaff.isPresent()) {
                StaffRoleEnum role = currStaff.get().getStaffRoleEnum();
                if (role == StaffRoleEnum.ADMIN) {
                    isAdmin = true;
                }
            }
        }
        return isAdmin;
    }

    public Medication createMedication(Medication newMedication) throws UnableToCreateMedicationException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateMedicationException("Staff cannot create medication as he/she is not an admin.");
        }
        try {
            String name = newMedication.getInventoryItemName();
            if (name.trim().equals("")) {
                throw new UnableToCreateMedicationException("Name must be present.");
            }
            String description = newMedication.getInventoryItemDescription();
            if (description.trim().equals("")) {
                throw new UnableToCreateMedicationException("Description must be present.");
            }
            ItemTypeEnum itemTypeEnum = newMedication.getItemTypeEnum();
            if (itemTypeEnum == null) {
                throw new UnableToCreateMedicationException("Item Type must be present");
            }
            Integer quantity = newMedication.getQuantityInStock();
            if (quantity < 1) {
                throw new UnableToCreateMedicationException("Quantity in stock must be more than 0");
            }
            BigDecimal restockPrice = newMedication.getRestockPricePerQuantity();
            if (restockPrice.equals(BigDecimal.ZERO)) {
                throw new UnableToCreateMedicationException("Price must be more than 0.00");
            }
            BigDecimal retailPrice = newMedication.getRetailPricePerQuantity();
            if (retailPrice.equals(BigDecimal.ZERO)) {
                throw new UnableToCreateMedicationException("Price must be more than 0.00");
            }
            medicationRepository.save(newMedication);
            return newMedication;
        } catch (Exception ex) {
            throw new UnableToCreateMedicationException(ex.getMessage());
        }
    }

    public String deleteMedication(Long inventoryItemId) throws MedicationNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateMedicationException("Staff cannot delete inventory as he/she is not an admin.");
        }
        try {
            Optional<Medication> newMedicationOptional = medicationRepository.findById(inventoryItemId);
            if (newMedicationOptional.isPresent()) {
                Medication medication = newMedicationOptional.get();
                medicationRepository.delete(medication);
                return "Consumable Equipment with inventoryItemId  " + inventoryItemId + " has been deleted successfully.";
            } else {
                throw new MedicationNotFoundException("Medication with ID: " + inventoryItemId + " is not found");
            }
        } catch (Exception ex) {
            throw new MedicationNotFoundException(ex.getMessage());
        }
    }

    public Medication updateMedication(Long inventoryItemId, Medication updatedMedication) throws MedicationNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateMedicationException("Staff cannot update medication as he/she is not an Admin.");
        }
        try {
            Optional<Medication> newMedicationOptional = medicationRepository.findById(inventoryItemId);
            if (newMedicationOptional.isPresent()) {
                Medication medication = newMedicationOptional.get();
                String name = medication.getInventoryItemName();
                if (name.trim().equals("")) {
                    throw new UnableToCreateMedicationException("Name must be present.");
                }
                String description = medication.getInventoryItemDescription();
                if (description.trim().equals("")) {
                    throw new UnableToCreateMedicationException("Description must be present.");
                }
                ItemTypeEnum itemTypeEnum = medication.getItemTypeEnum();
                if (itemTypeEnum == null) {
                    throw new UnableToCreateMedicationException("Item Type must be present");
                }
                Integer quantity = medication.getQuantityInStock();
                if (updatedMedication.getQuantityInStock() < 0) {
                    throw new UnableToCreateMedicationException("Quantity in stock cannot be less than 0");
                }
                BigDecimal price = medication.getRestockPricePerQuantity();
                if (price.equals(BigDecimal.ZERO)) {
                    throw new UnableToCreateMedicationException("Price must be more than 0.00");
                }
                BigDecimal retailPrice = medication.getRetailPricePerQuantity();
                if (retailPrice.equals(BigDecimal.ZERO)) {
                    throw new UnableToCreateMedicationException("Price must be more than 0.00");
                }
                if (updatedMedication.getInventoryItemName() != null) medication.setInventoryItemName(updatedMedication.getInventoryItemName());
                if (updatedMedication.getInventoryItemDescription() != null) medication.setInventoryItemDescription(updatedMedication.getInventoryItemDescription());
                if (updatedMedication.getItemTypeEnum() != null) medication.setItemTypeEnum(updatedMedication.getItemTypeEnum());
                if (updatedMedication.getQuantityInStock() != null) medication.setQuantityInStock(updatedMedication.getQuantityInStock());
                if (updatedMedication.getRestockPricePerQuantity() != null) medication.setRestockPricePerQuantity(updatedMedication.getRestockPricePerQuantity());
                if (updatedMedication.getRetailPricePerQuantity() != null) medication.setRetailPricePerQuantity(updatedMedication.getRetailPricePerQuantity());
                medicationRepository.save(medication);
                return medication;
            } else {
                throw new MedicationNotFoundException("Medication with ID: " + inventoryItemId + " is not found");
            }
        } catch (Exception ex) {
            throw new MedicationNotFoundException(ex.getMessage());
        }
    }

    public List<Medication> getAllMedicationByName(String name) throws MedicationNotFoundException {
        try {
            List<Medication> medicationList = medicationRepository.findByInventoryItemNameContainingIgnoreCase(name);
            System.out.print("get equipment");
            return medicationList;
        } catch (Exception ex) {
            throw new MedicationNotFoundException(ex.getMessage());
        }
    }
}
