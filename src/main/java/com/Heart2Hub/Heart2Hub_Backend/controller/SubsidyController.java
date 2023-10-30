package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Subsidy;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.SubsidyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subsidy")
@RequiredArgsConstructor
public class SubsidyController {

    private final SubsidyService subsidyService;

    @GetMapping("/getAllSubsidies")
    public ResponseEntity<List<Subsidy>> getAllSubsidies() {
        List<Subsidy> subsidies = subsidyService.findAllSubsidies();
        return new ResponseEntity<>(subsidies, HttpStatus.OK);
    }


    @PostMapping("/createSubsidy")
    public ResponseEntity<Subsidy> createSubsidy(@RequestBody Map<String, Object> requestBody) {
        String sex = requestBody.get("sex").toString();
        String race = requestBody.get("race").toString();
        String nationality = requestBody.get("nationality").toString();
        String subsidyName = requestBody.get("subsidyName").toString();
        String subsidyDescription = requestBody.get("subsidyDescription").toString();
        ItemTypeEnum itemTypeEnum = ItemTypeEnum.valueOf(requestBody.get("itemTypeEnum").toString());
        BigDecimal subsidyRate = BigDecimal.valueOf(Double.parseDouble(requestBody.get("subsidyRate").toString()));
        Integer year = Integer.parseInt(requestBody.get("minDOB").toString());
        LocalDateTime minDOB = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);

        Subsidy createdSubsidy = subsidyService.createSubsidy(subsidyRate, itemTypeEnum, minDOB, sex, race, nationality, subsidyName, subsidyDescription);
        return new ResponseEntity<>(createdSubsidy, HttpStatus.CREATED);
    }

    @DeleteMapping("/deleteSubsidy/{subsidyId}")
    public ResponseEntity<Void> deleteSubsidy(@PathVariable Long subsidyId) {
        subsidyService.deleteSubsidyById(subsidyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/updateSubsidyRate/{subsidyId}")
    public ResponseEntity<Void> updateSubsidyRate(@PathVariable Long subsidyId, @RequestBody Map<String, Object> requestBody) {
        BigDecimal subsidyRate = new BigDecimal(Double.parseDouble(requestBody.get("subsidyRate").toString())/100);
        subsidyService.updateSubsidyRate(subsidyId, subsidyRate);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

