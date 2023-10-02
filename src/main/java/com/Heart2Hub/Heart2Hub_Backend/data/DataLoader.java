package com.Heart2Hub.Heart2Hub_Backend.data;

import com.Heart2Hub.Heart2Hub_Backend.Heart2HubBackendApplication;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.LeaveService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.*;
import com.Heart2Hub.Heart2Hub_Backend.service.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

@Component("loader")
@Transactional
public class DataLoader implements CommandLineRunner {

  static Logger logger = Heart2HubBackendApplication.logger;

  private final StaffService staffService;
  private final ShiftService shiftService;
  private final DepartmentService departmentService;
  private final AuthenticationManager authenticationManager;
  //  private final SubDepartmentService subDepartmentService;
  private final FacilityService facilityService;
  private final PatientService patientService;
  private final NextOfKinRecordService nextOfKinRecordService;
  private final PrescriptionRecordService prescriptionRecordService;
  private final ProblemRecordService problemRecordService;
  private final MedicalHistoryRecordService medicalHistoryRecordService;
  private final TreatmentPlanRecordService treatmentPlanRecordService;
  private final LeaveService leaveService;
  private final ShiftConstraintsService shiftConstraintsService;

  private final ConsumableEquipmentService consumableEquipmentService;

  private final AllocatedInventoryService allocatedInventoryService;

  private final SubDepartmentRepository subDepartmentRepository;
    private final DepartmentRepository departmentRepository;
    private final WardService wardService;
    private final WardClassService wardClassService;
  //  private final SubDepartmentRepository subDepartmentRepository;

  private final AppointmentService appointmentService;

  public DataLoader(StaffService staffService, ShiftService shiftService, DepartmentService departmentService, AuthenticationManager authenticationManager, FacilityService facilityService, PatientService patientService, NextOfKinRecordService nextOfKinRecordService, PrescriptionRecordService prescriptionRecordService, ProblemRecordService problemRecordService, MedicalHistoryRecordService medicalHistoryRecordService, TreatmentPlanRecordService treatmentPlanRecordService, LeaveService leaveService, ShiftConstraintsService shiftConstraintsService, ConsumableEquipmentService consumableEquipmentService, AllocatedInventoryService allocatedInventoryService, SubDepartmentRepository subDepartmentRepository, DepartmentRepository departmentRepository, WardService wardService, WardClassService wardClassService, AppointmentService appointmentService) {
    this.staffService = staffService;
    this.shiftService = shiftService;
    this.departmentService = departmentService;
    this.authenticationManager = authenticationManager;
    this.facilityService = facilityService;
    this.patientService = patientService;
    this.nextOfKinRecordService = nextOfKinRecordService;
    this.prescriptionRecordService = prescriptionRecordService;
    this.problemRecordService = problemRecordService;
    this.medicalHistoryRecordService = medicalHistoryRecordService;
    this.treatmentPlanRecordService = treatmentPlanRecordService;
    this.leaveService = leaveService;
    this.shiftConstraintsService = shiftConstraintsService;
    this.consumableEquipmentService = consumableEquipmentService;
    this.allocatedInventoryService = allocatedInventoryService;
    this.subDepartmentRepository = subDepartmentRepository;
    this.departmentRepository = departmentRepository;
    this.wardService = wardService;
    this.wardClassService = wardClassService;
    this.appointmentService = appointmentService;
  }

  @Override
  public void run(String... args) {
    if (staffService.countStaff() == 0) {
      loadData();
    }
  }

  public void loadData() {
    long startTime = System.currentTimeMillis();

    // Create staff data
    Staff admin = new Staff("staff1", "password1", "Elgin", "Chan", 97882145l,
            StaffRoleEnum.valueOf("ADMIN"), true);
    Staff superAdmin = staffService.createSuperAdmin(admin);
    System.out.println(superAdmin.getUsername());

    // Set auth context using staff1
    Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken("staff1", "password1"));
    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);

    // Create other data
    createDepartmentData();
//    createSubDepartmentData();
    createFacilityData();
    createStaffData();
    createShiftData();
    createPatientData();
    createAppointmentData();
    createConsumableEquipmentData();

    //code ends here

    long endTime = System.currentTimeMillis();
    String message =
        "Time taken to Load Initial Test Data: " + (endTime - startTime) / 1000 + " seconds";
    logger.log(Level.INFO, message);
  }

  private void createStaffData() {
    LocalDateTime lt = LocalDateTime.now();
    Staff staff2 = staffService.createStaff(
        new Staff("staff2", "password2", "Tharman", "Shanmugaratnam", 93860982l,
            StaffRoleEnum.DOCTOR, true), "Emergency Medicine", new ImageDocument("id1.png", lt));
    Staff staff3 = staffService.createStaff(
        new Staff("staff3", "password3", "Beow", "Tan", 89645629l, StaffRoleEnum.DOCTOR, false),
        "Emergency Medicine", new ImageDocument("id2.png", lt));
    Staff staff4 = staffService.createStaff(
        new Staff("staff4", "password4", "Erling", "Haaland", 93490928l, StaffRoleEnum.DOCTOR,
            false), "Emergency Medicine", new ImageDocument("id3.png", lt));
    Staff staff5 = staffService.createStaff(
        new Staff("staff5", "password5", "John", "Wick", 87609870l, StaffRoleEnum.DOCTOR, true),
        "Cardiology", new ImageDocument("id4.png", lt));
    staffService.createStaff(
        new Staff("staff6", "password6", "Donald", "Raymond", 96997125l, StaffRoleEnum.DOCTOR,
            false), "Cardiology", new ImageDocument("id5.png", lt));
    staffService.createStaff(
        new Staff("staff7", "password7", "Steven", "Lim", 98762093l, StaffRoleEnum.DOCTOR, false),
        "Cardiology", new ImageDocument("id6.png", lt));
    staffService.createStaff(
        new Staff("staff8", "password8", "Kurt", "Tay", 80182931l, StaffRoleEnum.NURSE, true),
        "Cardiology", new ImageDocument("id7.png", lt));
    staffService.createStaff(
        new Staff("staff9", "password9", "Simon", "Cowell", 81927493l, StaffRoleEnum.NURSE, false),
        "Cardiology", new ImageDocument("id8.png", lt));
    staffService.createStaff(
            new Staff("staff10", "password10", "James", "Charles", 93420093l, StaffRoleEnum.NURSE,
                    true), "B20", new ImageDocument("id9.png", lt));
    staffService.createStaff(
            new Staff("staff11", "password11", "Ronald", "Weasley", 90897321l, StaffRoleEnum.NURSE,
                    false), "B20", new ImageDocument("id10.png", lt));

    leaveService.createLeave(LocalDateTime.now().plusMonths(3),
            LocalDateTime.now().plusMonths(3).plusDays(2), LeaveTypeEnum.ANNUAL, staff3, staff2,
            "Going to see F1 race"
    );
    leaveService.createLeave(LocalDateTime.now().plusMonths(2),
            LocalDateTime.now().plusMonths(2).plusDays(3), LeaveTypeEnum.ANNUAL, staff4, staff2,
            "Thailand family trip"
    );
    leaveService.createLeave(LocalDateTime.now().plusMonths(3),
            LocalDateTime.now().plusMonths(3).plusDays(2), LeaveTypeEnum.ANNUAL, staff5, staff2,
            "Exam for my Master's degree"
    );
  }

  private void createDepartmentData() {
    System.out.println();
    departmentService.createDepartment(new Department("Cardiology"));
    departmentService.createDepartment(new Department("Orthopedics"));
    departmentService.createDepartment(new Department("Pediatrics"));
    departmentService.createDepartment(new Department("Neurology"));
    departmentService.createDepartment(new Department("Emergency Medicine"));
    departmentService.createDepartment(new Department("Surgery"));
    departmentService.createDepartment(new Department("Ophthalmology"));
    departmentService.createDepartment(new Department("Psychiatry"));
    departmentService.createDepartment(new Department("Postanal Recovery"));
    departmentService.createDepartment(new Department("Admin"));

//    TO-DO: WARD CREATION
    wardClassService.createWardClass(new WardClass("A", new BigDecimal("621"), 1));
    wardClassService.createWardClass(new WardClass("B1", new BigDecimal("309.31"), 4));
    wardClassService.createWardClass(new WardClass("B2", new BigDecimal("57"), 6));
    wardClassService.createWardClass(new WardClass("C", new BigDecimal("40.70"), 8));

    wardService.createWard(new Ward("A10", "Block A", 1), "A");
    wardService.createWard(new Ward("B10", "Block B", 4), "B");
    wardService.createWard(new Ward("B20", "Block B", 6), "B");
//    departmentService.createDepartment(new Department("Ward B-1"));
//    departmentService.createDepartment(new Department("Ward B-2"));
//    departmentService.createDepartment(new Department("Ward B-3"));
//    departmentService.createDepartment(new Department("Ward C-1"));
//    departmentService.createDepartment(new Department("Ward C-2"));
//    departmentService.createDepartment(new Department("Ward C-3"));
  }

  private void createSubDepartmentData() {
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Interventional Cardiology", "Specializes in minimally invasive procedures to treat heart conditions"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Electrophysiology (EP) Lab", "Focuses on the diagnosis and treatment of heart rhythm disorders, including procedures like cardiac ablation"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Catheterization Lab", "Performs diagnostic procedures like cardiac catheterization and coronary angiography"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Heart Failure Clinic", "Provides comprehensive care for patients with congestive heart failure, including medication management and lifestyle counseling"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Rehabilitation", "Offers programs to help patients recover from heart-related surgeries or events through exercise and lifestyle modifications"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Echocardiography Unit", "Specializes in using echocardiograms (ultrasound of the heart) for diagnostic purposes"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Nuclear Cardiology Unit", "Utilizes nuclear imaging techniques to assess blood flow to the heart and cardiac function"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Imaging Center", "Provides various imaging services, including MRI and CT scans, for detailed cardiac assessments"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Telemetry Unit", "Monitors patients' heart rhythms continuously, often used in critical care settings"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Adult Congenital Heart Disease Clinic", "Focuses on the care of adults born with congenital heart defects"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Preventive Cardiology Clinic", "Emphasizes heart disease prevention through risk assessment, lifestyle changes, and medication when necessary"));
//    subDepartmentService.createSubDepartment(1L, new SubDepartment("Heart Transplantation Unit", "Manages patients in need of heart transplants and provides post-transplant care"));
//    subDepartmentService.createSubDepartment(2L, new SubDepartment("Orthopedic Surgery", "Specializes in surgical treatments for musculoskeletal conditions"));
//    subDepartmentService.createSubDepartment(2L, new SubDepartment("Physical Therapy", "Provides rehabilitation and physical therapy services for orthopedic patients"));
//    subDepartmentService.createSubDepartment(2L, new SubDepartment("Sports Medicine", "Focuses on injuries related to sports and physical activity"));
//    subDepartmentService.createSubDepartment(2L, new SubDepartment("Orthopedic Trauma Center", "Manages fractures and severe musculoskeletal injuries"));
//    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Cardiology", "Specializes in heart conditions in children"));
//    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Neurology", "Focuses on neurological conditions in children"));
//    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Oncology", "Treats cancer and blood disorders in children"));
//    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Emergency Care", "Provides emergency medical care for children"));
//    subDepartmentService.createSubDepartment(4L, new SubDepartment("Neurophysiology Lab", "Performs diagnostic tests to assess nervous system function"));
//    subDepartmentService.createSubDepartment(4L, new SubDepartment("Neurocritical Care Unit", "Specializes in the intensive care of neurological patients"));
//    subDepartmentService.createSubDepartment(4L, new SubDepartment("Stroke Center", "Provides advanced care for stroke patients"));
//    subDepartmentService.createSubDepartment(4L, new SubDepartment("Neuropsychology Clinic", "Evaluates cognitive and behavioral aspects of neurological disorders"));
//    subDepartmentService.createSubDepartment(5L, new SubDepartment("Emergency Room (ER)", "Offers immediate medical care for various emergencies"));
//    subDepartmentService.createSubDepartment(5L, new SubDepartment("Trauma Center", "Handles severe injuries and trauma cases"));
//    subDepartmentService.createSubDepartment(5L, new SubDepartment("Toxicology Unit", "Manages poisoning and overdose cases"));
//    subDepartmentService.createSubDepartment(5L, new SubDepartment("Emergency Psychiatry", "Addresses psychiatric emergencies in the ER"));
//    subDepartmentService.createSubDepartment(6L, new SubDepartment("General Surgery", "Performs a wide range of surgical procedures"));
//    subDepartmentService.createSubDepartment(6L, new SubDepartment("Cardiothoracic Surgery", "Specializes in heart and lung surgeries"));
//    subDepartmentService.createSubDepartment(6L, new SubDepartment("Plastic and Reconstructive Surgery", "Focuses on cosmetic and reconstructive procedures"));
//    subDepartmentService.createSubDepartment(6L, new SubDepartment("Minimally Invasive Surgery", "Utilizes minimally invasive techniques for surgery"));
//    subDepartmentService.createSubDepartment(7L, new SubDepartment("Ophthalmic Surgery", "Performs surgeries related to the eyes"));
//    subDepartmentService.createSubDepartment(7L, new SubDepartment("Retina Clinic", "Specializes in diseases of the retina and vitreous"));
//    subDepartmentService.createSubDepartment(7L, new SubDepartment("Cornea and External Disease Unit", "Focuses on corneal and external eye conditions"));
//    subDepartmentService.createSubDepartment(7L, new SubDepartment("Pediatric Ophthalmology", "Provides eye care for children"));
//    subDepartmentService.createSubDepartment(8L, new SubDepartment("Adult Psychiatry", "Treats mental health conditions in adults"));
//    subDepartmentService.createSubDepartment(8L, new SubDepartment("Child and Adolescent Psychiatry", "Focuses on mental health care for children and teens"));
//    subDepartmentService.createSubDepartment(8L, new SubDepartment("Addiction Psychiatry", "Addresses substance abuse and addiction disorders"));
//    subDepartmentService.createSubDepartment(8L, new SubDepartment("Forensic Psychiatry", "Deals with mental health in the legal context"));
//    TO-DO: WARD ROOM CREATION
//    for (long L = 9L; L <= 17L; L++) {
//      subDepartmentService.createSubDepartment(L, new SubDepartment("Room 1", ""));
//      subDepartmentService.createSubDepartment(L, new SubDepartment("Room 2", ""));
//      subDepartmentService.createSubDepartment(L, new SubDepartment("Room 3", ""));
//    }
  }

  private void createFacilityData() {
    // For Sub Department Facility Creation
    for (long L = 1L; L <= 9L; L++) {
      facilityService.createFacility(L,
              new Facility("Consultation Room 1 " + departmentRepository.findById(L).get().getName(),
                      "Level 1", "", 2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.CONSULTATION_ROOM));
      facilityService.createFacility(L,
              new Facility("Triage Room 1 " + departmentRepository.findById(L).get().getName(),
                      "Level 2", "", 2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.TRIAGE_ROOM));
      facilityService.createFacility(L,
              new Facility("Triage Room 2 " + departmentRepository.findById(L).get().getName(),
                      "Level 3", "", 2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.TRIAGE_ROOM));
    }
//    TO-DO: WARD BED CREATION
//    for (long L = 1L; L <= 40L; L++) {
//      facilityService.createFacility(L, new Facility("Consultation Room 1", "","",2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.CONSULTATION_ROOM));
//      facilityService.createFacility(L, new Facility("Bed 1", "","",2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.WARD_BED));
//    }
  }

  private void createShiftData() {
    // Get the current date and time
    LocalDateTime currentDate = LocalDateTime.now();

    // Calculate the date of the Monday of the current week
    LocalDateTime monday = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

    // EM Head doctor -> staff2 (staff3, staff4)
    // Cardiology Head doctor -> staff5 (staff6, staff 7)
    // Cardiology Head nurse -> staff8 (staff9)
    // Ward B20 Head nurse -> staff10 (staff11)

    // Allocate Monday shifts
    LocalDateTime currentDateTime = monday.plusDays(0);
    int day = currentDateTime.getDayOfMonth();
    int month = currentDateTime.getMonthValue();
    int year = currentDateTime.getYear();

    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff3", 2L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
    shiftService.createShift("staff4", 3L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Tuesday
    currentDateTime = monday.plusDays(1);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 1L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
    shiftService.createShift("staff3", 2L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
    shiftService.createShift("staff4", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Wednesday
    currentDateTime = monday.plusDays(2);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff3", 2L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
    shiftService.createShift("staff4", 3L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Thursday
    currentDateTime = monday.plusDays(3);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 1L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
    shiftService.createShift("staff3", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff4", 3L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working 24hr shift"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));


    // Friday
    currentDateTime = monday.plusDays(4);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff3", 2L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Saturday
    currentDateTime = monday.plusDays(5);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 1L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
    shiftService.createShift("staff3", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff4", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Sunday
    currentDateTime = monday.plusDays(6);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff6", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Generate cardiology doctor shifts for the next 2 weeks
    for (int i=7; i<=13; i++) {
      currentDateTime = monday.plusDays(i);
      day = currentDateTime.getDayOfMonth();
      month = currentDateTime.getMonthValue();
      year = currentDateTime.getYear();
      System.out.println(currentDateTime);
      // Cardiology doctor shifts - Working hours (8am - 4pm)
      if (i != 11) {
        shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
      }
      if (i != 12) {
        shiftService.createShift("staff6", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
      }
      if (i != 13) {
        shiftService.createShift("staff7", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
      }
    }

    shiftConstraintsService.createShiftConstraints(
        new ShiftConstraints(LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), 2,
            StaffRoleEnum.DOCTOR), "Consultation Room 1 Cardiology");

  }

  private void createPatientData() {
      Patient newPatient1 = patientService.createPatient(new Patient("patient1","password1"), "S9983422D");
      Patient newPatient2 = patientService.createPatient(new Patient("patient2","password2"), "S9983423D");
      Patient newPatient3 = patientService.createPatient(new Patient("patient3","password3"), new ElectronicHealthRecord("S9983424D","John","Smith", LocalDateTime.of(1990, 2, 28, 0, 0, 0), "Singapore", "Male", "Caucasian", "Singaporean", "Marina Bay Sands", "83571234"));
      nextOfKinRecordService.createNextOfKinRecord(newPatient3.getPatientId(),new NextOfKinRecord("Wife", "S5882619D"));
      prescriptionRecordService.createPrescriptionRecord(newPatient3.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2010, 10, 5, 13, 15, 0), "Insulin", 1, 1, "Diabetes Management", "Take before meals", "Doctor Maria Garcia", PrescriptionStatusEnum.COLLECTED));
      problemRecordService.createProblemRecord(newPatient3.getPatientId(), new ProblemRecord("Type 2 Diabetes", "Doctor Maria Garcia", LocalDateTime.of(2009, 8, 12, 11, 20, 0), PriorityEnum.HIGH, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
      medicalHistoryRecordService.createMedicalHistoryRecord(newPatient3.getPatientId(), new MedicalHistoryRecord("High Blood Pressure", "Doctor Maria Garcia", LocalDateTime.of(2008, 4, 18, 14, 30, 0), LocalDateTime.of(2009, 9, 2, 9, 45, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.CARDIOVASCULAR));
      Patient newPatient4 = patientService.createPatient(new Patient("patient4","password4"), new ElectronicHealthRecord("S9983425D","Linda","Wong", LocalDateTime.of(1973, 11, 8, 0, 0, 0), "Singapore", "Female", "Chinese", "Singaporean", "Jurong West St 71", "81126543"));
      nextOfKinRecordService.createNextOfKinRecord(newPatient4.getPatientId(),new NextOfKinRecord("Son", "S5882620D"));
      nextOfKinRecordService.createNextOfKinRecord(newPatient4.getPatientId(),new NextOfKinRecord("Daughter", "S6882620D"));
      prescriptionRecordService.createPrescriptionRecord(newPatient4.getPatientId(), new PrescriptionRecord(LocalDateTime.of(1995, 3, 2, 10, 30, 0), "Painkillers", 2, 1, "Pain Relief for Arthritis", "Take as needed", "Doctor Kevin Tan", PrescriptionStatusEnum.COLLECTED));
      problemRecordService.createProblemRecord(newPatient4.getPatientId(), new ProblemRecord("Arthritis", "Doctor Kevin Tan", LocalDateTime.of(1994, 12, 15, 16, 0, 0), PriorityEnum.LOW, ProblemTypeEnum.OBSTETRIC));
      medicalHistoryRecordService.createMedicalHistoryRecord(newPatient4.getPatientId(), new MedicalHistoryRecord("Asthma", "Doctor Kevin Tan", LocalDateTime.of(1990, 7, 8, 9, 0, 0), LocalDateTime.of(1991, 5, 20, 14, 45, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.RESPIRATORY));
      Patient newPatient5 = patientService.createPatient(new Patient("patient5","password5"), new ElectronicHealthRecord("S9983426D","Megan","Chua", LocalDateTime.of(2000, 6, 15, 0, 0, 0), "Singapore", "Female", "Chinese", "Singaporean", "Bukit Timah Rd", "87751234"));
      nextOfKinRecordService.createNextOfKinRecord(newPatient5.getPatientId(),new NextOfKinRecord("Father", "S5882621D"));
      nextOfKinRecordService.createNextOfKinRecord(newPatient5.getPatientId(),new NextOfKinRecord("Mother", "S6882621D"));
      prescriptionRecordService.createPrescriptionRecord(newPatient5.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2021, 8, 10, 9, 45, 0), "Vitamin D", 1, 1, "Vitamin Supplement", "Take daily", "Doctor Sarah Tan", PrescriptionStatusEnum.COLLECTED));
      problemRecordService.createProblemRecord(newPatient5.getPatientId(), new ProblemRecord("Seasonal Allergies", "Doctor Sarah Tan", LocalDateTime.of(2021, 5, 3, 11, 15, 0), PriorityEnum.LOW, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
  }

  private void createAppointmentData() {

    Patient patient1 = patientService.getPatientByUsername("patient1");

    //for today
    appointmentService.createNewAppointment("test description 1",
        LocalDateTime.now().toString(),
        LocalDateTime.now().toString(),
        "LOW",
        patient1.getUsername(),
        "Cardiology");
    appointmentService.createNewAppointment("test description 2",
        LocalDateTime.now().toString(),
        LocalDateTime.now().toString(),
        "LOW",
        patient1.getUsername(),
        "Cardiology");
    appointmentService.createNewAppointment("test description 3",
        LocalDateTime.now().toString(),
        LocalDateTime.now().toString(),
        "LOW",
        patient1.getUsername(),
        "Cardiology");
    appointmentService.createNewAppointment("test description 4",
        LocalDateTime.now().toString(),
        LocalDateTime.now().toString(),
        "LOW",
        patient1.getUsername(),
        "Cardiology");
    appointmentService.createNewAppointment("test description 5",
        LocalDateTime.now().toString(),
        LocalDateTime.now().toString(),
        "LOW",
        patient1.getUsername(),
        "Cardiology");

    //testing filtering view
//    appointmentService.assignAppointmentToStaff(5L,4L);

    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(7L).toString(),
            LocalDateTime.now().toString(),
            "LOW",
            patient1.getUsername(),
            "Orthopedics");
    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(7L).toString(),
            LocalDateTime.now().toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");
    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(7L).toString(),
            LocalDateTime.now().toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");
    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(7L).toString(),
            LocalDateTime.now().toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");
    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(7L).toString(),
            LocalDateTime.now().toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");
    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(7L).toString(),
            LocalDateTime.now().toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");
    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(7L).toString(),
            LocalDateTime.now().toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");

    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(9L).toString(),
            LocalDateTime.now().plusDays(3L).toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");
    appointmentService.createNewAppointment("test description",
            LocalDateTime.now().plusDays(14L).toString(),
            LocalDateTime.now().plusDays(5L).toString(),
            "LOW",
            patient1.getUsername(),
            "Cardiology");
  }
  private void createConsumableEquipmentData() {
        ConsumableEquipment newConsumableEquipment1 = consumableEquipmentService.createConsumableEquipment(new ConsumableEquipment("Latex Powder-Free Gloves", "1 Box 100pcs", ItemTypeEnum.CONSUMABLE,100,BigDecimal.TEN));
      ConsumableEquipment newConsumableEquipment2 = consumableEquipmentService.createConsumableEquipment(new ConsumableEquipment("Surgical Masks", "1 Box 100pcs", ItemTypeEnum.CONSUMABLE,100,BigDecimal.valueOf(5)));
      ConsumableEquipment newConsumableEquipment3 = consumableEquipmentService.createConsumableEquipment(new ConsumableEquipment("Cotton Wool Pads", "1 Box 200pcs", ItemTypeEnum.CONSUMABLE,50,BigDecimal.valueOf(4)));
      ConsumableEquipment newConsumableEquipment4 = consumableEquipmentService.createConsumableEquipment(new ConsumableEquipment("Tissue Paper", "1 Box 20pcs", ItemTypeEnum.CONSUMABLE,1000,BigDecimal.valueOf(2)));
      ConsumableEquipment newConsumableEquipment5 = consumableEquipmentService.createConsumableEquipment(new ConsumableEquipment("Disposable Needles", "1 Box 5pcs", ItemTypeEnum.CONSUMABLE,100,BigDecimal.valueOf(3)));

      Facility f = facilityService.findFacilityById(Long.parseLong("1"));

      AllocatedInventory item1 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(), newConsumableEquipment1.getInventoryItemId(), 10, 1);
      AllocatedInventory item2 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(), newConsumableEquipment2.getInventoryItemId(), 10, 1);
      AllocatedInventory item3 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(), newConsumableEquipment3.getInventoryItemId(), 10, 1);
      AllocatedInventory item4 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(), newConsumableEquipment4.getInventoryItemId(), 10, 1);
      AllocatedInventory item5 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(), newConsumableEquipment5.getInventoryItemId(), 10, 1);

  }
}