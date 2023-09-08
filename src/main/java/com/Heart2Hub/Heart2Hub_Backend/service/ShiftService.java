package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.ShiftNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateShiftException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateStaffException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class ShiftService {


  private final ShiftRepository shiftRepository;
  private final StaffRepository staffRepository;

  public ShiftService(ShiftRepository shiftRepository, StaffRepository staffRepository) {
    this.shiftRepository = shiftRepository;
    this.staffRepository = staffRepository;
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

  public Shift createShift(String staffUsername, Shift newShift) throws UnableToCreateShiftException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot allocate shifts as he/she is not a head.");
    }
    try {
      LocalDateTime startTime = newShift.getStartTime();
      LocalDateTime endTime = newShift.getEndTime();
      if (startTime == null || endTime == null) {
        throw new UnableToCreateShiftException("Start time and end time must be present.");
      }
      Staff assignedStaff = staffRepository.findByUsername(staffUsername).get();
      List<Shift> shifts = shiftRepository.findShiftsByStaff(assignedStaff);
      if (startTime.isAfter(endTime)) {
        throw new UnableToCreateShiftException("Start time cannot be later than end time.");
      }
      if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
        throw new UnableToCreateShiftException("Shifts have to be allocated within the same day.");
      }
      long hours = startTime.until(endTime, ChronoUnit.HOURS);
      int MAXIMUM_SHIFT_HOURS = 12;
      int HOURS_BETWEEN_DAY_NIGHT_SHIFTS = 8;
      if (hours > MAXIMUM_SHIFT_HOURS) {
        throw new UnableToCreateShiftException("Staff exceeds maximum working hours per day at " + hours + " hours. (Maximum is " + MAXIMUM_SHIFT_HOURS + ").");
      }

      for (Shift shift : shifts) {
        if (startTime.toLocalDate().equals(shift.getStartTime().toLocalDate())) {
          throw new UnableToCreateShiftException("There is already a shift allocated to staff " + staffUsername + " on " + startTime.toLocalDate());
        }
        if (shift.getEndTime().compareTo(startTime) == -1 && shift.getEndTime().until(startTime, ChronoUnit.HOURS) < HOURS_BETWEEN_DAY_NIGHT_SHIFTS) {
          throw new UnableToCreateShiftException("Staff has worked a night shift the previous day, and cannot work the morning shift today.");
        }
        if (shift.getStartTime().compareTo(endTime) == 1 && endTime.until(shift.getStartTime(), ChronoUnit.HOURS) < HOURS_BETWEEN_DAY_NIGHT_SHIFTS) {
          throw new UnableToCreateShiftException("Staff has a day shift the following the day, and cannot work the night shift today.");
        }
      }

      // TO-DO: CHECK FOR LEAVES

      assignedStaff.getListOfShifts().add(newShift);
      newShift.setStaff(assignedStaff);
      staffRepository.save(assignedStaff);
      shiftRepository.save(newShift);
      return newShift;
    } catch (Exception ex) {
      throw new UnableToCreateShiftException(ex.getMessage());
    }
  }

  public List<Shift> getAllShiftsByRole(String role) throws ShiftNotFoundException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot view all shifts as he/she is not a head.");
    }
    try {
      RoleEnum roleEnum = RoleEnum.valueOf(role.toUpperCase());
      List<Staff> staffList = staffRepository.findByRoleEnum(roleEnum);
      List<Shift> listOfShifts = new ArrayList<>();
      for (Staff staff : staffList) {
        List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
        listOfShifts.addAll(shifts);
      }
      return listOfShifts;
    } catch (Exception ex) {
      throw new ShiftNotFoundException(ex.getMessage());
    }
  }

  public void deleteShift(Long shiftId) throws ShiftNotFoundException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot delete shifts as he/she is not a head.");
    }
    try {
      Optional<Shift> shiftOptional = shiftRepository.findById(shiftId);
      if (shiftOptional.isPresent()) {
        Shift shift = shiftOptional.get();
        Staff staff = shift.getStaff();
        staff.getListOfShifts().remove(shift);
        List<FacilityBooking> facilityBookingList = shift.getListOfFacilityBookings();
        shift.setListOfFacilityBookings(null);
        for (FacilityBooking fb : facilityBookingList) {
          if (Objects.equals(fb.getShift().getShiftId(), shiftId)) {
            fb.setShift(null);
          }
        }
        shiftRepository.delete(shift);
      } else {
        throw new ShiftNotFoundException("Shift with ID: " + shiftId + " is not found");
      }
    } catch (Exception ex) {
      throw new ShiftNotFoundException(ex.getMessage());
    }
  }

  public Shift updateShift(Long shiftId, Shift updatedShift) throws ShiftNotFoundException {
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
        shiftRepository.save(shift);
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
      throw new ShiftNotFoundException(ex.getMessage());
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
        return listOfShifts;
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " is not found.");
      }
    } catch (Exception ex) {
      throw new ShiftNotFoundException(ex.getMessage());
    }
  }
}
