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

import javax.management.relation.Role;
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

  public List<ShiftConstraints> getAllShiftConstraintsByRole(String role) throws RoleNotFoundException {
    try {
      RoleEnum roleEnum = RoleEnum.valueOf(role.toUpperCase());
      return shiftConstraintsRepository.findByRoleEnum(roleEnum);
    } catch (Exception ex) {
      throw new RoleNotFoundException(ex.getMessage());
    }
  }

  public void deleteShiftConstraints(Long shiftConstraintsId) throws ShiftConstraintsNotFoundException {
    try {
      Optional<ShiftConstraints> shiftConstraintsOptional = shiftConstraintsRepository.findById(shiftConstraintsId);
      if (shiftConstraintsOptional.isPresent()) {
        ShiftConstraints sc = shiftConstraintsOptional.get();
        shiftConstraintsRepository.delete(sc);
      } else {
        throw new ShiftConstraintsNotFoundException("Shift constraints ID " + shiftConstraintsId + " not found!");
      }
    } catch (Exception ex) {
      throw new ShiftConstraintsNotFoundException(ex.getMessage());
    }
  }

  public ShiftConstraints updateShiftConstraints(Long shiftConstraintsId, ShiftConstraints updatedShiftConstraints) throws ShiftConstraintsNotFoundException, UnableToCreateShiftConstraintsException {
    try {
      Optional<ShiftConstraints> shiftConstraintsOptional = shiftConstraintsRepository.findById(shiftConstraintsId);
      if (shiftConstraintsOptional.isPresent()) {
        ShiftConstraints sc = shiftConstraintsOptional.get();
        List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndRoleEnumEquals(updatedShiftConstraints.getEndTime(), updatedShiftConstraints.getStartTime(), sc.getRoleEnum());
        if (listOfShiftConstraints.isEmpty() || (listOfShiftConstraints.size() == 1 && listOfShiftConstraints.get(0).getShiftConstraintsId() == shiftConstraintsId)) {
          if (updatedShiftConstraints.getStartTime() != null) sc.setStartTime(updatedShiftConstraints.getStartTime());
          if (updatedShiftConstraints.getEndTime() != null) sc.setEndTime(updatedShiftConstraints.getEndTime());
          if (updatedShiftConstraints.getMinPax() != null) sc.setMinPax(updatedShiftConstraints.getMinPax());
          shiftConstraintsRepository.save(sc);
          return sc;
        } else {
          throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + sc.getRoleEnum() + ".");
        }
      } else {
        throw new ShiftConstraintsNotFoundException("Shift with ID: " + shiftConstraintsId + " is not found");
      }
    } catch (Exception ex) {
      throw new ShiftConstraintsNotFoundException(ex.getMessage());
    }
  }
}
