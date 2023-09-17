package com.Heart2Hub.Heart2Hub_Backend.data;

import com.Heart2Hub.Heart2Hub_Backend.Heart2HubBackendApplication;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.DepartmentService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.Heart2Hub.Heart2Hub_Backend.service.SubDepartmentService;
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
  private final DepartmentService departmentService;
  private final SubDepartmentService subDepartmentService;

  public DataLoader(StaffService staffService, DepartmentService departmentService, SubDepartmentService subDepartmentService) {
    this.staffService = staffService;
    this.departmentService = departmentService;
    this.subDepartmentService = subDepartmentService;
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
    createDepartmentData();
    createSubDepartmentData();
    createStaffData();

    //code ends here

    long endTime = System.currentTimeMillis();
    String message =
        "Time taken to Load Initial Test Data: " + (endTime - startTime) / 1000 + " seconds";
    logger.log(Level.INFO, message);
  }

  private void createDepartmentData() {
    departmentService.createDepartment("Cardiology");
    departmentService.createDepartment("Dermatology");
    departmentService.createDepartment("Emergency Medicine");
    departmentService.createDepartment("Gastroenterology");
    departmentService.createDepartment("General Surgery");
    departmentService.createDepartment("Geriatric Medicine");
    departmentService.createDepartment("Infectious Diseases");
    departmentService.createDepartment("Neurology");
    departmentService.createDepartment("Obstetrics and Gynaecology");
    departmentService.createDepartment("Orthopaedics");
    departmentService.createDepartment("Psychiatry");
    departmentService.createDepartment("Radiation Oncology");
    departmentService.createDepartment("Renal Medicine");
    departmentService.createDepartment("Urology");
  }

  private void createSubDepartmentData() {
    subDepartmentService.createSubDepartment("Cardio Clinic A", "Cardiology");
    subDepartmentService.createSubDepartment("Derma Clinic A", "Dermatology");
  }

  private void createStaffData() {
    Staff superAdmin = new Staff("elginchan", "password", "Elgin", "Chan", 90000000l, StaffRoleEnum.valueOf("ADMIN"));
    Staff doctor = new Staff("doctortest", "password", "Elgin", "Chan", 90000001l, StaffRoleEnum.valueOf("DOCTOR"));
    staffService.createStaff(superAdmin, "Cardio Clinic A");
    staffService.createStaff(doctor, "Cardio Clinic A");
  }

}

