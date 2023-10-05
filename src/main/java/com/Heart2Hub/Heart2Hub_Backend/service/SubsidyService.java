package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Subsidy;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.SubsidyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubsidyService {

    private final SubsidyRepository subsidyRepository;
    private final ElectronicHealthRecordRepository electronicHealthRecordRepository;
    private final ElectronicHealthRecordService electronicHealthRecordService;

    public SubsidyService(SubsidyRepository subsidyRepository, ElectronicHealthRecordRepository electronicHealthRecordRepository, ElectronicHealthRecordService electronicHealthRecordService) {
        this.subsidyRepository = subsidyRepository;
        this.electronicHealthRecordRepository = electronicHealthRecordRepository;
        this.electronicHealthRecordService = electronicHealthRecordService;
    }

    public List<Subsidy> findAllSubsidies() {
        return subsidyRepository.findAll();
    }

    public Subsidy createSubsidy(BigDecimal subsidyRate, ItemTypeEnum itemTypeEnum,
                                 LocalDateTime minDOB, String sex, String race, String nationality) {
        Subsidy s = new Subsidy(subsidyRate, itemTypeEnum, minDOB, sex, race, nationality);

        List<ElectronicHealthRecord> listOfEHR = electronicHealthRecordService.getAllElectronicHealthRecords()
                .stream()
                .filter(ehr -> ehr.getDateOfBirth().isBefore(minDOB) || ehr.getDateOfBirth().isEqual(minDOB))
                .filter(ehr -> "All".equals(sex) || Objects.equals(ehr.getSex(), sex))
                .filter(ehr -> "All".equals(race) || Objects.equals(ehr.getRace(), race))
                .filter(ehr -> "All".equals(nationality) || Objects.equals(ehr.getNationality(), nationality))
                .collect(Collectors.toList());

        // Attach the Subsidy object to each filtered EHR
        for (ElectronicHealthRecord ehr : listOfEHR) {
            ehr.getListOfSubsidies().add(s);
        }

        subsidyRepository.save(s);

        return s;
    }



    public void deleteSubsidyById(Long subsidyId) {
        // Find all Electronic Health Records where dateOfBath is at least minDOB
        List<ElectronicHealthRecord> listOfEHR = electronicHealthRecordService.getAllElectronicHealthRecords();

        // Delete the Subsidy object by ID
        subsidyRepository.deleteById(subsidyId);

        // Remove the reference to the deleted Subsidy from each filtered EHR
        for (ElectronicHealthRecord ehr : listOfEHR) {
            ehr.getListOfSubsidies().removeIf(subsidy -> subsidy.getSubsidyId().equals(subsidyId));
        }
    }

    public void updateSubsidyRate(Long subsidyId, BigDecimal newSubsidyRate) {
        List<ElectronicHealthRecord> listOfEHR = electronicHealthRecordService.getAllElectronicHealthRecords();


        // Update the Subsidy rate by ID
        Optional<Subsidy> optionalSubsidy = subsidyRepository.findById(subsidyId);
        if (optionalSubsidy.isPresent()) {
            Subsidy subsidy = optionalSubsidy.get();
            subsidy.setSubsidyRate(newSubsidyRate);
            subsidyRepository.save(subsidy);
        }

        // Update the reference to the Subsidy rate in each filtered EHR
        for (ElectronicHealthRecord ehr : listOfEHR) {
            List<Subsidy> subsidies = ehr.getListOfSubsidies();
            for (Subsidy subsidy : subsidies) {
                if (subsidy.getSubsidyId().equals(subsidyId)) {
                    subsidy.setSubsidyRate(newSubsidyRate);
                }
            }
        }
    }

    public void refreshSubsidiesForNewEHRs() {

        List<Subsidy> existingSubsidies = subsidyRepository.findAll();

        List<ElectronicHealthRecord> newEHRs = electronicHealthRecordService.getAllElectronicHealthRecords();


        for (Subsidy subsidy : existingSubsidies) {
            List<ElectronicHealthRecord> listOfEHR = newEHRs
                    .stream()
                    .filter(ehr -> ehr.getDateOfBirth().isBefore(subsidy.getMinDOB()) || ehr.getDateOfBirth().isEqual(subsidy.getMinDOB()))
                    .filter(ehr -> "All".equals(subsidy.getSex()) || Objects.equals(ehr.getSex(), subsidy.getSex()))
                    .filter(ehr -> "All".equals(subsidy.getRace()) || Objects.equals(ehr.getRace(), subsidy.getRace()))
                    .filter(ehr -> "All".equals(subsidy.getNationality()) || Objects.equals(ehr.getNationality(), subsidy.getNationality()))
                    .collect(Collectors.toList());

            for (ElectronicHealthRecord ehr : listOfEHR) {
                if (!ehr.getListOfSubsidies().contains(subsidy)) {
                    ehr.getListOfSubsidies().add(subsidy);
                }
            }
        }

        electronicHealthRecordRepository.saveAll(newEHRs);
    }

    public void refreshSubsidiesForEHR(Long ehrId) {

            ElectronicHealthRecord ehr = electronicHealthRecordRepository.findById(ehrId).get();

            // Fetch all existing subsidies
            List<Subsidy> existingSubsidies = subsidyRepository.findAll();

            // Loop through existing subsidies to find eligible ones for 'ehr'
            for (Subsidy subsidy : existingSubsidies) {
                // Check if the EHR already has this subsidy attached
                boolean hasSubsidy = ehr.getListOfSubsidies().stream()
                        .anyMatch(s -> s.getSubsidyId().equals(subsidy.getSubsidyId()));

                // If the EHR doesn't have this subsidy and meets the criteria, attach it
                if (!hasSubsidy && meetsCriteria(ehr, subsidy)) {
                    ehr.getListOfSubsidies().add(subsidy);
                }
            }

            // Save the updated EHR with attached subsidies
            electronicHealthRecordRepository.save(ehr);

    }

    // Method to check if an EHR meets the criteria of a subsidy
    private boolean meetsCriteria(ElectronicHealthRecord ehr, Subsidy subsidy) {
        boolean meetsMinDOB = ehr.getDateOfBirth().isBefore(subsidy.getMinDOB()) || ehr.getDateOfBirth().isEqual(subsidy.getMinDOB());
        boolean meetsSex = "All".equals(subsidy.getSex()) || Objects.equals(subsidy.getSex(), ehr.getSex());
        boolean meetsRace = "All".equals(subsidy.getRace()) || Objects.equals(subsidy.getRace(), ehr.getRace());
        boolean meetsNationality = "All".equals(subsidy.getNationality()) || Objects.equals(subsidy.getNationality(), ehr.getNationality());
        return meetsMinDOB && meetsSex && meetsRace && meetsNationality;
    }

}



