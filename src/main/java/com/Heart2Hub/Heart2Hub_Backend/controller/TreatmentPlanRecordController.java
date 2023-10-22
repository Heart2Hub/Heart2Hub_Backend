package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Invitation;
import com.Heart2Hub.Heart2Hub_Backend.entity.TreatmentPlanRecord;
import com.Heart2Hub.Heart2Hub_Backend.service.TreatmentPlanRecordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/treatmentPlanRecord")
@RequiredArgsConstructor
public class TreatmentPlanRecordController {

  private final TreatmentPlanRecordService treatmentPlanRecordService;

  @PostMapping("/createTreatmentPlanRecord")
  public ResponseEntity<TreatmentPlanRecord> createTreatmentPlanRecord(
      @RequestParam Long electronicHealthRecordId, @RequestParam Long staffId,
      @RequestBody TreatmentPlanRecord treatmentPlanRecord) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.createTreatmentPlanRecord(electronicHealthRecordId, staffId,
            treatmentPlanRecord)
    );
  }

  @PutMapping("/updateTreatmentPlanRecord")
  public ResponseEntity<TreatmentPlanRecord> updateTreatmentPlanRecord(
      @RequestParam Long electronicHealthRecordId, @RequestParam Long treatmentPlanRecordId,
      @RequestParam Long staffId,
      @RequestBody TreatmentPlanRecord treatmentPlanRecord) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.updateTreatmentPlanRecord(electronicHealthRecordId,
            treatmentPlanRecordId, staffId, treatmentPlanRecord)
    );
  }

  @PutMapping("/completeTreatmentPlanRecord")
  public ResponseEntity<TreatmentPlanRecord> completeTreatmentPlanRecord(
      @RequestParam Long electronicHealthRecordId, @RequestParam Long treatmentPlanRecordId,
      @RequestParam Long staffId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.completeTreatmentPlanRecord(electronicHealthRecordId,
            treatmentPlanRecordId, staffId)
    );
  }

  @DeleteMapping("/deleteTreatmentPlanRecord")
  public ResponseEntity<Boolean> deleteTreatmentPlanRecord(
      @RequestParam Long electronicHealthRecordId,
      @RequestParam Long treatmentPlanRecordId, @RequestParam Long staffId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.deleteTreatmentPlanRecord(electronicHealthRecordId,
            treatmentPlanRecordId, staffId)
    );
  }

  @GetMapping("/viewTreatmentPlanRecordImages")
  public ResponseEntity<List<ImageDocument>> viewTreatmentPlanRecordImages(
      @RequestParam Long treatmentPlanRecordId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.viewTreatmentPlanRecordImages(treatmentPlanRecordId)
    );
  }

  @PostMapping("/addImageAttachmentToTreatmentPlan")
  public ResponseEntity<TreatmentPlanRecord> addImageAttachmentToTreatmentPlan(
      @RequestParam Long treatmentPlanRecordId,
      @RequestParam String imageLink,
      @RequestParam String createdDate, @RequestParam Long staffId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.addImageAttachmentToTreatmentPlan(treatmentPlanRecordId,
            imageLink, createdDate, staffId)
    );
  }

  @DeleteMapping("/removeImageAttachmentFromTreatmentPlan")
  public ResponseEntity<TreatmentPlanRecord> removeImageAttachmentFromTreatmentPlan(
      @RequestParam Long treatmentPlanRecordId,
      @RequestParam String imageLink,
      @RequestParam Long staffId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.removeImageAttachmentFromTreatmentPlan(treatmentPlanRecordId,
            imageLink, staffId)
    );
  }

  @PostMapping("/addInvitationToTreatmentPlanRecord")
  public ResponseEntity<TreatmentPlanRecord> addInvitationToTreatmentPlanRecord(
      @RequestParam Long treatmentPlanRecordId, @RequestParam Long staffId,
      @RequestParam Long invitedStaffId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.addInvitationToTreatmentPlanRecord(treatmentPlanRecordId,
            staffId, invitedStaffId)
    );
  }

  @DeleteMapping("/deleteInvitationToTreatmentPlanRecord")
  public ResponseEntity<TreatmentPlanRecord> deleteInvitationToTreatmentPlanRecord(
      @RequestParam Long treatmentPlanRecordId, @RequestParam Long staffId,
      @RequestParam Long invitationId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.deleteInvitationToTreatmentPlanRecord(treatmentPlanRecordId,
            staffId, invitationId)
    );
  }

  @GetMapping("/getTreatmentPlanById")
  public ResponseEntity<TreatmentPlanRecord> getTreatmentPlanById(
      @RequestParam Long treatmentPlanRecordId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.getTreatmentPlanById(treatmentPlanRecordId)
    );
  }

  @GetMapping("/getListOfInvitationsInTreatmentPlanRecord")
  public ResponseEntity<List<Invitation>> getListOfInvitationsInTreatmentPlanRecord(
      @RequestParam Long treatmentPlanRecordId) {
    return ResponseEntity.ok(
        treatmentPlanRecordService.getListOfInvitationsInTreatmentPlanRecord(treatmentPlanRecordId)
    );
  }
}
