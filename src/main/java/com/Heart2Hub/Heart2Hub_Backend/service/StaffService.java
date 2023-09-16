package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateStaffException;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

  public StaffService(JwtService jwtService, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, StaffRepository staffRepository) {
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.staffRepository = staffRepository;
  }

  public Long countStaff() {
    return staffRepository.count();
  }

  public Optional<Staff> findByUsername(String username) {
    return staffRepository.findByUsername(username);
  }

  public Optional<Staff> findById(Long id) { return staffRepository.findById(id); }

  public Staff createStaff(String username, String password, String firstname, String lastname,
                           Long mobileNumber, StaffRoleEnum roleEnum) {
    Staff newStaff = new Staff(username, passwordEncoder.encode(password), firstname, lastname, mobileNumber, roleEnum);
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
                           Long mobileNumber, RoleEnum roleEnum) {
    Staff newStaff = new Staff(username, passwordEncoder.encode(password), firstname, lastname, mobileNumber, roleEnum);
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
}
