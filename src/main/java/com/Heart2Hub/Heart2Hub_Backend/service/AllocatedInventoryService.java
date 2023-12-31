package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.AllocatedInventory;
import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.AllocatedInventoryRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ConsumableEquipmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AllocatedInventoryService {

    private final AllocatedInventoryRepository allocatedInventoryRepository;
    private final ConsumableEquipmentRepository consumableEquipmentRepository;

    private final FacilityRepository facilityRepository;

    public AllocatedInventoryService(AllocatedInventoryRepository allocatedInventoryRepository, ConsumableEquipmentRepository consumableEquipmentRepository, FacilityRepository facilityRepository) {
        this.allocatedInventoryRepository = allocatedInventoryRepository;
        this.consumableEquipmentRepository = consumableEquipmentRepository;
        this.facilityRepository = facilityRepository;
    }


    public List<AllocatedInventory> findAllAllocatedInventoryOfFacility(Long id) {
        Facility f = facilityRepository.findById(id).get();
        return allocatedInventoryRepository.findAllocatedInventoriesByFacility(f);
    }

//    public List<AllocatedInventory> findAllAllocatedInventoryByItemId(Long id) {
//        ConsumableEquipment f = facilityRepository.findById(id).get();
//        return allocatedInventoryRepository.findAllocatedInventoriesByFacility(f);
//    }

    public AllocatedInventory createAllocatedInventory(Long facilityId, Long inventoryItemId, Integer quantity, Integer minQuantity) throws UnableToCreateConsumableEquipmentException {
        AllocatedInventory item = new AllocatedInventory(quantity, minQuantity);
        ConsumableEquipment equipment = consumableEquipmentRepository.findById(inventoryItemId).get();
        Facility facility = facilityRepository.findById(facilityId).get();
        item.setFacility(facility);
        item.setConsumableEquipment(equipment);
        List<AllocatedInventory> facilityInventory = facility.getListOfAllocatedInventories();
        for (AllocatedInventory inventory: facilityInventory) {
            if (inventoryItemId == inventory.getConsumableEquipment().getInventoryItemId()) {
                throw new UnableToCreateConsumableEquipmentException("Cannot add item already in allocated inventory");
            }
        }

        if (equipment.getQuantityInStock() < quantity) {
            throw new InsufficientQuantityException("Not enough quantity");
        } else {
            equipment.setQuantityInStock(equipment.getQuantityInStock()-quantity);
            // Transaction item here

        }

        allocatedInventoryRepository.save(item);
        consumableEquipmentRepository.save(equipment);

        Facility f = facilityRepository.findById(facility.getFacilityId()).get();
        f.getListOfAllocatedInventories().add(item);
        facilityRepository.save(f);

        return item;
    }

    public AllocatedInventory updateAllocatedInventory(long inventoryId, Integer newQuantity, Integer minQuantity) {
        AllocatedInventory inventory = allocatedInventoryRepository.findById(inventoryId).get();
        ConsumableEquipment item = consumableEquipmentRepository.findById(inventory.getConsumableEquipment().getInventoryItemId()).get();
        if (newQuantity < 0) {
            throw new InsufficientLeaveBalanceException("Quantity cannot be less than 0");
        }
        if (item.getQuantityInStock() + inventory.getAllocatedInventoryCurrentQuantity() < newQuantity) {
            throw new InsufficientLeaveBalanceException("Not enough quantity");
        }

        if (inventory.getMinimumQuantityBeforeRestock() > newQuantity) {
            throw new InsufficientLeaveBalanceException("Quantity less than Minimum Quantity required");
        }

        item.setQuantityInStock(item.getQuantityInStock() + inventory.getAllocatedInventoryCurrentQuantity() - newQuantity);
        //Transaction item next time
        inventory.setAllocatedInventoryCurrentQuantity(newQuantity);
        inventory.setMinimumQuantityBeforeRestock(minQuantity);
        consumableEquipmentRepository.save(item);
        allocatedInventoryRepository.save(inventory);
        return inventory;
    }

    public AllocatedInventory useAllocatedInventory(long inventoryId, Integer newQuantity) {
        AllocatedInventory inventory = allocatedInventoryRepository.findById(inventoryId).get();
        ConsumableEquipment item = consumableEquipmentRepository.findById(inventory.getConsumableEquipment().getInventoryItemId()).get();
        if (newQuantity < 0) {
            throw new InsufficientLeaveBalanceException("No more quantity, restock needed");
        }
        //Transaction item next time
        inventory.setAllocatedInventoryCurrentQuantity(newQuantity);
        consumableEquipmentRepository.save(item);
        allocatedInventoryRepository.save(inventory);
        return inventory;
    }

    public String deleteAllocatedInventory(long inventoryId) {

        try {
        Optional<AllocatedInventory> allocatedInventoryOptional = allocatedInventoryRepository.findById(inventoryId);

        if (allocatedInventoryOptional.isPresent()) {
            AllocatedInventory allocatedInventory = allocatedInventoryOptional.get();
            ConsumableEquipment item = consumableEquipmentRepository.findById(allocatedInventory.getConsumableEquipment().getInventoryItemId()).get();
            item.setQuantityInStock(item.getQuantityInStock() + allocatedInventory.getAllocatedInventoryCurrentQuantity());

            Facility facility = allocatedInventory.getFacility();
            facility.getListOfAllocatedInventories().remove(allocatedInventory);
//            ConsumableEquipment consumableEquipment = allocatedInventory.getConsumableEquipment();
            allocatedInventory.setConsumableEquipment(null);
            allocatedInventoryRepository.delete(allocatedInventory);
            return "Allocated Inventory with allocatedInventoryId  " + inventoryId + " has been deleted successfully.";
        } else {
            throw new FacilityNotFoundException("Allocated Inventory with allocatedInventoryId: " + inventoryId + " is not found");
        }
        } catch (Exception ex) {
            throw new FacilityNotFoundException(ex.getMessage());
        }
    }
}
