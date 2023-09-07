package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateShiftException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateStaffException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShiftService {


  private final ShiftRepository shiftRepository;
  private final StaffRepository staffRepository;

  public ShiftService(ShiftRepository shiftRepository, StaffRepository staffRepository) {
    this.shiftRepository = shiftRepository;
    this.staffRepository = staffRepository;
  }

  public Shift createShift(String staffUsername, Shift newShift) {
    try {
      LocalDateTime startTime = newShift.getStartTime();
      LocalDateTime endTime = newShift.getEndTime();
      Staff assignedStaff = staffRepository.findByUsername(staffUsername).get();
      List<Shift> shifts = shiftRepository.findShiftByStaff(assignedStaff);
      if (startTime.isAfter(endTime)) {
        throw new UnableToCreateShiftException("Start time cannot be later than end time.");
      }
      if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
        throw new UnableToCreateShiftException("Shifts have to be allocated within the same day.");
      }
      long hours = startTime.until(endTime, ChronoUnit.HOURS );
      if (hours > 12) {
        throw new UnableToCreateShiftException("Staff exceeds maximum working hours per day at " + hours + " hours. (Maximum is 12)" );
      }

      for (Shift shift : shifts) {
        if (startTime.toLocalDate().equals(shift.getStartTime().toLocalDate())) {
          throw new UnableToCreateShiftException("There is already a shift allocated to staff " + staffUsername + " on " + startTime.toLocalDate());
        }
        if (shift.getEndTime().compareTo(startTime) == -1 && shift.getEndTime().until(startTime, ChronoUnit.HOURS) < 11) {
          throw new UnableToCreateShiftException("Staff has worked a night shift the previous day, and cannot work the morning shift today.");
        }
        if (shift.getStartTime().compareTo(endTime) == 1 && endTime.until(shift.getStartTime(), ChronoUnit.HOURS) < 11) {
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

}
