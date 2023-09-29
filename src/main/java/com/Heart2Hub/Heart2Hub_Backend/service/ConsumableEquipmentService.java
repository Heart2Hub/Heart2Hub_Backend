package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.ConsumableEquipmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.FacilityNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateConsumableEquipmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ConsumableEquipmentRepository;
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
public class ConsumableEquipmentService {

    private final StaffRepository staffRepository;
    private final ConsumableEquipmentRepository consumableEquipmentRepository;

    public ConsumableEquipmentService(StaffRepository staffRepository, ConsumableEquipmentRepository consumableEquipmentRepository) {
        this.staffRepository = staffRepository;
        this.consumableEquipmentRepository = consumableEquipmentRepository;
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

    public ConsumableEquipment createConsumableEquipment(ConsumableEquipment newConsumableEquipment) throws UnableToCreateConsumableEquipmentException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateConsumableEquipmentException("Staff cannot create consumable equipment as he/she is not an admin.");
        }
        try {
            String name = newConsumableEquipment.getInventoryItemName();
            if (name.trim().equals("")) {
                throw new UnableToCreateConsumableEquipmentException("Name must be present.");
            }
            String description = newConsumableEquipment.getInventoryItemDescription();
            if (description.trim().equals("")) {
                throw new UnableToCreateConsumableEquipmentException("Description must be present.");
            }
            ItemTypeEnum itemTypeEnum = newConsumableEquipment.getItemTypeEnum();
            if (itemTypeEnum == null) {
                throw new UnableToCreateConsumableEquipmentException("Item Type must be present");
            }
            Integer quantity = newConsumableEquipment.getQuantityInStock();
            if (quantity < 1) {
                throw new UnableToCreateConsumableEquipmentException("Quantity in stock must be more than 0");
            }
            BigDecimal price = newConsumableEquipment.getRestockPricePerQuantity();
            if (price.equals(BigDecimal.ZERO)) {
                throw new UnableToCreateConsumableEquipmentException("Price must be more than 0.00");
            }
            consumableEquipmentRepository.save(newConsumableEquipment);
            return newConsumableEquipment;
        } catch (Exception ex) {
            throw new UnableToCreateConsumableEquipmentException(ex.getMessage());
        }
    }

    public String deleteConsumableEquipment(Long inventoryItemId) throws ConsumableEquipmentNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateConsumableEquipmentException("Staff cannot delete inventory as he/she is not an admin.");
        }
        try {
            Optional<ConsumableEquipment> consumableEquipmentOptional = consumableEquipmentRepository.findById(inventoryItemId);
            if (consumableEquipmentOptional.isPresent()) {
                ConsumableEquipment consumableEquipment = consumableEquipmentOptional.get();
                consumableEquipmentRepository.delete(consumableEquipment);
                return "Consumable Equipment with consumableEquipmentId " + inventoryItemId + " has been deleted successfully.";
            } else {
                throw new ConsumableEquipmentNotFoundException("Consumable Equipment with ID: " + inventoryItemId + " is not found");
            }
        } catch (Exception ex) {
            throw new ConsumableEquipmentNotFoundException(ex.getMessage());
        }
    }

    public ConsumableEquipment updateConsumableEquipment(Long inventoryItemId, ConsumableEquipment updatedConsumableEquipment) throws ConsumableEquipmentNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateConsumableEquipmentException("Staff cannot update consumable equipment as he/she is not an Admin.");
        }
        try {
            Optional<ConsumableEquipment> consumableEquipmentOptional = consumableEquipmentRepository.findById(inventoryItemId);
            if (consumableEquipmentOptional.isPresent()) {
                ConsumableEquipment consumableEquipment = consumableEquipmentOptional.get();
                String name = consumableEquipment.getInventoryItemName();
                if (name.trim().equals("")) {
                    throw new UnableToCreateConsumableEquipmentException("Name must be present.");
                }
                String description = consumableEquipment.getInventoryItemDescription();
                if (description.trim().equals("")) {
                    throw new UnableToCreateConsumableEquipmentException("Description must be present.");
                }
                ItemTypeEnum itemTypeEnum = consumableEquipment.getItemTypeEnum();
                if (itemTypeEnum == null) {
                    throw new UnableToCreateConsumableEquipmentException("Item Type must be present");
                }
                Integer quantity = consumableEquipment.getQuantityInStock();
                if (quantity < 1) {
                    throw new UnableToCreateConsumableEquipmentException("Quantity in stock must be more than 0 " + quantity);
                }
                BigDecimal price = consumableEquipment.getRestockPricePerQuantity();
                if (price.equals(BigDecimal.ZERO)) {
                    throw new UnableToCreateConsumableEquipmentException("Price must be more than 0.00");
                }
                if (updatedConsumableEquipment.getInventoryItemName() != null) consumableEquipment.setInventoryItemName(updatedConsumableEquipment.getInventoryItemName());
                if (updatedConsumableEquipment.getInventoryItemDescription() != null) consumableEquipment.setInventoryItemDescription(updatedConsumableEquipment.getInventoryItemDescription());
                if (updatedConsumableEquipment.getItemTypeEnum() != null) consumableEquipment.setItemTypeEnum(updatedConsumableEquipment.getItemTypeEnum());
                if (updatedConsumableEquipment.getQuantityInStock() != null) consumableEquipment.setQuantityInStock(updatedConsumableEquipment.getQuantityInStock());
                if (updatedConsumableEquipment.getRestockPricePerQuantity() != null) consumableEquipment.setRestockPricePerQuantity(updatedConsumableEquipment.getRestockPricePerQuantity());
              consumableEquipmentRepository.save(consumableEquipment);
                return consumableEquipment;
            } else {
                throw new ConsumableEquipmentNotFoundException("Consumable Equipment with ID: " + inventoryItemId + " is not found");
            }
        } catch (Exception ex) {
            throw new ConsumableEquipmentNotFoundException(ex.getMessage());
        }
    }

    public List<ConsumableEquipment> getAllConsumableEquipmentByName(String name) throws ConsumableEquipmentNotFoundException {
        try {
            List<ConsumableEquipment> consumableEquipmentList = consumableEquipmentRepository.findByInventoryItemNameContainingIgnoreCase(name);
            System.out.print("get equipment");
            return consumableEquipmentList;
        } catch (Exception ex) {
            throw new ConsumableEquipmentNotFoundException(ex.getMessage());
        }
    }
}

