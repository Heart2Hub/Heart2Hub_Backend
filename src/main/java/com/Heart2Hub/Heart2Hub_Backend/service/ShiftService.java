package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import com.Heart2Hub.Heart2Hub_Backend.entity.Leave;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityBookingRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.LeaveRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class ShiftService {


  private final ShiftRepository shiftRepository;
  private final StaffRepository staffRepository;
  private final FacilityBookingRepository facilityBookingRepository;

  private final LeaveRepository leaveRepository;
  private final FacilityBookingService facilityBookingService;

  public ShiftService(ShiftRepository shiftRepository, StaffRepository staffRepository, FacilityBookingService facilityBookingService, FacilityBookingRepository facilityBookingRepository, LeaveRepository leaveRepository) {
    this.shiftRepository = shiftRepository;
    this.staffRepository = staffRepository;
    this.facilityBookingService = facilityBookingService;
    this.facilityBookingRepository = facilityBookingRepository;
    this.leaveRepository = leaveRepository;
  }

  public boolean isLoggedInUserHead() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isHead = false;
    if (authentication != null) {
      User user = (User) authentication.getPrincipal();
      Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
      if (currStaff.isPresent()) {
        isHead = currStaff.get().getIsHead();
      }
    }
    return isHead;
  }

  public Shift createShift(String staffUsername, Long facilityId, Shift newShift) throws UnableToCreateShiftException, StaffNotFoundException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot allocate shifts as he/she is not a head.");
    }
    System.out.println("hey");
    try {
        Optional<Staff> optionalStaff = staffRepository.findByUsername(staffUsername);
        if (optionalStaff.isPresent()) {
          Staff assignedStaff = optionalStaff.get();
          if (checkShiftConditions(newShift, assignedStaff)) {
            assignedStaff.getListOfShifts().add(newShift);
            newShift.setStaff(assignedStaff);
            FacilityBooking fb = new FacilityBooking(newShift.getStartTime(), newShift.getEndTime(), "Shift for staff " + assignedStaff.getUsername());
            FacilityBooking newFacilityBooking = facilityBookingService.createBooking(fb, facilityId);
            newShift.setFacilityBooking(newFacilityBooking);
            newFacilityBooking.setShift(newShift);
            return shiftRepository.save(newShift);
          } else {
            throw new UnableToCreateShiftException("Shift does not meet predefined shift constraints");
          }
        } else {
          throw new StaffNotFoundException("Staff with username " + staffUsername + " not found!");
        }
    } catch (Exception ex) {
      throw new UnableToCreateShiftException(ex.getMessage());
    }
  }

  public boolean checkShiftConditions(Shift newShift, Staff assignedStaff) throws UnableToCreateShiftException, StaffNotFoundException {
    LocalDateTime startTime = newShift.getStartTime();
    LocalDateTime endTime = newShift.getEndTime();
    if (startTime == null || endTime == null) {
      throw new UnableToCreateShiftException("Start time and end time must be present.");
    }
    List<Shift> shifts = shiftRepository.findShiftsByStaff(assignedStaff);

    if (startTime.isAfter(endTime)) {
      throw new UnableToCreateShiftException("Start time cannot be later than end time.");
    }
    if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
      throw new UnableToCreateShiftException("Shifts have to be allocated within the same day.");
    }
    int HOURS_BETWEEN_DAY_NIGHT_SHIFTS = 8;
    int HOURS_AFTER_24HR_SHIFT = 24;
    long MAX_HOURS_PER_WEEK = 56;

    for (Shift shift : shifts) {
      if (newShift.getShiftId() == null || newShift.getShiftId() != shift.getShiftId()) {
        if (startTime.toLocalDate().equals(shift.getStartTime().toLocalDate())) {
          throw new UnableToCreateShiftException("There is already a shift allocated to staff " + assignedStaff.getUsername() + " on " + startTime.toLocalDate());
        }
        // If staff has a 24 hour shift
        if (shift.getStartTime().getHour() == 0 && shift.getStartTime().getMinute() == 0 && shift.getEndTime().getHour() == 23 && shift.getEndTime().getMinute() == 59) {
          if (shift.getEndTime().compareTo(startTime) == -1 && shift.getEndTime().until(startTime, ChronoUnit.HOURS) < HOURS_AFTER_24HR_SHIFT) {
            throw new UnableToCreateShiftException("Staff has worked a 24h shift the previous day, and cannot work on this day.");
          }
        }
        if (shift.getEndTime().compareTo(startTime) == -1 && shift.getEndTime().until(startTime, ChronoUnit.HOURS) < HOURS_BETWEEN_DAY_NIGHT_SHIFTS) {
          throw new UnableToCreateShiftException("Staff has worked a night shift the previous day, and cannot work the morning shift today.");
        }
        if (shift.getStartTime().compareTo(endTime) == 1 && endTime.until(shift.getStartTime(), ChronoUnit.HOURS) < HOURS_BETWEEN_DAY_NIGHT_SHIFTS) {
          throw new UnableToCreateShiftException("Staff has a day shift the following the day, and cannot work the night shift today.");
        }
      }
    }

    List<Shift> weeklyShifts = viewWeeklyRoster(assignedStaff.getUsername(), newShift.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    if (newShift.getShiftId() == null && weeklyShifts.size() == 6) {
      throw new UnableToCreateShiftException("Staff cannot work today as he/she requires at least 1 day of rest per week.");
    }
    long totalHours = 0;
    for (Shift shift : weeklyShifts) {
      totalHours += shift.getStartTime().until(shift.getEndTime(), ChronoUnit.HOURS);
    }
    totalHours += newShift.getStartTime().until(newShift.getEndTime(), ChronoUnit.HOURS);
    if (totalHours > MAX_HOURS_PER_WEEK) {
      throw new UnableToCreateShiftException("Staff cannot work today as he/she exceeds the maximum working hours per week (56 hours).");
    }

    // TODO: CHECK FOR LEAVES
    List<Leave> listOfLeaves = leaveRepository.findByStaff(assignedStaff);
    for (Leave leave : listOfLeaves) {
      if (startTime.isEqual(leave.getStartDate()) || startTime.isEqual(leave.getEndDate()) ||
              startTime.isAfter(leave.getStartDate()) && startTime.isBefore(leave.getEndDate())) {
        throw new UnableToCreateShiftException("Staff is on leave on this date.");
      }
    }

    return true;
  }

  public List<Shift> getAllShiftsByRole(String role) throws StaffRoleNotFoundException, UnableToCreateShiftException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot view all shifts as he/she is not a head.");
    }
    try {
      StaffRoleEnum staffRoleEnum = StaffRoleEnum.valueOf(role.toUpperCase());
      List<Staff> staffList = staffRepository.findByStaffRoleEnum(staffRoleEnum);
      List<Shift> listOfShifts = new ArrayList<>();
      for (Staff staff : staffList) {
        List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
        listOfShifts.addAll(shifts);
      }
      return listOfShifts;
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }

  public void deleteShift(Long shiftId) throws ShiftNotFoundException, UnableToCreateShiftException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot delete shifts as he/she is not a head.");
    }
    try {
      Optional<Shift> shiftOptional = shiftRepository.findById(shiftId);
      if (shiftOptional.isPresent()) {
        Shift shift = shiftOptional.get();
        Staff staff = shift.getStaff();
        staff.getListOfShifts().remove(shift);
        facilityBookingRepository.delete(shift.getFacilityBooking());
        shiftRepository.delete(shift);
      } else {
        throw new ShiftNotFoundException("Shift with ID: " + shiftId + " is not found");
      }
    } catch (Exception ex) {
      throw new ShiftNotFoundException(ex.getMessage());
    }
  }

  public Shift updateShift(Long shiftId, Long facilityId, Shift updatedShift) throws ShiftNotFoundException, UnableToCreateShiftException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot update shifts as he/she is not a head.");
    }
    try {
      Optional<Shift> shiftOptional = shiftRepository.findById(shiftId);
      if (shiftOptional.isPresent()) {
        Shift shift = shiftOptional.get();
        if (updatedShift.getStartTime() != null) shift.setStartTime(updatedShift.getStartTime());
        if (updatedShift.getEndTime() != null) shift.setEndTime(updatedShift.getEndTime());
        if (updatedShift.getComments() != null) shift.setComments(updatedShift.getComments());
        if (shift.getFacilityBooking().getFacility().getFacilityId() != facilityId) {
          facilityBookingService.updateBooking(shift.getFacilityBooking().getFacilityBookingId(), facilityId);
        }
        if (checkShiftConditions(shift, shift.getStaff())) {
          shiftRepository.save(shift);
        } else {
          throw new UnableToCreateShiftException("Shift does not meet predefined shift constraints");
        }
        return shift;
      } else {
        throw new ShiftNotFoundException("Shift with ID: " + shiftId + " is not found");
      }
    } catch (Exception ex) {
      throw new ShiftNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewMonthlyRoster(String username, Integer month, Integer year) throws StaffNotFoundException {
    try {
      Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
      if (optionalStaff.isPresent()) {
        Staff staff = optionalStaff.get();
        List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
        List<Shift> listOfShifts = new ArrayList<>();
        for (Shift shift : shifts) {
          if (shift.getStartTime().getMonthValue() == month && shift.getStartTime().getYear() == year) {
            listOfShifts.add(shift);
          }
        }
        return listOfShifts;
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " is not found.");
      }
    } catch (Exception ex) {
      throw new StaffNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewWeeklyRoster(String username, String date) throws StaffNotFoundException {
    try {
      Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
      if (optionalStaff.isPresent()) {
        Staff staff = optionalStaff.get();
        List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
        List<Shift> listOfShifts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
        for (Shift shift : shifts) {
          if (!shift.getStartTime().isBefore(startOfWeek) && !shift.getEndTime().isAfter(endOfWeek)) {
            listOfShifts.add(shift);
          }
        }
        listOfShifts.sort(Comparator.comparing(Shift::getStartTime));
        return listOfShifts;
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " is not found.");
      }
    } catch (Exception ex) {
      throw new StaffNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewDailyRoster(String date, String role) throws StaffRoleNotFoundException {
    try {
      StaffRoleEnum staffRoleEnum = StaffRoleEnum.valueOf(role.toUpperCase());
      LocalDateTime start = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      LocalDateTime end = LocalDateTime.parse(date + " 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      return shiftRepository.findByStaffStaffRoleEnumAndStartTimeBetween(staffRoleEnum, start, end);
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewOverallRoster(String username) throws StaffRoleNotFoundException {
    try {
      Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
      if (optionalStaff.isPresent()) {
        Staff staff = optionalStaff.get();
        return shiftRepository.findShiftsByStaff(staff);
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " is not found.");
      }
    } catch (Exception ex) {
      throw new StaffNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> getAllShiftsForStaffFromDates(String username, String start, String end) throws StaffRoleNotFoundException {
    try {
      LocalDateTime startDate = LocalDateTime.parse(start + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      LocalDateTime endDate = LocalDateTime.parse(end + " 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return shiftRepository.findByStaffUsernameAndStartTimeBetween(username, startDate, endDate);
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }
}
