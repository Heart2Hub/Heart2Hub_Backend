package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.InpatientTreatment;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicationOrder;
import com.Heart2Hub.Heart2Hub_Backend.service.AppointmentService;
import com.Heart2Hub.Heart2Hub_Backend.service.InpatientTreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inpatientTreatment")
@RequiredArgsConstructor
public class InpatientTreatmentController {

    private final InpatientTreatmentService inpatientTreatmentService;
    private final AppointmentService appointmentService;

    @PostMapping("/createInpatientTreatment")
    public ResponseEntity<InpatientTreatment> createInpatientTreatment(
            @RequestParam("serviceItemId") Long serviceItemId, @RequestParam("admissionId") Long admissionId, @RequestParam("staffId") Long staffId,
            @RequestBody InpatientTreatment inpatientTreatment) {

        InpatientTreatment inpatientTreatment1 = inpatientTreatmentService.createInpatientTreatment(serviceItemId, admissionId, staffId, inpatientTreatment);
        appointmentService.sendUpdateToClients("swimlane");
        return ResponseEntity.ok(inpatientTreatment1);

    }

    @GetMapping("/getInpatientTreatmentById")
    public ResponseEntity<InpatientTreatment> getInpatientTreatmentById(@RequestParam("inpatientTreatmentId") Long inpatientTreatmentId) {
        return ResponseEntity.ok(inpatientTreatmentService.getInpatientTreatmentById(inpatientTreatmentId));
    }

    @PutMapping("/updateArrival")
    public ResponseEntity<InpatientTreatment> updateArrival(
            @RequestParam("inpatientTreatmentId") Long inpatientTreatmentId,
            @RequestParam("arrivalStatus") Boolean arrivalStatus) {
        InpatientTreatment inpatientTreatment1 = inpatientTreatmentService.updateArrival(inpatientTreatmentId, arrivalStatus);
        appointmentService.sendUpdateToClients("swimlane");
        return ResponseEntity.ok(inpatientTreatment1);
    }

    @PutMapping("/updateComplete")
    public ResponseEntity<InpatientTreatment> updateComplete(
            @RequestParam("inpatientTreatmentId") Long inpatientTreatmentId,
            @RequestParam("admissionId") Long admissionId) {
        InpatientTreatment inpatientTreatment1 = inpatientTreatmentService.updateComplete(inpatientTreatmentId, admissionId);
        appointmentService.sendUpdateToClients("swimlane");
        return ResponseEntity.ok(inpatientTreatment1);
    }

    @DeleteMapping("/deleteInpatientTreatment")
    public ResponseEntity<String> deleteInpatientTreatment(
            @RequestParam("inpatientTreatmentId") Long inpatientTreatmentId,
            @RequestParam("admissionId") Long admissionId) {
        String msg = inpatientTreatmentService.deleteInpatientTreatment(inpatientTreatmentId, admissionId);
        appointmentService.sendUpdateToClients("swimlane");
        return ResponseEntity.ok(msg);
    }

}
