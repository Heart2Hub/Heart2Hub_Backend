package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsumableEquipmentRepository extends JpaRepository<ConsumableEquipment, Long> {
    List<ConsumableEquipment> findByInventoryItemNameContainingIgnoreCase(String name);
}
