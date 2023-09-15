package com.Heart2Hub.Heart2Hub_Backend.data;

import com.Heart2Hub.Heart2Hub_Backend.Heart2HubBackendApplication;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.LeaveService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component("loader")
@Transactional
public class DataLoader implements CommandLineRunner {

  static Logger logger = Heart2HubBackendApplication.logger;

  private final StaffService staffService;
  private final LeaveService leaveService;

  public DataLoader(StaffService staffService, LeaveService leaveService) {

    this.staffService = staffService;
    this.leaveService = leaveService;
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
    createStaffData();

    //code ends here

    long endTime = System.currentTimeMillis();
    String message =
        "Time taken to Load Initial Test Data: " + (endTime - startTime) / 1000 + " seconds";
    logger.log(Level.INFO, message);
  }

  private void createStaffData() {
    Staff staff1 =  staffService.createStaff("staff1", "password1", "staff", "1", 90000001l, RoleEnum.ADMIN);
    Staff staff2 = staffService.createStaff("staff2", "password2", "staff", "2", 90000002l, RoleEnum.DOCTOR);
    Staff staff3 = staffService.createHeadStaff("staff3", "password3", "staff", "3", 90000002l, RoleEnum.ADMIN);
    Staff staff4 = staffService.createHeadStaff("staff4", "password4", "staff", "4", 90000002l, RoleEnum.ADMIN);

    leaveService.createLeave(LocalDateTime.now().plusMonths(2),
            LocalDateTime.now().plusMonths(2).plusDays(3), LeaveTypeEnum.ANNUAL, staff1, staff2
            );
    leaveService.createLeave(LocalDateTime.now().plusMonths(3),
            LocalDateTime.now().plusMonths(3).plusDays(3), LeaveTypeEnum.SICK, staff1, staff2
    );
  }

}

