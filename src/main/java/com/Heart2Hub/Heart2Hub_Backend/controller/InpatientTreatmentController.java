package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.InpatientTreatment;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicationOrder;
import com.Heart2Hub.Heart2Hub_Backend.service.InpatientTreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inpatientTreatment")
@RequiredArgsConstructor
public class InpatientTreatmentController {

    private final InpatientTreatmentService inpatientTreatmentService;

    @PostMapping("/createInpatientTreatment")
    public ResponseEntity<InpatientTreatment> createInpatientTreatment(
            @RequestParam("serviceItemId") Long serviceItemId, @RequestParam("admissionId") Long admissionId, @RequestParam("staffId") Long staffId,
            @RequestBody InpatientTreatment inpatientTreatment) {
        return ResponseEntity.ok(
                inpatientTreatmentService.createInpatientTreatment(serviceItemId, admissionId, staffId, inpatientTreatment)
        );
    }

    @GetMapping("/getInpatientTreatmentById")
    public ResponseEntity<InpatientTreatment> getInpatientTreatmentById(@RequestParam("inpatientTreatmentId") Long inpatientTreatmentId) {
        return ResponseEntity.ok(inpatientTreatmentService.getInpatientTreatmentById(inpatientTreatmentId));
    }

    @PutMapping("/updateArrival")
    public ResponseEntity<InpatientTreatment> updateArrival(
            @RequestParam("inpatientTreatmentId") Long inpatientTreatmentId,
            @RequestParam("arrivalStatus") Boolean arrivalStatus) {
        return ResponseEntity.ok(inpatientTreatmentService.updateArrival(inpatientTreatmentId, arrivalStatus));
    }

    @PutMapping("/updateComplete")
    public ResponseEntity<InpatientTreatment> updateComplete(
            @RequestParam("inpatientTreatmentId") Long inpatientTreatmentId,
            @RequestParam("admissionId") Long admissionId) {
        return ResponseEntity.ok(inpatientTreatmentService.updateComplete(inpatientTreatmentId, admissionId));
    }

    @DeleteMapping("/deleteInpatientTreatment")
    public ResponseEntity<String> deleteInpatientTreatment(
            @RequestParam("inpatientTreatmentId") Long inpatientTreatmentId,
            @RequestParam("admissionId") Long admissionId) {
        return ResponseEntity.ok(inpatientTreatmentService.deleteInpatientTreatment(inpatientTreatmentId, admissionId));
    }

}
