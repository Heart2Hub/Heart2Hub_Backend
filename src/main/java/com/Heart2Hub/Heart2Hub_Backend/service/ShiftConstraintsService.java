package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.FacilityBooking;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftConstraintsRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class ShiftConstraintsService {


  private final ShiftConstraintsRepository shiftConstraintsRepository;

  public ShiftConstraintsService(ShiftConstraintsRepository shiftConstraintsRepository) {
    this.shiftConstraintsRepository = shiftConstraintsRepository;
  }

  public ShiftConstraints createShiftConstraints(ShiftConstraints newShiftConstraints) throws UnableToCreateShiftConstraintsException {
    try {
      ShiftConstraints shiftConstraints = new ShiftConstraints(newShiftConstraints.getStartTime(),
                                                                newShiftConstraints.getEndTime(),
                                                                newShiftConstraints.getMinPax(),
                                                                newShiftConstraints.getRoleEnum());
      List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndRoleEnumEquals(newShiftConstraints.getEndTime(), newShiftConstraints.getStartTime(), newShiftConstraints.getRoleEnum());
      if (!listOfShiftConstraints.isEmpty()) {
        throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + newShiftConstraints.getRoleEnum() + ".");
      }
      shiftConstraintsRepository.save(shiftConstraints);
      return shiftConstraints;
    } catch (Exception ex) {
      throw new UnableToCreateShiftConstraintsException(ex.getMessage());
    }
  }

  public List<ShiftConstraints> getAllShiftConstraintsByRole(String role) throws ShiftConstraintsNotFoundException {
    try {
      RoleEnum roleEnum = RoleEnum.valueOf(role.toUpperCase());
      return shiftConstraintsRepository.findByRoleEnum(roleEnum);
    } catch (Exception ex) {
      throw new ShiftConstraintsNotFoundException(ex.getMessage());
    }
  }

  public void deleteShiftConstraints(Long shiftConstraintsId) throws ShiftConstraintsNotFoundException {
    try {
      Optional<ShiftConstraints> shiftConstraintsOptional = shiftConstraintsRepository.findById(shiftConstraintsId);
      if (shiftConstraintsOptional.isPresent()) {
        ShiftConstraints sc = shiftConstraintsOptional.get();
        shiftConstraintsRepository.delete(sc);
      }
    } catch (Exception ex) {
      throw new ShiftConstraintsNotFoundException(ex.getMessage());
    }
  }
}
