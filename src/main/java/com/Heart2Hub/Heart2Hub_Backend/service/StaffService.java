package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateStaffException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;

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
  //private final DepartmentRepository departmentRepository;
  private final DepartmentService departmentService;

  public StaffService(JwtService jwtService, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, StaffRepository staffRepository, DepartmentRepository departmentRepository) {
    this.jwtService = jwtService;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.staffRepository = staffRepository;
    //this.departmentRepository = departmentRepository;
    this.departmentService = new DepartmentService(departmentRepository);
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

  public Staff createStaff(String username, String password, String firstname, String lastname,
                           Long mobileNumber, StaffRoleEnum roleEnum, String departmentName) {

    try {
      Department department = departmentService.getDepartmentByName(departmentName);
      LeaveBalance leaveBalance = new LeaveBalance();
      Staff newStaff = new Staff(username, passwordEncoder.encode(password), firstname, lastname, mobileNumber, roleEnum, department, leaveBalance);
      staffRepository.save(newStaff);
      department.getListOfStaff().add(newStaff);
      return newStaff;
    } catch (DepartmentNotFoundException ex) {
      throw new UnableToCreateStaffException(ex.getMessage());
    }
  }

//  public Staff createStaff(Staff newStaff, String departmentName) {
//    try {
//      Department department = departmentRepository.findByDepartmentName(departmentName).get();
//      LeaveBalance leaveBalance = new LeaveBalance();
//
//    }
//  }

  //for authentication
  public String authenticateStaff(String username, String password) {
    //authenticate username and password, otherwise fails
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    //at this point, user is authenticated
    Staff staff = staffRepository.findByUsername(username)
        .orElseThrow(() -> new StaffNotFoundException("Staff not found"));
    return jwtService.generateToken(staff);
  }

  public Staff getStaffByUsername(String username) {
     Staff staff = staffRepository.findByUsername(username).orElseThrow(() -> new StaffNotFoundException("Username Does Not Exist."));
     return staff;
  }
}
