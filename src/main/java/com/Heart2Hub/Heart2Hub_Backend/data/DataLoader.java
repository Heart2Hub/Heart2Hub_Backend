package com.Heart2Hub.Heart2Hub_Backend.data;

import com.Heart2Hub.Heart2Hub_Backend.Heart2HubBackendApplication;
import com.Heart2Hub.Heart2Hub_Backend.entity.LeaveBalance;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.RoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("loader")
@Transactional
public class DataLoader implements CommandLineRunner {

  static Logger logger = Heart2HubBackendApplication.logger;

  private final StaffService staffService;

  public DataLoader(StaffService staffService) {
    this.staffService = staffService;
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
    staffService.createStaff("staff1", "password1", "staff", "1", 90000001l, RoleEnum.ADMIN);
    staffService.createStaff("staff2", "password2", "staff", "2", 90000002l, RoleEnum.DOCTOR);
  }

}

