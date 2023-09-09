package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftPreference;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftConstraintsRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftPreferenceRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ShiftPreferenceService {


  private final ShiftPreferenceRepository shiftPreferenceRepository;
  private final StaffRepository staffRepository;

  public ShiftPreferenceService(ShiftPreferenceRepository shiftPreferenceRepository, StaffRepository staffRepository) {
    this.shiftPreferenceRepository = shiftPreferenceRepository;
    this.staffRepository = staffRepository;
  }

  public ShiftPreference createShiftPreference(ShiftPreference newShiftPreference) throws UnableToCreateShiftPreferenceException, StaffNotFoundException {
    try {
      ShiftPreference shiftPreference = new ShiftPreference(newShiftPreference.getStartTime(), newShiftPreference.getEndTime());
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null) {
        User user = (User) authentication.getPrincipal();
        Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
        if (currStaff.isPresent()) {
          Staff staff = currStaff.get();
          ShiftPreference sp = staff.getShiftPreference();
          if (sp == null) {
            staff.setShiftPreference(shiftPreference);
            shiftPreferenceRepository.save(shiftPreference);
          } else {
            staff.getShiftPreference().setStartTime(newShiftPreference.getStartTime());
            staff.getShiftPreference().setEndTime(newShiftPreference.getEndTime());
          }
        } else {
          throw new StaffNotFoundException("Staff not found!");
        }
      }
      return shiftPreference;
    } catch (Exception ex) {
      throw new UnableToCreateShiftPreferenceException(ex.getMessage());
    }
  }

  public ShiftPreference getShiftPreference(String username) throws StaffNotFoundException, ShiftPreferenceNotFoundException {
    try {
      Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
      if (optionalStaff.isPresent()) {
        Staff staff = optionalStaff.get();
        return staff.getShiftPreference();
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " not found!");
      }
    } catch (Exception ex) {
      throw new ShiftPreferenceNotFoundException(ex.getMessage());
    }
  }

  public void deleteShiftPreference(Long shiftPreferenceId) throws ShiftPreferenceNotFoundException, StaffNotFoundException {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null) {
        User user = (User) authentication.getPrincipal();
        Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
        if (currStaff.isPresent()) {
          Staff staff = currStaff.get();
          Optional<ShiftPreference> optionalShiftPreference = shiftPreferenceRepository.findById(shiftPreferenceId);
          if (optionalShiftPreference.isPresent()) {
            ShiftPreference sp = optionalShiftPreference.get();
            staff.setShiftPreference(null);
            shiftPreferenceRepository.delete(sp);
          } else {
            throw new ShiftPreferenceNotFoundException("Shift preference with id " + shiftPreferenceId + " not found!");
          }
        } else {
          throw new StaffNotFoundException("Staff not found!");
        }
      }
    } catch (Exception ex) {
      throw new ShiftPreferenceNotFoundException("Shift preference with id " + shiftPreferenceId + " not found!");
    }
  }

}
