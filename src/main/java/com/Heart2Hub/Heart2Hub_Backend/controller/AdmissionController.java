package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.AdmissionDTO;
import com.Heart2Hub.Heart2Hub_Backend.dto.AppointmentDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.mapper.AdmissionMapper;
import com.Heart2Hub.Heart2Hub_Backend.service.AdmissionService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.Heart2Hub.Heart2Hub_Backend.service.WardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admission")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionService admissionService;

    private final AdmissionMapper admissionMapper;

    private final WardService wardService;

    private final StaffService staffService;

    @GetMapping("/getAllAdmissions")
    public ResponseEntity<List<Admission>> getAllAdmissions() {
        return ResponseEntity.ok(admissionService.getAllAdmissions());
    }

    @GetMapping("/getAdmissionsForWard")
    public ResponseEntity<List<AdmissionDTO>> getAdmissionsForWard(@RequestParam("wardName") String wardName) {
        Ward ward = wardService.getAllWardsByName(wardName).get(0);
        List<Admission> currentAdmissions = ward.getListOfCurrentDayAdmissions().stream()
                .filter(admission -> admission.getAdmissionDateTime() != null)
                .collect(Collectors.toList());;
        List<AdmissionDTO> admissionDTOS = currentAdmissions.stream().map(admission -> admissionMapper.toDTO(admission)).collect(
                Collectors.toList());
        return ResponseEntity.ok(admissionDTOS);
    }

    @GetMapping("/getAdmissionsForStaff")
    public ResponseEntity<List<AdmissionDTO>> getAdmissionsForStaff(@RequestParam("staffId") Long staffId) {
        Staff staff = staffService.getStaffById(staffId);
        List<Admission> staffAdmissions = staff.getListOfAssignedAdmissions();
        List<AdmissionDTO> admissionDTOS = staffAdmissions.stream().map(admission -> admissionMapper.toDTO(admission)).collect(
                Collectors.toList());
        return ResponseEntity.ok(admissionDTOS);
    }

    @GetMapping("/getAdmissionByAdmissionId")
    public ResponseEntity<AdmissionDTO> getAdmissionByAdmissionId(@RequestParam("admissionId") Long admissionId) {
        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.getAdmissionByAdmissionId(admissionId)));
    }

    @PostMapping("/createAdmission")
    public ResponseEntity<Admission> createAdmission(
            @RequestParam("duration") Integer duration,
            @RequestParam("reason") String reason,
            @RequestParam("patientId") Long patientId,
            @RequestParam("doctorId") Long doctorId) {
        return ResponseEntity.ok(admissionService.createAdmission(duration, reason, patientId, doctorId));
    }

    @PutMapping("/scheduleAdmission")
    public ResponseEntity<Admission> scheduleAdmission(
            @RequestParam("admissionId") Long admissionId,
            @RequestParam("wardAvailabilityId") Long wardAvailabilityId,
            @RequestParam("admission") String admission,
            @RequestParam("discharge") String discharge) {
        return ResponseEntity.ok(admissionService.scheduleAdmission(admissionId, wardAvailabilityId, admission, discharge));
    }

//    @PutMapping("/assignAdmissionToStaff")
//    public ResponseEntity<AdmissionDTO> assignAdmissionToStaff(
//            @RequestParam("admissionId") Long admissionId,
//            @RequestParam("toStaffId") Long toStaffId,
//            @RequestParam("fromStaffId") Long fromStaffId) {
//        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.assignAdmissionToStaff(admissionId, toStaffId, fromStaffId)));
//    }

    @PutMapping("/assignAdmissionToStaff")
    public ResponseEntity<AdmissionDTO> assignAdmissionToStaff(
            @RequestParam("admissionId") Long admissionId,
            @RequestParam("toStaffId") Long toStaffId) {
        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.assignAdmissionToStaff(admissionId, toStaffId)));
    }

//    @PutMapping("/assignAdmissionToNurse")
//    public ResponseEntity<AdmissionDTO> assignAdmissionToNurse(
//            @RequestParam("admissionId") Long admissionId,
//            @RequestParam("toStaffId") Long toStaffId,
//            @RequestParam("fromStaffId") Long fromStaffId) {
//        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.assignAdmissionToNurse(admissionId, toStaffId, fromStaffId)));
//    }
//
//    @PutMapping("/assignAdmissionToAdmin")
//    public ResponseEntity<AdmissionDTO> assignAdmissionToAdmin(
//            @RequestParam("admissionId") Long admissionId,
//            @RequestParam("toStaffId") Long toStaffId,
//            @RequestParam("fromStaffId") Long fromStaffId) {
//        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.assignAdmissionToAdmin(admissionId, toStaffId, fromStaffId)));
//    }

    @PutMapping("/updateAdmissionArrival")
    public ResponseEntity<AdmissionDTO> updateAdmissionArrival(
            @RequestParam("admissionId") Long admissionId,
            @RequestParam("arrivalStatus") Boolean arrivalStatus,
            @RequestParam("staffId") Long staffId) {
        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.updateAdmissionArrival(admissionId, arrivalStatus, staffId)));
    }

    @PutMapping("/updateAdmissionComments")
    public ResponseEntity<AdmissionDTO> updateAdmissionComments(
            @RequestParam("admissionId") Long admissionId,
            @RequestParam("comments") String comments,
            @RequestParam("staffId") Long staffId) {
        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.updateAdmissionComments(admissionId, comments, staffId)));
    }

    @PutMapping("/cancelAdmission")
    public ResponseEntity<String> cancelAdmission(
            @RequestParam("admissionId") Long admissionId,
            @RequestParam("wardId") Long wardId) {
        return ResponseEntity.ok(admissionService.cancelAdmission(admissionId, wardId));
    }


    @PutMapping("/handleDischarge")
    public ResponseEntity<String> handleDischarge(@RequestParam("date") String date) {
        return ResponseEntity.ok(admissionService.dischargeAdmissions(date));
    }

    @PutMapping("/handleAllocateIncoming")
    public ResponseEntity<String> handleAllocateIncoming(@RequestParam("date") String date) {
        return ResponseEntity.ok(admissionService.allocateScheduledAdmissions(date));
    }

    @PutMapping("/updateDischargeDate")
    public ResponseEntity<AdmissionDTO> updateDischargeDate(
            @RequestParam("admissionId") Long admissionId,
            @RequestParam("dischargeDate") String dischargeDate,
            @RequestParam("transactionItemId") Long transactionItemId) {
        return ResponseEntity.ok(admissionMapper.toDTO(admissionService.updateDischargeDate(admissionId, dischargeDate, transactionItemId)));
    }

    @PutMapping("/addImageAttachment")
    public ResponseEntity<Admission> addImageAttachment(
            @RequestParam("admissionId") Long admissionId,
            @RequestParam("imageLink") String imageLink,
            @RequestParam("createdDate") String createdDate) {
        return ResponseEntity.ok(admissionService.addImageAttachment(admissionId, imageLink, createdDate));
    }

    @GetMapping("/viewImageAttachments")
    public ResponseEntity<List<ImageDocument>> viewImageAttachments(
            @RequestParam("admissionId") Long admissionId) {
        return ResponseEntity.ok(admissionService.viewImageAttachments(admissionId));
    }

}
