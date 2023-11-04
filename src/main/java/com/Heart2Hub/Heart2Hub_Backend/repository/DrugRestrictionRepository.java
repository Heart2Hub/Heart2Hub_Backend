package com.Heart2Hub.Heart2Hub_Backend.repository;

import com.Heart2Hub.Heart2Hub_Backend.entity.DrugRestriction;
import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugRestrictionRepository extends JpaRepository<DrugRestriction, Long> {
    DrugRestriction findByDrugNameContainingIgnoreCase(String name);

}
