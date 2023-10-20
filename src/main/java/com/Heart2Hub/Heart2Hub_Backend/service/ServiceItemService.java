package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import com.Heart2Hub.Heart2Hub_Backend.entity.ServiceItem;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.entity.Unit;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.MedicationNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.ServiceItemNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicationException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateServiceItemException;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ServiceItemRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.UnitRepository;
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
public class ServiceItemService {
    private final StaffRepository staffRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final UnitRepository unitRepository;


    public ServiceItemService(StaffRepository staffRepository, ServiceItemRepository serviceItemRepository, UnitRepository unitRepository) {
        this.staffRepository = staffRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.unitRepository = unitRepository;
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

    public ServiceItem createServiceItem(Long unitId, ServiceItem newServiceItem) throws UnableToCreateServiceItemException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateServiceItemException("Staff cannot create medication as he/she is not an admin.");
        }
        try {
            String name = newServiceItem.getInventoryItemName();
            if (name.trim().equals("")) {
                throw new UnableToCreateServiceItemException("Name must be present.");
            }
            String description = newServiceItem.getInventoryItemDescription();
            if (description.trim().equals("")) {
                throw new UnableToCreateServiceItemException("Description must be present.");
            }
            ItemTypeEnum itemTypeEnum = newServiceItem.getItemTypeEnum();
            if (itemTypeEnum == null) {
                throw new UnableToCreateServiceItemException("Item Type must be present");
            }
            BigDecimal retailPrice = newServiceItem.getRetailPricePerQuantity();
            if (retailPrice.equals(BigDecimal.ZERO)) {
                throw new UnableToCreateServiceItemException("Price must be more than 0.00");
            }
            Optional<Unit> unitOptional = unitRepository.findById(unitId);
            if (unitOptional.isPresent()) {
                Unit unit = unitOptional.get();
                newServiceItem.setUnit(unit);
            }
            serviceItemRepository.save(newServiceItem);
            return newServiceItem;
        } catch (Exception ex) {
            throw new UnableToCreateServiceItemException(ex.getMessage());
        }
    }

    public String deleteServiceItem(Long inventoryItemId) throws ServiceItemNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateServiceItemException("Staff cannot delete inventory as he/she is not an admin.");
        }
        try {
            Optional<ServiceItem> serviceItemOptional = serviceItemRepository.findById(inventoryItemId);
            if (serviceItemOptional.isPresent()) {
                ServiceItem serviceItem = serviceItemOptional.get();
                serviceItemRepository.delete(serviceItem);
                return "Service Item with inventoryItemId  " + inventoryItemId + " has been deleted successfully.";
            } else {
                throw new ServiceItemNotFoundException("Service Item with ID: " + inventoryItemId + " is not found");
            }
        } catch (Exception ex) {
            throw new ServiceItemNotFoundException(ex.getMessage());
        }
    }

    public ServiceItem updateServiceItem (Long inventoryItemId, ServiceItem updatedServiceItem) throws ServiceItemNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateServiceItemException("Staff cannot update medication as he/she is not an Admin.");
        }
        try {
            Optional<ServiceItem> serviceItemOptional = serviceItemRepository.findById(inventoryItemId);
            if (serviceItemOptional.isPresent()) {
                ServiceItem serviceItem = serviceItemOptional.get();
                String name = serviceItem.getInventoryItemName();
                if (name.trim().equals("")) {
                    throw new UnableToCreateServiceItemException("Name must be present.");
                }
                String description = serviceItem.getInventoryItemDescription();
                if (description.trim().equals("")) {
                    throw new UnableToCreateServiceItemException("Description must be present.");
                }
                ItemTypeEnum itemTypeEnum = serviceItem.getItemTypeEnum();
                if (itemTypeEnum == null) {
                    throw new UnableToCreateServiceItemException("Item Type must be present");
                }
                BigDecimal retailPrice = serviceItem.getRetailPricePerQuantity();
                if (retailPrice.equals(BigDecimal.ZERO)) {
                    throw new UnableToCreateServiceItemException("Price must be more than 0.00");
                }
                if (updatedServiceItem.getInventoryItemName() != null) serviceItem.setInventoryItemName(updatedServiceItem.getInventoryItemName());
                if (updatedServiceItem.getInventoryItemDescription() != null) serviceItem.setInventoryItemDescription(updatedServiceItem.getInventoryItemDescription());
                if (updatedServiceItem.getItemTypeEnum() != null) serviceItem.setItemTypeEnum(updatedServiceItem.getItemTypeEnum());
                if (updatedServiceItem.getRetailPricePerQuantity() != null) serviceItem.setRetailPricePerQuantity(updatedServiceItem.getRetailPricePerQuantity());
                serviceItemRepository.save(serviceItem);
                return serviceItem;
            } else {
                throw new ServiceItemNotFoundException("Service Item with ID: " + inventoryItemId + " is not found");
            }
        } catch (Exception ex) {
            throw new ServiceItemNotFoundException(ex.getMessage());
        }
    }

    public List<ServiceItem> getAllServiceItem() throws ServiceItemNotFoundException {
        try {
            List<ServiceItem> serviceItemList = serviceItemRepository.findAll();
            System.out.print("get equipment");
            return serviceItemList;
        } catch (Exception ex) {
            throw new ServiceItemNotFoundException(ex.getMessage());
        }
    }
}

