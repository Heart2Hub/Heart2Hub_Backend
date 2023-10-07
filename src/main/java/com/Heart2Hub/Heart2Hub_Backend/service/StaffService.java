package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffDisabledException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffRoleNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToChangePasswordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateStaffException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.UnitRepository;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StaffService {

  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final StaffRepository staffRepository;
  private final UnitRepository unitRepository;

  private final DepartmentService departmentService;
  private final ImageDocumentService imageDocumentService;

  public StaffService(JwtService jwtService, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, StaffRepository staffRepository,
      UnitRepository unitRepository, DepartmentService departmentService,
      ImageDocumentService imageDocumentService) {
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.staffRepository = staffRepository;
    this.unitRepository = unitRepository;
    this.departmentService = departmentService;
    this.imageDocumentService = imageDocumentService;
  }

  public Long countStaff() {
    return staffRepository.count();
  }

  public List<Staff> getAllStaff() {
    return staffRepository.findAll();
  }

  public Optional<Staff> findByUsername(String username) {
    return staffRepository.findByUsername(username);
  }

//  public Staff createAdmin(String username, String password, String firstname, String lastname,
//                           Long mobileNumber, StaffStaffRoleEnum staffStaffRoleEnum, Boolean isHead) {
//    Staff newStaff = new Staff(username, passwordEncoder.encode(password), firstname, lastname, mobileNumber, StaffRoleEnum, isHead);
//    try {
//      LeaveBalance balance = new LeaveBalance();
//      SubDepartment subDepartment = new SubDepartment("");
//      newStaff.setLeaveBalance(balance);
//      newStaff.setSubDepartment(subDepartment);
//      staffRepository.save(newStaff);
//      return newStaff;
//    } catch (DepartmentNotFoundException ex) {
//      throw new UnableToCreateStaffException(ex.getMessage());
//    }
//  }

  public Staff createSuperAdmin(Staff superAdmin) {
    String password = superAdmin.getPassword();
    superAdmin.setPassword(passwordEncoder.encode(password));
    staffRepository.save(superAdmin);
    return superAdmin;
  }

  public Staff createStaff(Staff newStaff, String departmentName) {
    return createStaff(newStaff, departmentName, null);
  }

  public Staff createStaff(Staff newStaff, String departmentName, ImageDocument imageDocument) {
    if (newStaff.getIsHead() && newStaff.getStaffRoleEnum().toString().equals("ADMIN")) {
      throw new UnableToCreateStaffException("Cannot create another super admin");
    } else {
      String password = newStaff.getPassword();
      newStaff.setPassword(passwordEncoder.encode(password));
      Unit unit = unitRepository.findByNameContainingIgnoreCase(departmentName).get(0);
      newStaff.setUnit(unit);
      if (imageDocument != null) {
        ImageDocument createdImageDocument = imageDocumentService.createImageDocument(
            imageDocument);
        newStaff.setProfilePicture(createdImageDocument); // Set the image document if provided
      }

      try {
        staffRepository.save(newStaff);
        return newStaff;
      } catch (Exception ex) {
        throw new UnableToCreateStaffException("Username already exists");
      }
    }

  }

  public Staff findById(Long id) {
    return staffRepository.findById(id)
        .orElseThrow(() -> new StaffNotFoundException("Staff not found"));
  }

  public Staff updateStaff(Staff updatedStaff, String departmentName) {
    return updateStaff(updatedStaff, departmentName, null);
  }

  public Staff updateStaff(Staff updatedStaff, String departmentName, ImageDocument imageDocument) {
    if (updatedStaff.getIsHead() && updatedStaff.getStaffRoleEnum().toString().equals("ADMIN")) {
      throw new UnableToCreateStaffException("Cannot create another super admin");
    } else {
      String username = updatedStaff.getUsername();
      Staff existingStaff = getStaffByUsername(username);
      existingStaff.setMobileNumber(updatedStaff.getMobileNumber());
      existingStaff.setIsHead(updatedStaff.getIsHead());
      existingStaff.setStaffRoleEnum(updatedStaff.getStaffRoleEnum());
      Unit unit = unitRepository.findByNameContainingIgnoreCase(departmentName).get(0);
      existingStaff.setUnit(unit);

      if (imageDocument != null) {
        ImageDocument createdImageDocument = imageDocumentService.createImageDocument(
            imageDocument);
        existingStaff.setProfilePicture(createdImageDocument); // Set the image document if provided
      }

      staffRepository.save(existingStaff);
      return existingStaff;
    }
  }

  public Staff disableStaff(String username) {
    Staff existingStaff = getStaffByUsername(username);
    existingStaff.setDisabled(true);
    staffRepository.save(existingStaff);
    return existingStaff;
  }

  //for authentication
  public String authenticateStaff(String username, String password) {
    //authenticate username and password, otherwise fails
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    //at this point, user is authenticated
    Staff staff = staffRepository.findByUsername(username)
        .orElseThrow(() -> new StaffNotFoundException("Staff not found"));
    if (staff.getDisabled()) {
      throw new StaffDisabledException("Staff is disabled");
    }
    return jwtService.generateToken(staff);
  }

  public Staff getStaffByUsername(String username) {
    Staff staff = staffRepository.findByUsername(username)
        .orElseThrow(() -> new StaffNotFoundException("Username Does Not Exist."));
    return staff;
  }

  public List<Staff> getAllHeadStaff() {
    List<Staff> newList = new ArrayList<>();
    List<Staff> allStaff = staffRepository.findAll();
    for (Staff staff : allStaff) {
      if (staff.getIsHead()) {
        newList.add(staff);
      }
    }
    return Optional.of(newList).get();
  }

  public List<String> getStaffRoles() {
    List<String> staffRolesString = new ArrayList<>();
    StaffRoleEnum[] staffRoles = StaffRoleEnum.values();
    for (StaffRoleEnum staffRole : staffRoles) {
      staffRolesString.add(staffRole.name());
    }

    return staffRolesString;
  }

  public List<Staff> getStaffByRole(String role, String unit) throws StaffRoleNotFoundException {
    try {
      StaffRoleEnum staffRoleEnum = StaffRoleEnum.valueOf(role.toUpperCase());
      return staffRepository.findByStaffRoleEnumAndUnitNameEqualsIgnoreCase(staffRoleEnum, unit);
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException("Role " + role + " does not exist");
    }
  }

  //reset password
  public Boolean changePassword(String username, String oldPassword, String newPassword)
      throws UnableToChangePasswordException {
    Staff staff = getStaffByUsername(username);
    if (passwordEncoder.matches(oldPassword, staff.getPassword())) {
      if (newPassword.length() > 6) {
        try {
          staff.setPassword(passwordEncoder.encode(newPassword));
          return Boolean.TRUE;
        } catch (Exception ex) {
          throw new UnableToChangePasswordException("New Password already in use");
        }
      } else {
        throw new UnableToChangePasswordException("New Password provided is too short");
      }
    } else {
      throw new UnableToChangePasswordException("Old Password provided is Incorrect");
    }
  }

  public List<Shift> getStaffsWorkingInCurrentShiftAndDepartment(String departmentName) {

    List<Staff> listOfStaff = staffRepository.findByUnitName(departmentName);
    LocalDateTime now = LocalDateTime.now();
    List<Shift> listOfRelatedShifts = new ArrayList<>();
    for (Staff staff : listOfStaff) {
      for (Shift shift: staff.getListOfShifts()) {
        //check if staff working now
        if (shift.getStartTime().isBefore(now) && shift.getEndTime().isAfter(now)) {
          listOfRelatedShifts.add(shift);
          break;
        }
      }
    }
    return listOfRelatedShifts;
  }

  public Staff getStaffById(Long id) {
    Staff staff = staffRepository.findById(id)
            .orElseThrow(() -> new StaffNotFoundException("Username Does Not Exist."));
    return staff;
  }
}
