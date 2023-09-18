package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;

import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateStaffException;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final SubDepartmentRepository subDepartmentRepository;

  public StaffService(JwtService jwtService, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, StaffRepository staffRepository, SubDepartmentRepository subDepartmentRepository) {
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.staffRepository = staffRepository;
    this.subDepartmentRepository = subDepartmentRepository;
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

  public Staff createStaff(Staff newStaff, String subDepartmentName) {
      String password = newStaff.getPassword();
      newStaff.setPassword(passwordEncoder.encode(password));
      SubDepartment subDepartment = subDepartmentRepository.findByNameContainingIgnoreCase(subDepartmentName).get(0);
      newStaff.setSubDepartment(subDepartment);
      try {
          staffRepository.save(newStaff);
          return newStaff;
      } catch (Exception ex) {
          throw new UnableToCreateStaffException("Username already exists");
      }
  public Optional<Staff> findById(Long id) { return staffRepository.findById(id); }

  public Staff createStaff(String username, String password, String firstname, String lastname,
                           Long mobileNumber, StaffRoleEnum staffRoleEnum) {
    Staff newStaff = new Staff(username, passwordEncoder.encode(password), firstname, lastname, mobileNumber, staffRoleEnum);
    try {
      LeaveBalance balance = new LeaveBalance();
      newStaff.setLeaveBalance(balance);
      staffRepository.save(newStaff);
      return newStaff;
    } catch (Exception ex) {
      throw new UnableToCreateStaffException(ex.getMessage());
    }
  }

  public Staff createHeadStaff(String username, String password, String firstname, String lastname,
                           Long mobileNumber, StaffRoleEnum staffRoleEnum) {
    Staff newStaff = new Staff(username, passwordEncoder.encode(password), firstname, lastname, mobileNumber, staffRoleEnum);
    try {
      newStaff.setIsHead(true);
      LeaveBalance balance = new LeaveBalance();
      newStaff.setLeaveBalance(balance);
      staffRepository.save(newStaff);
      return newStaff;
    } catch (Exception ex) {
      throw new UnableToCreateStaffException(ex.getMessage());
    }
  }

  public Staff updateStaff(Staff updatedStaff, String subDepartmentName) {
      String username = updatedStaff.getUsername();
      Staff existingStaff = getStaffByUsername(username);
      existingStaff.setMobileNumber(updatedStaff.getMobileNumber());
      existingStaff.setIsHead(updatedStaff.getIsHead());
      SubDepartment subDepartment = subDepartmentRepository.findByNameContainingIgnoreCase(subDepartmentName).get(0);
      existingStaff.setSubDepartment(subDepartment);
      staffRepository.save(existingStaff);
      return existingStaff;
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
    return jwtService.generateToken(staff);
  }

  public Optional<Staff> getStaffByUsername(String username) {
    return staffRepository.findByUsername(username);
  }

  public Optional<List<Staff>> getAllHeadStaff() {
    List<Staff> newList = new ArrayList<>();
    List <Staff> allStaff = staffRepository.findAll();
      for (Staff staff : allStaff) {
          if (staff.getIsHead()) {
              newList.add(staff);
          }
      }
    return Optional.of(newList);
  }

  public List<String> getStaffRoles() {
      List<String> staffRolesString = new ArrayList<>();
      StaffRoleEnum[] staffRoles = StaffRoleEnum.values();
      for (StaffRoleEnum staffRole : staffRoles) {
          staffRolesString.add(staffRole.name());
      }

      return staffRolesString;
  }

  public List<Staff> getStaffByRole(String role) throws StaffRoleNotFoundException {
    try {
      StaffRoleEnum staffRoleEnum = StaffRoleEnum.valueOf(role.toUpperCase());
      return staffRepository.findByStaffRoleEnum(staffRoleEnum);
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException("Role " + role + " does not exist");
    }
  }
}
