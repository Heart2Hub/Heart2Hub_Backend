package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.entity.Leave;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ApprovalStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.LeaveBalanceRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.LeaveRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private final LeaveRepository leaveRepository;

    @Autowired
    private final StaffRepository staffRepository;

    @Autowired
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private final ImageDocumentService imageDocumentService;

    public LeaveService(LeaveRepository leaveRepository, StaffRepository staffRepository, LeaveBalanceRepository leaveBalanceRepository, ImageDocumentService imageDocumentService) {
        this.leaveRepository = leaveRepository;
        this.staffRepository = staffRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.imageDocumentService = imageDocumentService;
    }

    //Need to edit to make sure Managed and List of Leaves
    public List<Leave> retrieveStaffManagedLeaves(Staff staff) {
        return leaveRepository.findByHeadStaff(staff);
    }

    public Optional<Leave> findById(Long id) {
        return leaveRepository.findById(id);
    }

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

    public Leave createLeave(LocalDateTime startDate, LocalDateTime endDate, LeaveTypeEnum leaveTypeEnum, Staff staff, Staff headStaff, String comments) {
        return createLeave(startDate, endDate, leaveTypeEnum, staff, headStaff, comments, null);
    }

    public Leave createLeave(LocalDateTime startDate, LocalDateTime endDate, LeaveTypeEnum leaveTypeEnum, Staff staff, Staff headStaff, String comments, ImageDocument imageDocument) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime maxDate = currentDate.plusMonths(6);
        LocalDateTime minDate = currentDate.plusMonths(1).minusDays(1);

        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date cannot be before start date");
        }


        // Check if the startDate and endDate are within the allowed date ranges
        if(leaveTypeEnum == LeaveTypeEnum.ANNUAL) {
            if (startDate.isAfter(minDate) && endDate.isBefore(maxDate)) {
            } else {
                throw new InvalidDateRangeException("Start date and end date must be within allowed date ranges.");
            }
        }
        if (endDate.isBefore(maxDate)) {
                // Check for overlapping leaves
                List<Leave> staffLeaves = leaveRepository.findByStaff(staff);

                // Check for overlaps in the fetched leaves
                boolean hasOverlap = staffLeaves.stream()
                        .anyMatch(leave -> (startDate.isBefore(leave.getEndDate()) || startDate.isEqual(leave.getEndDate())) &&
                                (endDate.isAfter(leave.getStartDate()) || endDate.isEqual(leave.getStartDate())));

                if (!hasOverlap) {

                    Leave newLeave = new Leave(startDate, endDate, leaveTypeEnum);
                    newLeave.setApprovalStatusEnum(ApprovalStatusEnum.PENDING);

                    Staff assignedStaff = staffRepository.findById(staff.getStaffId()).get();
                    Staff assignedHeadStaff = staffRepository.findById(headStaff.getStaffId()).get();
                    LeaveBalance lb = leaveBalanceRepository.findById(assignedStaff.getLeaveBalance().getLeaveBalanceId()).get();

                    Duration duration = Duration.between(startDate, endDate);
                    int days = (int) duration.toDays() + 1;

                    if (leaveTypeEnum == LeaveTypeEnum.ANNUAL) {
                        if (days <= lb.getAnnualLeave()) {
                        } else {
                            throw new InsufficientLeaveBalanceException("Insufficient annual leave balance.");
                        }

                    } else if (leaveTypeEnum == LeaveTypeEnum.SICK) {
                        if (days <= lb.getSickLeave()) {

                        } else {
                            throw new InsufficientLeaveBalanceException("Insufficient sick leave balance.");
                        }
                    } else if (leaveTypeEnum == LeaveTypeEnum.PARENTAL){
                        if (days <= lb.getParentalLeave()) {
                        } else {
                            throw new InsufficientLeaveBalanceException("Insufficient parental leave balance.");
                        }
                    }

                    newLeave.setStaff(assignedStaff);
                    newLeave.setHeadStaff(assignedHeadStaff);
                    newLeave.setComments(comments);

                    assignedStaff.getListOfLeaves().add(newLeave);
                    assignedHeadStaff.getListOfManagedLeaves().add(newLeave);

                    if (imageDocument != null) {
                        ImageDocument createdImageDocument = imageDocumentService.createImageDocument(imageDocument);
                        newLeave.setImageDocuments(createdImageDocument); // Set the image document if provided
                    }

                    staffRepository.save(assignedStaff);
                    staffRepository.save(assignedHeadStaff);
                    leaveRepository.save(newLeave);
                    return newLeave;
                } else {
                    throw new LeaveOverlapException("Leave overlaps with existing leaves.");
                }

        } else {
            throw new InvalidDateRangeException("Start date and end date must be within allowed date ranges.");
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

    public void deleteLeave(long leaveId) {
        Leave l = leaveRepository.findById(leaveId).get();
        Staff staff = staffRepository.findById(l.getStaff().getStaffId()).get();
        Staff headStaff = staffRepository.findById(l.getHeadStaff().getStaffId()).get();
        LeaveBalance lb = leaveBalanceRepository.findById(staff.getLeaveBalance().getLeaveBalanceId()).get();

        Duration duration = Duration.between(l.getStartDate(), l.getEndDate());
        Integer days = (int) duration.toDays() +1;
        if (l.getLeaveTypeEnum() == LeaveTypeEnum.ANNUAL) {
            lb.setAnnualLeave(lb.getAnnualLeave() + days);
        } else if (l.getLeaveTypeEnum() == LeaveTypeEnum.SICK) {
            lb.setSickLeave(lb.getSickLeave() + days);
        } else {
            lb.setParentalLeave(lb.getParentalLeave() + days);
        }

        staff.getListOfLeaves().remove(l);
        headStaff.getListOfManagedLeaves().remove(l);

        leaveRepository.delete(l);
        staffRepository.save(staff);
        staffRepository.save(headStaff);
        leaveBalanceRepository.save(lb);

    }

    public Leave updateLeave(
            Long leaveId,
            LocalDateTime newStartDate,
            LocalDateTime newEndDate,
            String newComments,
            Long staffId) {

        Optional<Leave> leaveOptional = leaveRepository.findById(leaveId);

        if (leaveOptional.isPresent()) {
            Leave leaveToUpdate = leaveOptional.get();

            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime maxDate = currentDate.plusMonths(6);
            LocalDateTime minDate = currentDate.plusMonths(1);

            LocalDateTime prevDate = currentDate.minusDays(1);

            if(leaveToUpdate.getLeaveTypeEnum() == LeaveTypeEnum.ANNUAL) {
                if (newStartDate.isAfter(minDate) && newEndDate.isBefore(maxDate)) {
                } else {
                    throw new InvalidDateRangeException("Start date and end date must be within allowed date ranges.");
                }
            }

            if (newStartDate.isAfter(prevDate) && newEndDate.isBefore(maxDate)) {
                    List<Leave> staffLeaves = staffRepository.findById(staffId).get().getListOfLeaves();

                    // Check for overlaps in the fetched leaves, excluding the leave being updated
                    boolean hasOverlap = staffLeaves.stream()
                            .filter(leave -> !leave.equals(leaveToUpdate))
                            .anyMatch(leave -> (newStartDate.isBefore(leave.getEndDate()) || newStartDate.isEqual(leave.getEndDate())) &&
                                    (newEndDate.isAfter(leave.getStartDate()) || newEndDate.isEqual(leave.getStartDate())));

                    if (!hasOverlap) {

                        Duration duration1 = Duration.between(leaveToUpdate.getStartDate(), leaveToUpdate.getEndDate());
                        Integer plusDays = (int) duration1.toDays() + 1;

                        Duration duration2 = Duration.between(newStartDate, newEndDate);
                        Integer minusDays = (int) duration2.toDays() + 1;

                        LeaveBalance lb = staffRepository.findById(staffId).get().getLeaveBalance();

                        if (leaveToUpdate.getLeaveTypeEnum() == LeaveTypeEnum.ANNUAL) {
                            if (lb.getAnnualLeave() - minusDays + plusDays < 0) {
                                throw new InsufficientLeaveBalanceException("Insufficient annual leave balance.");
                            }
                            lb.setAnnualLeave(lb.getAnnualLeave() - minusDays + plusDays);
                        } else if (leaveToUpdate.getLeaveTypeEnum() == LeaveTypeEnum.SICK) {
                            if (lb.getSickLeave() - minusDays + plusDays < 0) {
                                throw new InsufficientLeaveBalanceException("Insufficient sick leave balance.");
                            }
                            lb.setSickLeave(lb.getSickLeave() - minusDays + plusDays);
                        } else {
                            if (lb.getParentalLeave() - minusDays + plusDays < 0) {
                                throw new InsufficientLeaveBalanceException("Insufficient parental leave balance.");
                            }
                            lb.setParentalLeave(lb.getParentalLeave() - minusDays + plusDays);
                        }


                leaveToUpdate.setStartDate(newStartDate);
                        leaveToUpdate.setEndDate(newEndDate);
                        leaveToUpdate.setComments(newComments);

                        leaveRepository.save(leaveToUpdate);
                        leaveBalanceRepository.save(lb);

                        return leaveToUpdate;
                    } else {
                        throw new LeaveOverlapException("Leave overlaps with existing leaves.");
                    }
                } else {
                    throw new InvalidDateRangeException("Insufficient leave balance.");
                }

        } else {
            throw new LeaveNotFoundException("Leave with ID " + leaveId + " not found.");
        }
    }
}

