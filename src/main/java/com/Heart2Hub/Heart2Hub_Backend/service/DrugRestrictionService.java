package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.DrugRestriction;
import com.Heart2Hub.Heart2Hub_Backend.entity.Medication;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicationException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DrugRestrictionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class DrugRestrictionService {

    private final DrugRestrictionRepository drugRestrictionRepository;

    public DrugRestrictionService(DrugRestrictionRepository drugRestrictionRepository) {
        this.drugRestrictionRepository = drugRestrictionRepository;
    }

    public DrugRestriction createDrugRestriction(DrugRestriction newDrugRestriction) throws UnableToCreateMedicationException {
        try {
            String name = newDrugRestriction.getDrugName();
            if (name.trim().equals("")) {
                throw new UnableToCreateMedicationException("Name must be present.");
            }
                drugRestrictionRepository.save(newDrugRestriction);
                return newDrugRestriction;
        } catch (Exception ex) {
            throw new UnableToCreateMedicationException(ex.getMessage());
        }
    }

}
