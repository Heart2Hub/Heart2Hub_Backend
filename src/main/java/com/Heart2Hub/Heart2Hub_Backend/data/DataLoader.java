package com.Heart2Hub.Heart2Hub_Backend.data;

import com.Heart2Hub.Heart2Hub_Backend.Heart2HubBackendApplication;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.DepartmentService;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityBookingService;
import com.Heart2Hub.Heart2Hub_Backend.service.ShiftService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component("loader")
@Transactional
public class DataLoader implements CommandLineRunner {

  static Logger logger = Heart2HubBackendApplication.logger;

  private final StaffService staffService;
  private final ShiftService shiftService;
  private final DepartmentService departmentService;
  private final FacilityBookingService facilityBookingService;

  private final AuthenticationManager authenticationManager;

  public DataLoader(StaffService staffService, DepartmentService departmentService, FacilityBookingService facilityBookingService, AuthenticationManager authenticationManager, ShiftService shiftService) {

    this.staffService = staffService;
    this.departmentService = departmentService;
    this.facilityBookingService = facilityBookingService;
    this.shiftService = shiftService;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public void run(String... args) {
    if (staffService.countStaff() == 0) {
      loadData();
    }
  }

  public void loadData() {
    long startTime = System.currentTimeMillis();
    //code starts here
    createDepartmentFacility();
    createStaffData();

    Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("staff1", "password1"));
    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);

    createShiftData();

    //code ends here

    long endTime = System.currentTimeMillis();
    String message =
        "Time taken to Load Initial Test Data: " + (endTime - startTime) / 1000 + " seconds";
    logger.log(Level.INFO, message);
  }

  private void createDepartmentFacility() {
    Department d = departmentService.createDepartment("A&E");
    Long sd = departmentService.createSubDepartment("A&E Clinic 1", "Clinic 1 of A&E", d.getDepartmentId());
    Long facility1 = facilityBookingService.createFacility(new Facility("Operating Room", "1st Floor", "Operations done here", 5, FacilityStatusEnum.OPERATING_ROOM, FacilityTypeEnum.AVAILABLE), sd);
    Long facility2 = facilityBookingService.createFacility(new Facility("Emergency Room", "2nd Floor", "Emergencies done here", 10, FacilityStatusEnum.EMERGENCY_ROOM, FacilityTypeEnum.AVAILABLE), sd);
  }
  private void createStaffData() {
    staffService.createStaff("staff1", "password1", "staff", "1", 90000001l, RoleEnum.ADMIN, true);
    staffService.createStaff("staff2", "password2", "staff", "2", 90000002l, RoleEnum.DOCTOR, true);
    staffService.createStaff("staff3", "password3", "Beow", "Tan", 90000002l, RoleEnum.DOCTOR, false);
    staffService.createStaff("staff4", "password4", "Erling", "Haaland", 90000002l, RoleEnum.DOCTOR, false);
    staffService.createStaff("staff5", "password5", "Uncle", "Raymond", 90000002l, RoleEnum.DOCTOR, false);
  }

  private void createShiftData() {
    // Get the current date and time
    LocalDateTime currentDate = LocalDateTime.now();

    // Calculate the date of the Monday of the current week
    LocalDateTime monday = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    for (int i=0; i<6; i++) {
      LocalDateTime currentDateTime = monday.plusDays(i);
      int day = currentDateTime.getDayOfMonth();
      int month = currentDateTime.getMonthValue();
      int year = currentDateTime.getYear();
      String[] staffList = {"staff2", "staff3", "staff4", "staff5"};
      for (int j=0; j<staffList.length; j++) {
        String username = staffList[j];
        if (j % 2 == 0){
          shiftService.createShift(username, 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0), LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
        } else {
          shiftService.createShift(username, 1L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0), LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
        }
      }
    }
  }

}

