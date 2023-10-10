package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.AllocatedInventory;
import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllocatedInventoryRepository extends JpaRepository<AllocatedInventory, Long> {
    List<AllocatedInventory> findAllocatedInventoriesByFacility(Facility facility);

    List<AllocatedInventory> findAllocatedInventoriesByConsumableEquipment(ConsumableEquipment consumableEquipment);
}
