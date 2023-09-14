package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.entity.Leave;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.LeaveRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.LeaveBalanceService;
import com.Heart2Hub.Heart2Hub_Backend.service.LeaveService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LeaveController {


    private final LeaveBalanceService leaveBalanceService;
    private final LeaveService leaveService;
    private final StaffService staffService;

    //As a staff, I can check my leave Balance
    @GetMapping("/leaveBalance/{staffid}")
    public ResponseEntity<LeaveBalance> getLeaveBalance (@PathVariable("staffid") long staffid) {
        Optional<Staff> staff = staffService.findById(staffid);
        return ResponseEntity.ok(staff.get().getLeaveBalance());

    }

    //As a staff, I can view my upcoming leaves
    @GetMapping("/listLeave/{staffid}")
    public ResponseEntity<List<Leave>> getAllStaffLeaves(@PathVariable("staffid") long staffid) {
        Optional<Staff> staff = staffService.findById(staffid);

        return ResponseEntity.ok(staff.get().getListOfLeaves());
    }

    //As a Head Staff, I can view Leave applications of staff members under me
    @GetMapping("/managedLeave/{staffid}")
    public ResponseEntity<List<Leave>> getAllManagedLeaves(@PathVariable("staffid") long staffid) {
        Optional<Staff> staff = staffService.findById(staffid);

        return ResponseEntity.ok(leaveService.retrieveStaffManagedLeaves(staff.get()));
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
    @PutMapping("/leave/approveLeaveDate/{leaveid}")
    public ResponseEntity<Leave> approveLeaveDate(@PathVariable long leaveid) {
        Optional<Leave> leaveData = leaveService.findById(leaveid);

        if (leaveData.isPresent()) {
            Leave l = leaveData.get();
            leaveService.approveLeave(l);
            return ResponseEntity.ok(l);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //As a Head Staff, I can approve a leave application of a staff member
    @PutMapping("/leave/rejectLeaveDate/{leaveid}")
    public ResponseEntity<Leave> rejectLeaveDate(@PathVariable long leaveid) {
        Optional<Leave> leaveData = leaveService.findById(leaveid);
        Optional<LeaveBalance> leaveBalance = leaveBalanceService.getLeaveBalanceById(leaveData.get().getStaff().getLeaveBalance().getLeaveBalanceId());

        if (leaveData.isPresent()) {
            LeaveBalance lb = leaveBalance.get();
            Leave leave = leaveData.get();
            //Find the number of days to reimburse the staff
            Duration duration = Duration.between(leave.getStartDate(), leave.getEndDate());
            Integer days = (int) duration.toDays();

            //Reimburse staff accordingly to their Leave Type
            if (leave.getLeaveTypeEnum() == LeaveTypeEnum.ANNUAL) {
                lb.setAnnualLeave(lb.getAnnualLeave() + days);
            } else if (leave.getLeaveTypeEnum() == LeaveTypeEnum.SICK) {
                lb.setSickLeave(lb.getSickLeave() + days);
            } else {
                lb.setParentalLeave(lb.getParentalLeave() + days);
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
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @RequestParam("leaveTypeEnum") LeaveTypeEnum leaveTypeEnum,
            @RequestBody Staff staff,
            @RequestBody Staff headStaff) {
        Optional<LeaveBalance> leaveBalance = leaveBalanceService.getLeaveBalanceById(staff.getLeaveBalance().getLeaveBalanceId());
        Leave l;

        if (leaveBalance.isPresent()) {
            LeaveBalance lb = leaveBalance.get();
            Duration duration = Duration.between(startDate, endDate);
            Integer days = (int) duration.toDays();

            if (leaveTypeEnum == LeaveTypeEnum.ANNUAL) {
                if (days <= lb.getAnnualLeave()) {
                    l = leaveService.createLeave(startDate,endDate,leaveTypeEnum,staff,headStaff);
                    lb.setAnnualLeave(lb.getAnnualLeave() - days);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }

            } else if (leaveTypeEnum == LeaveTypeEnum.SICK) {
                if (days <= lb.getSickLeave()) {
                    l = leaveService.createLeave(startDate,endDate,leaveTypeEnum,staff,headStaff);
                    lb.setSickLeave(lb.getSickLeave() - days);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                if (days <= lb.getParentalLeave()) {
                    l = leaveService.createLeave(startDate,endDate,leaveTypeEnum,staff,headStaff);
                    lb.setParentalLeave(lb.getParentalLeave() - days);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
            leaveBalanceService.updateLeaveBalance(lb);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(l);
    }



}
