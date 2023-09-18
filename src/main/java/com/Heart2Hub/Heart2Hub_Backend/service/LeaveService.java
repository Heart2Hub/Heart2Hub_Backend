package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Leave;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.LeaveNotPendingException;
import com.Heart2Hub.Heart2Hub_Backend.repository.LeaveRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private final LeaveRepository leaveRepository;

    @Autowired
    private final StaffRepository staffRepository;


    public LeaveService(LeaveRepository leaveRepository, StaffRepository staffRepository) {
        this.leaveRepository = leaveRepository;
        this.staffRepository = staffRepository;
    }

    //Need to edit to make sure Managed and List of Leaves
    public List<Leave> retrieveStaffManagedLeaves(Staff staff) {
        return leaveRepository.findByHeadStaff(staff);
    }

//    public List<ApprovalStatusEnum> retrieveApprovalStatusEnum() {
//        return Arrays.asList(ApprovalStatusEnum.PENDING, ApprovalStatusEnum.APPROVED,ApprovalStatusEnum.REJECTED);
//    }
//
//    public List<LeaveTypeEnum> retrieveLeaveTypeEnum() {
//        return Arrays.asList(LeaveTypeEnum.ANNUAL, LeaveTypeEnum.SICK, LeaveTypeEnum.PARENTAL);
//    }

    public Optional<Leave> findById(Long id) { return leaveRepository.findById(id); }

    //Only able to update if Leave has a Pending State
    public void updateLeaveDate(Leave leave) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leave.getLeaveId());

        try {
            Leave l = optionalLeave.get();
            if (l.getApprovalStatusEnum() == ApprovalStatusEnum.PENDING) {
                l.setStartDate(leave.getStartDate());
                l.setEndDate(leave.getEndDate());
                l.setComments(leave.getComments());
                leaveRepository.save(l);
            }
        } catch (Exception ex) {
            throw new LeaveNotPendingException(ex.getMessage());
        }

    }

    public Leave createLeave(LocalDateTime startDate, LocalDateTime endDate, LeaveTypeEnum leaveTypeEnum, Staff staff, Staff headStaff) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime maxDate = currentDate.plusMonths(6);
        LocalDateTime minDate = currentDate.plusMonths(1);

//        List<Leave> staffLeaves = staff.getListOfLeaves();
//        Boolean dateOverlap = false;
//
//        //If dates overlap, then set to true
//        if (staffLeaves != null) {
//            for (int i = 0; i < staffLeaves.size(); i++) {
//                Leave l = staffLeaves.get(i);
//                LocalDateTime start = l.getStartDate();
//                LocalDateTime end = l.getEndDate();
//
//                if (start.isBefore(endDate) && end.isAfter(startDate)) {
//                    dateOverlap = true;
//                    break;
//                }
//            }
//        }

        //If start date > 1 month from now, end date < 6 months from now, and no overlapping dates: Create a new Leave
        if (startDate.isAfter(minDate) && endDate.isBefore(maxDate)) {
            Leave newLeave = new Leave(startDate, endDate, leaveTypeEnum);
            newLeave.setApprovalStatusEnum(ApprovalStatusEnum.PENDING);

            Staff assignedStaff = staffRepository.findById(staff.getStaffId()).get();
            Staff assignedHeadStaff = staffRepository.findById(headStaff.getStaffId()).get();

            newLeave.setStaff(assignedStaff);
            newLeave.setHeadStaff(assignedHeadStaff);

            assignedStaff.getListOfLeaves().add(newLeave);
            assignedHeadStaff.getListOfManagedLeaves().add(newLeave);



//            List<Leave> staffLeave = staff.getListOfLeaves();
//            List<Leave> newStaffLeave = new ArrayList<>(staffLeave);
//            newStaffLeave.add(newLeave);
//            staff.setListOfLeaves(newStaffLeave);
//
//            //headStaff.getListOfManagedLeaves().add(newLeave);
//            List<Leave> headStaffLeave = headStaff.getListOfManagedLeaves();
//            List<Leave> newHeadStaffLeave = new ArrayList<>(headStaffLeave);
//            newHeadStaffLeave.add(newLeave);
//            headStaff.setListOfManagedLeaves(newHeadStaffLeave);

            staffRepository.save(assignedStaff);
            staffRepository.save(assignedHeadStaff);
            leaveRepository.save(newLeave);
            return newLeave;
        } else {
            throw new LeaveNotPendingException();
        }
    }

    //For Head staff to approve
    public void approveLeave(Leave leave) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leave.getLeaveId());

        try {
            Leave l = optionalLeave.get();
            l.setApprovalStatusEnum(ApprovalStatusEnum.APPROVED);
//            if (l.getLeaveTypeEnum().equals(LeaveTypeEnum.ANNUAL)) {
//                l.getStaff().getLeaveBalance().setAnnualLeave(l.getStaff().getLeaveBalance().getAnnualLeave()-1);
//            } else if (l.getLeaveTypeEnum().equals(LeaveTypeEnum.SICK)) {
//                l.getStaff().getLeaveBalance().setSickLeave(l.getStaff().getLeaveBalance().getSickLeave()-1);
//            } else if (l.getLeaveTypeEnum().equals(LeaveTypeEnum.PARENTAL)) {
//                l.getStaff().getLeaveBalance().setParentalLeave(l.getStaff().getLeaveBalance().getParentalLeave()-1);
//            }
            leaveRepository.save(l);

        } catch (Exception ex) {
            throw new LeaveNotPendingException(ex.getMessage());
        }

    }

    //If Head Staff reject leave, we need to reimburse the Leave Balance
    public void rejectLeave(Leave leave) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leave.getLeaveId());

        try {
            Leave l = optionalLeave.get();
            l.setApprovalStatusEnum(ApprovalStatusEnum.REJECTED);
            leaveRepository.save(l);

        } catch (Exception ex) {
            throw new LeaveNotPendingException(ex.getMessage());
        }
    }
}
