package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftConstraintsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class ShiftConstraintsService {


  private final ShiftConstraintsRepository shiftConstraintsRepository;
  private final ShiftService shiftService;

  public ShiftConstraintsService(ShiftConstraintsRepository shiftConstraintsRepository, ShiftService shiftService) {
    this.shiftConstraintsRepository = shiftConstraintsRepository;
    this.shiftService = shiftService;
  }

  public ShiftConstraints createShiftConstraints(ShiftConstraints newShiftConstraints) throws UnableToCreateShiftConstraintsException {
    try {
      if (newShiftConstraints.getMinPax() < 1) {
        throw new UnableToCreateShiftConstraintsException("Min pax must be greater than or equal to 1");
      }
      ShiftConstraints shiftConstraints = new ShiftConstraints(newShiftConstraints.getStartTime(),
                                                                newShiftConstraints.getEndTime(),
                                                                newShiftConstraints.getMinPax(),
                                                                newShiftConstraints.getStaffRoleEnum());
      List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndStaffRoleEnumEquals(newShiftConstraints.getEndTime(), newShiftConstraints.getStartTime(), newShiftConstraints.getStaffRoleEnum());
      if (!listOfShiftConstraints.isEmpty()) {
        ShiftConstraints sc = listOfShiftConstraints.get(0);
        LocalTime shiftStart = LocalTime.of(0,0,0);
        LocalTime shiftEnd = LocalTime.of(23,59,0);
        if (!(sc.getStartTime().equals(shiftStart) && sc.getEndTime().equals(shiftEnd))) {
          throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + newShiftConstraints.getStaffRoleEnum() + ".");
        }
      }
      shiftConstraintsRepository.save(shiftConstraints);
      return shiftConstraints;
    } catch (Exception ex) {
      throw new UnableToCreateShiftConstraintsException(ex.getMessage());
    }
  }

  public List<ShiftConstraints> getAllShiftConstraintsByRole(String role) throws StaffRoleNotFoundException {
    try {
      return shiftConstraintsRepository.findByStaffRoleEnum(StaffRoleEnum.valueOf(role.toUpperCase()));
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
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
      if (updatedShiftConstraints.getMinPax() < 1) {
        throw new UnableToCreateShiftConstraintsException("Min pax must be greater than or equal to 1");
      }
      Optional<ShiftConstraints> shiftConstraintsOptional = shiftConstraintsRepository.findById(shiftConstraintsId);
      if (shiftConstraintsOptional.isPresent()) {
        ShiftConstraints sc = shiftConstraintsOptional.get();
        List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndStaffRoleEnumEquals(updatedShiftConstraints.getEndTime(), updatedShiftConstraints.getStartTime(), sc.getStaffRoleEnum());
        if (listOfShiftConstraints.isEmpty() || (listOfShiftConstraints.size() == 1 && listOfShiftConstraints.get(0).getShiftConstraintsId() == shiftConstraintsId)) {
          if (updatedShiftConstraints.getStartTime() != null) sc.setStartTime(updatedShiftConstraints.getStartTime());
          if (updatedShiftConstraints.getEndTime() != null) sc.setEndTime(updatedShiftConstraints.getEndTime());
          if (updatedShiftConstraints.getMinPax() != null) sc.setMinPax(updatedShiftConstraints.getMinPax());
          shiftConstraintsRepository.save(sc);
          return sc;
        } else {
          throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + sc.getStaffRoleEnum() + ".");
        }
      } else {
        throw new ShiftConstraintsNotFoundException("Shift with ID: " + shiftConstraintsId + " is not found");
      }
    } catch (Exception ex) {
      throw new ShiftConstraintsNotFoundException(ex.getMessage());
    }
  }

  public boolean isValidWorkDay(String role, String date) throws StaffRoleNotFoundException {
    try {
      List<Shift> listOfShifts = shiftService.viewDailyRoster(date, role);
      List<ShiftConstraints> listOfShiftConstraints = getAllShiftConstraintsByRole(role);
      HashMap<Long, Integer> mapOfMinPax = new HashMap<>();
      LocalTime shift1Start = LocalTime.of(0,0,0);
      LocalTime shift2Start = LocalTime.of(8,0,0);
      LocalTime shift3Start = LocalTime.of(16,0,0);
      LocalTime shift3End = LocalTime.of(23,59,0);
      for (ShiftConstraints sc : listOfShiftConstraints) {
        LocalTime start = sc.getStartTime();
        LocalTime end = sc.getEndTime();
        if (start.equals(shift1Start) && end.equals(shift2Start)) {
          mapOfMinPax.put(1L, mapOfMinPax.getOrDefault(1L, 0)+sc.getMinPax());
        } else if (start.equals(shift2Start) && end.equals(shift3Start)) {
          mapOfMinPax.put(2L, mapOfMinPax.getOrDefault(2L, 0)+sc.getMinPax());
        } else if (start.equals(shift3Start) && end.equals(shift3End)) {
          mapOfMinPax.put(3L, mapOfMinPax.getOrDefault(3L, 0)+sc.getMinPax());
        } else {
          mapOfMinPax.put(4L, mapOfMinPax.getOrDefault(4L, 0)+sc.getMinPax());
        }
      }
      for (Shift shift : listOfShifts) {
        LocalTime start = shift.getStartTime().toLocalTime();
        LocalTime end = shift.getEndTime().toLocalTime();
        if (start.equals(shift1Start) && end.equals(shift2Start)) {
          mapOfMinPax.put(1L, mapOfMinPax.getOrDefault(1L, 0)-1);
        } else if (start.equals(shift2Start) && end.equals(shift3Start)) {
          mapOfMinPax.put(2L, mapOfMinPax.getOrDefault(2L, 0)-1);
        } else if (start.equals(shift3Start) && end.equals(shift3End)) {
          mapOfMinPax.put(3L, mapOfMinPax.getOrDefault(3L, 0)-1);
        } else {
          mapOfMinPax.put(4L, mapOfMinPax.getOrDefault(4L, 0)-1);
        }
      }
      for (Map.Entry<Long,Integer> entry : mapOfMinPax.entrySet()) {
        if (entry.getValue() > 0) {
          System.out.println(entry.getKey() + " " + entry.getValue());
          return false;
        }
      }
      return true;
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }
}
