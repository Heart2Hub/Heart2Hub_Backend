package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Subsidy;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.SubsidyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/subsidy")
public class SubsidyController {

    private final SubsidyService subsidyService;

    public SubsidyController(SubsidyService subsidyService) {
        this.subsidyService = subsidyService;
    }

    @GetMapping("/getAllSubsidies")
    public ResponseEntity<List<Subsidy>> getAllSubsidies() {
        List<Subsidy> subsidies = subsidyService.findAllSubsidies();
        return new ResponseEntity<>(subsidies, HttpStatus.OK);
    }


    @PostMapping("/createSubsidy")
    public ResponseEntity<Subsidy> createSubsidy(@RequestParam BigDecimal subsidyRate,
                                                 @RequestParam ItemTypeEnum itemTypeEnum,
                                                 @RequestParam LocalDateTime minDOB,
                                                 @RequestParam String sex,
                                                 @RequestParam String race,
                                                 @RequestParam String nationality) {
        Subsidy createdSubsidy = subsidyService.createSubsidy(subsidyRate, itemTypeEnum, minDOB, sex, race, nationality);
        return new ResponseEntity<>(createdSubsidy, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteSubsidy/{subsidyId}")
    public ResponseEntity<Void> deleteSubsidy(@PathVariable Long subsidyId) {
        subsidyService.deleteSubsidyById(subsidyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/updateSubsidyRate/{subsidyId}")
    public ResponseEntity<Void> updateSubsidyRate(@PathVariable Long subsidyId, @RequestParam String newSubsidyRate) {
        BigDecimal subsidyRate = new BigDecimal(newSubsidyRate);
        subsidyService.updateSubsidyRate(subsidyId, subsidyRate);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

