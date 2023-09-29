package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.AllocatedInventory;
import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.exception.InsufficientLeaveBalanceException;
import com.Heart2Hub.Heart2Hub_Backend.repository.AllocatedInventoryRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ConsumableEquipmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public AllocatedInventory createAllocatedInventory(Long facilityId, Long equipmentid, Integer quantity, Integer minQuantity) {
        AllocatedInventory item = new AllocatedInventory(quantity, minQuantity);
        ConsumableEquipment equipment = consumableEquipmentRepository.findById(equipmentid).get();
        Facility facility = facilityRepository.findById(facilityId).get();
        item.setFacility(facility);
        item.setConsumableEquipment(equipment);

        if (equipment.getQuantityInStock() < quantity) {
            throw new InsufficientLeaveBalanceException("Not enough quantity");
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

    public AllocatedInventory updateAllocatedInventory(long inventoryId, Integer newQuantity) {
        AllocatedInventory inventory = allocatedInventoryRepository.findById(inventoryId).get();
        ConsumableEquipment item = consumableEquipmentRepository.findById(inventory.getConsumableEquipment().getInventoryItemId()).get();
        if (item.getQuantityInStock() + inventory.getAllocatedInventoryCurrentQuantity() < newQuantity) {
            throw new InsufficientLeaveBalanceException("Not enough quantity");
        }

        if (inventory.getMinimumQuantityBeforeRestock() > newQuantity) {
            throw new InsufficientLeaveBalanceException("Quantity less than Minimum Quntity requried");
        }

        item.setQuantityInStock(item.getQuantityInStock() + inventory.getAllocatedInventoryCurrentQuantity() - newQuantity);
        //Transaction item next time
        inventory.setAllocatedInventoryCurrentQuantity(newQuantity);
        consumableEquipmentRepository.save(item);
        allocatedInventoryRepository.save(inventory);
        return inventory;
    }

    public void deleteAllocatedInventory(long inventoryId) {
        AllocatedInventory deleteInventory = updateAllocatedInventory(inventoryId, 0);
        deleteInventory.setConsumableEquipment(null);
        allocatedInventoryRepository.delete(deleteInventory);
    }
}
