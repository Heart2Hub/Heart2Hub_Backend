package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToChangePasswordException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateStaffException;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
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

  public Staff createStaff(String username, String password, String firstname, String lastname,
      Long mobileNumber, StaffRoleEnum roleEnum) {
    Staff newStaff = new Staff(username, passwordEncoder.encode(password), firstname, lastname,
        mobileNumber, roleEnum);
    try {
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
    Staff staff = getStaffByUsername(username);
    return jwtService.generateToken(staff);
  }

  public Staff getStaffByUsername(String username) {
    Staff staff = staffRepository.findByUsername(username)
        .orElseThrow(() -> new StaffNotFoundException("Username Does Not Exist."));
    return staff;
  }

  //reset password
  public Boolean changePassword(String username, String oldPassword, String newPassword) throws UnableToChangePasswordException{
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
}
