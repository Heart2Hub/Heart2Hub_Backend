package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Leave;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.InvalidDateRangeException;
import com.Heart2Hub.Heart2Hub_Backend.exception.LeaveOverlapException;
import com.Heart2Hub.Heart2Hub_Backend.repository.LeaveRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.LeaveBalanceService;
import com.Heart2Hub.Heart2Hub_Backend.service.LeaveService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {


    private final LeaveBalanceService leaveBalanceService;
    private final LeaveService leaveService;
    private final StaffService staffService;

    //As a staff, I can check my leave Balance
    @GetMapping("/getLeaveBalance")
    public ResponseEntity<LeaveBalance> getLeaveBalance (@RequestParam("staffId") long staffId) {
        Staff staff = staffService.findById(staffId);
        return ResponseEntity.ok(staff.getLeaveBalance());

    }

    //As a staff, I can view my upcoming leaves
    @GetMapping("/getAllStaffLeaves/{staffid}")
    public ResponseEntity<List<Leave>> getAllStaffLeaves(@PathVariable("staffid") long staffid) {
        Staff staff = staffService.findById(staffid);

        return ResponseEntity.ok(staff.getListOfLeaves());
    }

    //As a Head Staff, I can view Leave applications of staff members under me
    @GetMapping("/getAllManagedLeaves")
    public ResponseEntity<List<Leave>> getAllManagedLeaves(@RequestParam("staffId") long staffId) {
        Staff staff = staffService.findById(staffId);

        return ResponseEntity.ok(leaveService.retrieveStaffManagedLeaves(staff));
    }

//    @GetMapping("/getApprovalStatusEnumList")
//    public ResponseEntity<List<ApprovalStatusEnum>> getAllApprovalEnumList() {
//        return ResponseEntity.ok(leaveService.retrieveApprovalStatusEnum());
//    }

    @GetMapping("/getLeaveTypeEnumList")
    public ResponseEntity<LeaveTypeEnum[]> getAllLeaveTypeEnumList() {
        LeaveTypeEnum[] list = LeaveTypeEnum.values();
        return ResponseEntity.ok(list);
    }

    //As a staff, I can update my PENDING leaves
    @PutMapping("/leave/updateLeaveDate/{staffid}")
    public ResponseEntity<Leave> updateLeaveDate(@PathVariable("staffid") long id, @RequestBody Leave leave) {
        Optional<Leave> leaveData = leaveService.findById(leave.getLeaveId());

        if (leaveData.isPresent()) {
            leaveService.updateLeaveDate(leave);
            return ResponseEntity.ok(leave);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //As a Head Staff, I can approve a leave application of a staff member
    @PutMapping("/approveLeaveDate")
    public ResponseEntity<Leave> approveLeaveDate(@RequestParam("leaveId") long leaveId) {
        Optional<Leave> leaveData = leaveService.findById(leaveId);

        if (leaveData.isPresent()) {
            Leave l = leaveData.get();
            leaveService.approveLeave(l);
            return ResponseEntity.ok(l);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //As a Head Staff, I can approve a leave application of a staff member
    @PutMapping("/rejectLeaveDate")
    public ResponseEntity<Leave> rejectLeaveDate(@RequestParam("leaveId") long leaveId) {
        Optional<Leave> leaveData = leaveService.findById(leaveId);
        Optional<LeaveBalance> leaveBalance = leaveBalanceService.getLeaveBalanceById(leaveData.get().getStaff().getLeaveBalance().getLeaveBalanceId());

        if (leaveData.isPresent()) {
            LeaveBalance lb = leaveBalance.get();
            Leave leave = leaveData.get();
            //Find the number of days to reimburse the staff
            Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());
            Integer days = (int) duration.toDays();

            //Reimburse staff accordingly to their Leave Type
            if (leave.getLeaveTypeEnum() == LeaveTypeEnum.ANNUAL) {
                lb.setAnnualLeave(lb.getAnnualLeave() + days + 1);
            } else if (leave.getLeaveTypeEnum() == LeaveTypeEnum.SICK) {
                lb.setSickLeave(lb.getSickLeave() + days + 1);
            } else {
                lb.setParentalLeave(lb.getParentalLeave() + days + 1);
            }

            //Change Leave status to "PENDING", Update Leave Balance
            leaveService.rejectLeave(leave);
            leaveBalanceService.updateLeaveBalance(lb);
            return ResponseEntity.ok(leave);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    //As as staff, I can create a Leave
    @PostMapping("/createLeave")
    public ResponseEntity<Leave> createLeave(
            @RequestBody Map<String, Object> requestBody) {

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        //Leave l = objectMapper.convertValue(requestBody.get("leaveRecord"), Leave.class);

        LocalDateTime startDate = LocalDateTime.of(LocalDate.parse(requestBody.get("startDate").toString()), LocalTime.MIDNIGHT);
        LocalDateTime endDate = LocalDateTime.of(LocalDate.parse(requestBody.get("endDate").toString()), LocalTime.MIDNIGHT);

        LeaveTypeEnum leaveTypeEnum = LeaveTypeEnum.valueOf(requestBody.get("selectedLeaveTypeEnum").toString());

        Staff staff = staffService.findById(Long.parseLong(requestBody.get("staffId").toString()));
        Staff headStaff = staffService.findById(Long.parseLong(requestBody.get("selectedStaff").toString()));

        String comments = (String) requestBody.get("comments").toString();

        Optional<LeaveBalance> leaveBalance = leaveBalanceService.getLeaveBalanceById(staff.getLeaveBalance().getLeaveBalanceId());
        Leave l = new Leave();

        if (leaveTypeEnum != LeaveTypeEnum.UNPAID) {
            if (leaveBalance.isPresent()) {
                LeaveBalance lb = leaveBalance.get();
                Duration duration = Duration.between(startDate, endDate);
                Integer days = (int) duration.toDays() + 1;

                if (leaveTypeEnum == LeaveTypeEnum.ANNUAL) {
                    if (requestBody.get("imageLink") != null && requestBody.get("createdDate") != null) {
                        String imageLink = requestBody.get("imageLink").toString();
                        String createdDateStr = requestBody.get("createdDate").toString();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        LocalDateTime createdDate = LocalDateTime.parse(createdDateStr, formatter);

                        ImageDocument imageDocument = new ImageDocument(imageLink, createdDate);

                        l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments, imageDocument);
                    } else {
                        l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments);
                    }
                    lb.setAnnualLeave(lb.getAnnualLeave() - days);

                } else if (leaveTypeEnum == LeaveTypeEnum.SICK) {
                    if (requestBody.get("imageLink") != null && requestBody.get("createdDate") != null) {
                        String imageLink = requestBody.get("imageLink").toString();
                        String createdDateStr = requestBody.get("createdDate").toString();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        LocalDateTime createdDate = LocalDateTime.parse(createdDateStr, formatter);

                        ImageDocument imageDocument = new ImageDocument(imageLink, createdDate);

                        l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments, imageDocument);
                    } else {
                        l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments);
                    }
                    lb.setSickLeave(lb.getSickLeave() - days);
                } else {
                    if (requestBody.get("imageLink") != null && requestBody.get("createdDate") != null) {
                        String imageLink = requestBody.get("imageLink").toString();
                        String createdDateStr = requestBody.get("createdDate").toString();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                        LocalDateTime createdDate = LocalDateTime.parse(createdDateStr, formatter);

                        ImageDocument imageDocument = new ImageDocument(imageLink, createdDate);

                        l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments, imageDocument);
                    } else {
                        l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments);
                    }
//                if (days <= lb.getParentalLeave()) {
                    lb.setParentalLeave(lb.getParentalLeave() - days);
//                } else {
//                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//                }
                }
                leaveBalanceService.updateLeaveBalance(lb);
            }

        } else {
            if (requestBody.get("imageLink") != null && requestBody.get("createdDate") != null) {
                String imageLink = requestBody.get("imageLink").toString();
                String createdDateStr = requestBody.get("createdDate").toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime createdDate = LocalDateTime.parse(createdDateStr, formatter);

                ImageDocument imageDocument = new ImageDocument(imageLink, createdDate);

                l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments, imageDocument);
            } else {
                l = leaveService.createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments);
            }
        }

        return ResponseEntity.ok(l);
    }

    @DeleteMapping("/deleteLeave/{leaveId}")
    public ResponseEntity<String> deleteLeave(@PathVariable Long leaveId) {
        try {
            leaveService.deleteLeave(leaveId);

            return ResponseEntity.ok("Leave record deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete leave record");
        }
    }

    @PutMapping("/updateLeave/{leaveId}/{staffId}")
    public ResponseEntity<Leave> updateLeave(
            @PathVariable Long leaveId,
            @PathVariable Long staffId,
            @RequestBody Map<String, Object> requestBody) {


            LocalDateTime newStartDate = LocalDateTime.of(LocalDate.parse(requestBody.get("startDate").toString()), LocalTime.MIDNIGHT);
            LocalDateTime newEndDate = LocalDateTime.of(LocalDate.parse(requestBody.get("endDate").toString()), LocalTime.MIDNIGHT);
            String newComments = requestBody.get("comments").toString();

            Leave updatedLeave = leaveService.updateLeave(leaveId, newStartDate, newEndDate, newComments, staffId);
            return ResponseEntity.ok(updatedLeave);


    }
}
