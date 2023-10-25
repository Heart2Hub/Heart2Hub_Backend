package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.AllergenEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ProblemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.MedicationNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicationException;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class MedicationService {
    private final StaffRepository staffRepository;
    private final MedicationRepository medicationRepository;
    private final DrugRestrictionRepository drugRestrictionRepository;
    private final PatientRepository patientRepository;

    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;

    public MedicationService(StaffRepository staffRepository, MedicationRepository medicationRepository, DrugRestrictionRepository drugRestrictionRepository,
                             PatientRepository patientRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository) {
        this.staffRepository = staffRepository;
        this.medicationRepository = medicationRepository;
        this.drugRestrictionRepository = drugRestrictionRepository;

        this.patientRepository = patientRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
    }

    public boolean isLoggedInPharmacist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isPharmacist = false;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
            if (currStaff.isPresent()) {
                StaffRoleEnum role = currStaff.get().getStaffRoleEnum();
                if (role == StaffRoleEnum.PHARMACIST) {
                    isPharmacist = true;
                }
            }
        }
        return isPharmacist;
    }

    public List<String> getAllergenEnums() {
        List<String> allergenEnumsString = new ArrayList<>();
        AllergenEnum[] allergenEnums = AllergenEnum.values();
        for (AllergenEnum allergenEnum : allergenEnums) {
            allergenEnumsString.add(allergenEnum.name());
        }

        return allergenEnumsString;
    }

    public Medication createMedication(Medication newMedication) throws UnableToCreateMedicationException {
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
        try {
            Optional<Medication> newMedicationOptional = medicationRepository.findById(inventoryItemId);
            if (newMedicationOptional.isPresent()) {
                Medication medication = newMedicationOptional.get();
                    List<DrugRestriction> drugList = drugRestrictionRepository.findAll();
                    for (DrugRestriction drug : drugList) {
                        if (drug.getDrugName().equals(medication.getInventoryItemName())) {
                            drugRestrictionRepository.delete(drug)  ;
                        }
                    }

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
                if (updatedMedication.getInventoryItemName() != null) {
                    List<DrugRestriction> drugList = drugRestrictionRepository.findAll();
                    for (DrugRestriction drug : drugList) {
                        if (drug.getDrugName().equals(medication.getInventoryItemName())) {
                            drug.setDrugName(updatedMedication.getInventoryItemName());
                        }
                    }
                    medication.setInventoryItemName(updatedMedication.getInventoryItemName());
                }
                if (updatedMedication.getInventoryItemDescription() != null) medication.setInventoryItemDescription(updatedMedication.getInventoryItemDescription());
                if (updatedMedication.getItemTypeEnum() != null) medication.setItemTypeEnum(updatedMedication.getItemTypeEnum());
                if (updatedMedication.getQuantityInStock() != null) medication.setQuantityInStock(updatedMedication.getQuantityInStock());
                if (updatedMedication.getRestockPricePerQuantity() != null) medication.setRestockPricePerQuantity(updatedMedication.getRestockPricePerQuantity());
                if (updatedMedication.getRetailPricePerQuantity() != null) medication.setRetailPricePerQuantity(updatedMedication.getRetailPricePerQuantity());
                if (updatedMedication.getAllergenEnumList() != null) medication.setAllergenEnumList(updatedMedication.getAllergenEnumList());
                if (updatedMedication.getComments() != null) medication.setComments(updatedMedication.getComments());
                if (updatedMedication.getDrugRestrictions() != null) {
                    List<DrugRestriction> updatedRestrictions = updatedMedication.getDrugRestrictions();
                    List<DrugRestriction> drugList = drugRestrictionRepository.findAll();
                    for (DrugRestriction drug : drugList) {
                        if ((!updatedRestrictions.contains(drug)) && (medication.getDrugRestrictions().contains(drug))) {
                            drugRestrictionRepository.delete(drug);
                        }
                    }
                    medication.setDrugRestrictions(updatedMedication.getDrugRestrictions());
                }

                medicationRepository.save(medication);
                return medication;
            } else {
                throw new MedicationNotFoundException("Medication with ID: " + inventoryItemId + " is not found");
            }
        } catch (Exception ex) {
            throw new MedicationNotFoundException(ex.getMessage());
        }
    }

    public List<Medication> getAllMedicationByName() throws MedicationNotFoundException {
        try {
            List<Medication> medicationList = medicationRepository.findAll();
            System.out.print("get equipment");
            return medicationList;
        } catch (Exception ex) {
            throw new MedicationNotFoundException(ex.getMessage());
        }
    }

    public Medication findMedicationByInventoryItemId(Long inventoryItemId) {
        return medicationRepository.findById(inventoryItemId)
                .orElseThrow(() -> new MedicationNotFoundException("Medication Does not Exist"));
    }

    public List<Medication> getAllMedicationsByAllergy(Long pId) {
        ElectronicHealthRecord ehr = electronicHealthRecordRepository.findById(pId).get();
        List<Medication> medicationList = medicationRepository.findAll();
        //List<Medication> newList = new ArrayList<>();
        List<MedicalHistoryRecord>  mhrList = ehr.getListOfMedicalHistoryRecords();

        for (MedicalHistoryRecord mhr : mhrList) {
            if (mhr.getProblemTypeEnum() == ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC) {
                List<Medication> removalList = new ArrayList<>();
                AllergenEnum allergy = AllergenEnum.valueOf(mhr.getDescription());

                for (int j = 0; j < medicationList.size(); j++) {
                    Medication m = medicationList.get(j);
                    if (m.getAllergenEnumList().contains(allergy)) {
                        removalList.add(m);
                    }
                }

                medicationList.removeAll(removalList);
            }
        }

        return medicationList;
    }
}
