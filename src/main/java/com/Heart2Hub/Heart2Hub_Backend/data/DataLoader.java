package com.Heart2Hub.Heart2Hub_Backend.data;

import com.Heart2Hub.Heart2Hub_Backend.Heart2HubBackendApplication;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.LeaveTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import com.Heart2Hub.Heart2Hub_Backend.service.LeaveService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.*;
import com.Heart2Hub.Heart2Hub_Backend.service.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
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
import java.util.Collection;
import java.util.List;

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

  private final SubsidyService subsidyService;
  private final LeaveService leaveService;
  private final ShiftConstraintsService shiftConstraintsService;

  private final ConsumableEquipmentService consumableEquipmentService;

  private final AllocatedInventoryService allocatedInventoryService;

  private final SubDepartmentRepository subDepartmentRepository;
  private final DepartmentRepository departmentRepository;
  private final WardService wardService;
  private final WardClassService wardClassService;
  private final MedicationService medicationService;
  private final ServiceItemService serviceItemService;

  private final TransactionItemService transactionItemService;

  //  private final SubDepartmentRepository subDepartmentRepository;
  private final AppointmentService appointmentService;
  private final InventoryItemRepository inventoryItemRepository;

  private final PrescriptionRecordRepository prescriptionRecordRepository;
  private final InvoiceService invoiceService;
  private final DrugRestrictionService drugRestrictionService;
  private final UnitRepository unitRepository;
  private final AdmissionService admissionService;

  public DataLoader(StaffService staffService, ShiftService shiftService, DepartmentService departmentService, AuthenticationManager authenticationManager, FacilityService facilityService, PatientService patientService, NextOfKinRecordService nextOfKinRecordService, PrescriptionRecordService prescriptionRecordService, ProblemRecordService problemRecordService, MedicalHistoryRecordService medicalHistoryRecordService, TreatmentPlanRecordService treatmentPlanRecordService, SubsidyService subsidyService, LeaveService leaveService, ShiftConstraintsService shiftConstraintsService, ConsumableEquipmentService consumableEquipmentService, AllocatedInventoryService allocatedInventoryService, SubDepartmentRepository subDepartmentRepository, DepartmentRepository departmentRepository, WardService wardService, WardClassService wardClassService, MedicationService medicationService, ServiceItemService serviceItemService, TransactionItemService transactionItemService, AppointmentService appointmentService, InventoryItemRepository inventoryItemRepository, PrescriptionRecordRepository prescriptionRecordRepository, InvoiceService invoiceService, DrugRestrictionService drugRestrictionService,
                    UnitRepository unitRepository, AdmissionService admissionService) {
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
    this.subsidyService = subsidyService;
    this.leaveService = leaveService;
    this.shiftConstraintsService = shiftConstraintsService;
    this.consumableEquipmentService = consumableEquipmentService;
    this.allocatedInventoryService = allocatedInventoryService;
    this.subDepartmentRepository = subDepartmentRepository;
    this.departmentRepository = departmentRepository;
    this.wardService = wardService;
    this.wardClassService = wardClassService;
    this.medicationService = medicationService;
    this.serviceItemService = serviceItemService;
    this.transactionItemService = transactionItemService;
    this.appointmentService = appointmentService;
    this.inventoryItemRepository = inventoryItemRepository;
    this.prescriptionRecordRepository = prescriptionRecordRepository;
    this.invoiceService = invoiceService;
    this.drugRestrictionService = drugRestrictionService;
    this.unitRepository = unitRepository;
    this.admissionService = admissionService;
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
    createConsumableEquipmentData();
    createMedicationData();
    createServiceItemData();
    createPatientData();
    createAppointmentData();

    createSubsidyData();
    createTransactionItems();
    createInvoice();
    createAdmissionData();
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
        new Staff("staff10", "password10", "Simone", "Lim", 80806996l, StaffRoleEnum.NURSE,
            false),
        "Cardiology", new ImageDocument("id9.png", lt));
    staffService.createStaff(
        new Staff("staff11", "password11", "Harry", "Maguire", 90806996l, StaffRoleEnum.ADMIN, false),
        "Cardiology", new ImageDocument("id10.png", lt));
    staffService.createStaff(
        new Staff("staff12", "password12", "Taylor", "Swift", 89806996l, StaffRoleEnum.ADMIN, false),
        "Cardiology", new ImageDocument("id11.png", lt));
    staffService.createStaff(
        new Staff("nurseA11", "password", "James", "Charles", 93420093l, StaffRoleEnum.NURSE,
            true), "A1", new ImageDocument("id12.png", lt));
    staffService.createStaff(
        new Staff("nurseB101", "password", "Ronald", "Weasley", 90897321l, StaffRoleEnum.NURSE,
            false), "B10", new ImageDocument("id13.png", lt));
    staffService.createStaff(
            new Staff("nurseB201", "password", "Ed", "Sheeran", 93420094l, StaffRoleEnum.NURSE,
                    true), "B20", new ImageDocument("id12.png", lt));
    staffService.createStaff(
            new Staff("nurseB202", "password", "Cristiano", "Ronaldo", 93420094l, StaffRoleEnum.NURSE,
                    true), "B20", new ImageDocument("id13.png", lt));
    staffService.createStaff(
            new Staff("nurseC11", "password", "Xiao", "Ming", 93420094l, StaffRoleEnum.NURSE,
                    true), "C1", new ImageDocument("id12.png", lt));
    staffService.createStaff(
            new Staff("adminB201", "password", "Lionel", "Messi", 95420094l, StaffRoleEnum.ADMIN,
                    false), "B20", new ImageDocument("id14.png", lt));
    staffService.createStaff(
            new Staff("staff15", "password15", "Bruno", "Mars", 90897322l, StaffRoleEnum.PHARMACIST,
                    true), "Pharmacy", new ImageDocument("id14.png", lt));
    staffService.createStaff(
            new Staff("staff16", "password16", "Katy", "Perry", 90897323l, StaffRoleEnum.PHARMACIST,
                    false), "Pharmacy", new ImageDocument("id15.png", lt));
    staffService.createStaff(
            new Staff("staff17", "password17", "Cristiano", "Ronaldo", 90897324l, StaffRoleEnum.PHARMACIST,
                    false), "Pharmacy", new ImageDocument("id16.png", lt));
    staffService.createStaff(
            new Staff("staff18", "password18", "Meeeeeeeeelvin", "Tan", 93693693l, StaffRoleEnum.DIAGNOSTIC_RADIOGRAPHERS,
                    false), "Cardiology", new ImageDocument("id17.png", lt));
    staffService.createStaff(
            new Staff("adminPharm1", "password", "Ross", "Geller", 93693694l, StaffRoleEnum.ADMIN,
                    false), "Pharmacy", new ImageDocument("id18.png", lt));
    staffService.createStaff(
            new Staff("adminPharm2", "password", "Monica", "Geller", 93693695l, StaffRoleEnum.ADMIN,
                    false), "Pharmacy", new ImageDocument("id19.png", lt));

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
    departmentService.createDepartment(new Department("Radiology"));
    departmentService.createDepartment(new Department("Pharmacy"));
    departmentService.createDepartment(new Department("Admin"));

//    TO-DO: WARD CREATION
    wardClassService.createWardClass(new WardClass("A", new BigDecimal("621"), 1));
    wardClassService.createWardClass(new WardClass("B1", new BigDecimal("309.31"), 4));
    wardClassService.createWardClass(new WardClass("B2", new BigDecimal("57"), 6));
    wardClassService.createWardClass(new WardClass("C", new BigDecimal("40.70"), 8));

    wardService.createWard(new Ward("A1", "Block 1", 10), "A");
    wardService.createWard(new Ward("A2", "Block 2", 10), "A");
    wardService.createWard(new Ward("A3", "Block 3", 10), "A");
    wardService.createWard(new Ward("B10", "Block 4", 12), "B1");
    wardService.createWard(new Ward("B11", "Block 5", 12), "B1");
    wardService.createWard(new Ward("B12", "Block 6", 12), "B1");
    wardService.createWard(new Ward("B20", "Block 7", 18), "B2");
    wardService.createWard(new Ward("B21", "Block 8", 18), "B2");
    wardService.createWard(new Ward("B22", "Block 9", 18), "B2");
    wardService.createWard(new Ward("C1", "Block 10", 24), "C");
    wardService.createWard(new Ward("C2", "Block 11", 24), "C");
    wardService.createWard(new Ward("C3", "Block 12", 24), "C");

//    departmentService.createDepartment(new Department("Ward B-1"));
//    departmentService.createDepartment(new Department("Ward B-2"));
//    departmentService.createDepartment(new Department("Ward B-3"));
//    departmentService.createDepartment(new Department("Ward C-1"));
//    departmentService.createDepartment(new Department("Ward C-2"));
//    departmentService.createDepartment(new Department("Ward C-3"));
  }

  private void createFacilityData() {
    // For Sub Department Facility Creation
    for (long L = 1L; L <= 9L; L++) {
      facilityService.createFacility(L,
          new Facility("Consultation Room 1 " + departmentRepository.findById(L).get().getName(),
              "Level 1", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.CONSULTATION_ROOM));
      facilityService.createFacility(L,
          new Facility("Consultation Room 2 " + departmentRepository.findById(L).get().getName(),
              "Level 1", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.CONSULTATION_ROOM));
      facilityService.createFacility(L,
          new Facility("Consultation Room 3 " + departmentRepository.findById(L).get().getName(),
              "Level 1", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.CONSULTATION_ROOM));
      facilityService.createFacility(L,
          new Facility("Triage Room 1 " + departmentRepository.findById(L).get().getName(),
              "Level 2", "", 2, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.TRIAGE_ROOM));
      facilityService.createFacility(L,
          new Facility("Triage Room 2 " + departmentRepository.findById(L).get().getName(),
              "Level 2", "", 2, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.TRIAGE_ROOM));
      facilityService.createFacility(L,
          new Facility("Triage Room 3 " + departmentRepository.findById(L).get().getName(),
              "Level 2", "", 2, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.TRIAGE_ROOM));
      facilityService.createFacility(L,
          new Facility("Operating Room 1 " + departmentRepository.findById(L).get().getName(),
              "Level 2", "", 6, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.OPERATING_ROOM));
      facilityService.createFacility(L,
          new Facility("Operating Room 2 " + departmentRepository.findById(L).get().getName(),
              "Level 3", "", 6, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.OPERATING_ROOM));
      facilityService.createFacility(L,
          new Facility("Registration Counter 1 " + departmentRepository.findById(L).get().getName(),
              "Level 1", "", 2, FacilityStatusEnum.BOOKABLE,
              FacilityTypeEnum.ADMINISTRATION_OFFICES));
      facilityService.createFacility(L,
          new Facility("Registration Counter 2 " + departmentRepository.findById(L).get().getName(),
              "Level 1", "", 2, FacilityStatusEnum.BOOKABLE,
              FacilityTypeEnum.ADMINISTRATION_OFFICES));

      if (L == 5L) {
        facilityService.createFacility(L,
            new Facility("Emergency Room 1 " + departmentRepository.findById(L).get().getName(),
                "Level 1", "", 4, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.EMERGENCY_ROOM));
        facilityService.createFacility(L,
            new Facility("Emergency Room 2 " + departmentRepository.findById(L).get().getName(),
                "Level 1", "", 4, FacilityStatusEnum.NON_BOOKABLE,
                FacilityTypeEnum.EMERGENCY_ROOM));
      }
    }
    facilityService.createFacility(10L,
            new Facility("Counter 1 " + departmentRepository.findById(10L).get().getName(),
                    "Level 2", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.PHARMACY_COUNTER));
    facilityService.createFacility(10L,
            new Facility("Counter 2 " + departmentRepository.findById(10L).get().getName(),
                    "Level 2", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.PHARMACY_COUNTER));
    facilityService.createFacility(10L,
            new Facility("Counter 3 " + departmentRepository.findById(10L).get().getName(),
                    "Level 2", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.PHARMACY_COUNTER));
    facilityService.createFacility(10L,
            new Facility("Registration Counter 1 " + departmentRepository.findById(10L).get().getName(),
                    "Level 2", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.ADMINISTRATION_OFFICES));
    facilityService.createFacility(10L,
            new Facility("Registration Counter 2 " + departmentRepository.findById(10L).get().getName(),
                    "Level 2", "", 1, FacilityStatusEnum.BOOKABLE, FacilityTypeEnum.ADMINISTRATION_OFFICES));
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
    shiftService.createShift("staff2", 41L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff3", 42L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
    shiftService.createShift("staff4", 43L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff8", 4L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff9", 5L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff10", 6L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff11", 9L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff12", 10L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("nurseA11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB101", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB202", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseC11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff15", 93L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff16", 94L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff17", 95L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminPharm1", 96L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("adminPharm2", 97L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology diagnostic radiographers shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff18", 11L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Tuesday
    currentDateTime = monday.plusDays(1);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 41L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
    shiftService.createShift("staff3", 42L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
    shiftService.createShift("staff4", 43L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff8", 4L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff9", 5L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff10", 6L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff11", 9L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff12", 10L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("nurseA11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB101", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB202", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseC11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));


    // Pharmacy shifts - Working hours(8am - 4pm)
    shiftService.createShift("staff15", 93L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff16", 94L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff17", 95L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminPharm1", 96L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("adminPharm2", 97L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology diagnostic radiographers shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff18", 11L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Wednesday
    currentDateTime = monday.plusDays(2);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 41L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff3", 42L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
    shiftService.createShift("staff4", 43L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff8", 4L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff9", 5L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff10", 6L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff11", 9L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff12", 10L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("nurseA11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB101", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB202", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseC11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy shifts - Working hours(8am - 4pm)
    shiftService.createShift("staff15", 93L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff16", 94L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff17", 95L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminPharm1", 96L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("adminPharm2", 97L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology diagnostic radiographers shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff18", 11L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Thursday
    currentDateTime = monday.plusDays(3);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 41L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
    shiftService.createShift("staff3", 42L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff4", 43L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working 24hr shift"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff8", 4L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff9", 5L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff10", 6L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff11", 9L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff12", 10L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy shifts - Working hours(8am - 4pm)
    shiftService.createShift("staff15", 93L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff16", 94L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff17", 95L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminPharm1", 96L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("adminPharm2", 97L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology diagnostic radiographers shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff18", 11L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Friday
    currentDateTime = monday.plusDays(4);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 41L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff3", 42L, new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff6", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff8", 4L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff9", 5L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff11", 9L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("nurseA11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB101", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB202", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseC11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy shifts - Working hours(8am - 4pm)
    shiftService.createShift("staff15", 93L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff16", 94L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminPharm1", 96L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("adminPharm2", 97L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology diagnostic radiographers shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff18", 11L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    // Saturday
    currentDateTime = monday.plusDays(5);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // EM doctor shifts - 4 shifts available
    shiftService.createShift("staff2", 41L, new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
    shiftService.createShift("staff3", 42L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff4", 43L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff5", 1L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff8", 4L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff10", 6L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff12", 10L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("nurseA11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB101", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB202", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseC11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy shifts - Working hours(8am - 4pm)
    shiftService.createShift("staff15", 93L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff17", 95L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminPharm1", 96L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology diagnostic radiographers shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff18", 11L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Sunday
    currentDateTime = monday.plusDays(6);
    day = currentDateTime.getDayOfMonth();
    month = currentDateTime.getMonthValue();
    year = currentDateTime.getYear();
    // Cardiology doctor shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff6", 2L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff7", 3L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff9", 5L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff10", 6L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Cardiology admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("staff11", 9L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient nurse shifts - Working hours (8am - 4pm)
    shiftService.createShift("nurseA11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB101", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseB202", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("nurseC11", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Inpatient admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminB201", null, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));


    // Pharmacy shifts - Working hours(8am - 4pm)
    shiftService.createShift("staff16", 94L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
    shiftService.createShift("staff17", 95L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Pharmacy admin shifts - Working hours (8am - 4pm)
    shiftService.createShift("adminPharm2", 97L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
            LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));

    // Generate cardiology doctor shifts for the next 8 weeks
    for (int i = 7; i <= 48; i++) {
      currentDateTime = monday.plusDays(i);
      day = currentDateTime.getDayOfMonth();
      month = currentDateTime.getMonthValue();
      year = currentDateTime.getYear();
      // 29 oct only 1 doctor
      if (day == 29 && month == 10 && year == 2023) {
        shiftService.createShift("staff5", 1L,
                new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                        LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
      } else {
        // Cardiology doctor shifts - Working hours (8am - 4pm)
        if (i != 11 && i != 18 && i != 25 && i != 32 && i != 39 && i != 46) {
          shiftService.createShift("staff5", 1L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff8", 4L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff11", 9L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff15", 93L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("adminPharm2", 97L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
        }
        if (i != 12 && i != 19 && i != 26 && i != 33 && i != 40 && i != 47) {
          shiftService.createShift("staff6", 2L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff9", 5L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff12", 10L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff16", 94L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("adminPharm1", 96L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
        }
        if (i != 13 && i != 20 && i != 27 && i != 34 && i != 41 && i != 48) {
          shiftService.createShift("staff7", 3L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff10", 6L,
                  new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                          LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          shiftService.createShift("staff17", 95L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
          // Cardiology diagnostic radiographers shifts - Working hours (8am - 4pm)
          shiftService.createShift("staff18", 11L, new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
        }
      }
    }

    shiftConstraintsService.createShiftConstraints(
        new ShiftConstraints(LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), 1,
            StaffRoleEnum.DOCTOR), "Consultation Room 1 Emergency Medicine");

  }

  private void createPatientData() {
    LocalDateTime currentDate = LocalDateTime.now();

    Patient newPatient1 = patientService.createPatient(new Patient("patient1", "password1"), "S9983422D", new ImageDocument("id1.png",LocalDateTime.now()));
    Patient newPatient2 = patientService.createPatient(new Patient("patient2", "password2"), "S9983423D", new ImageDocument("id4.png",LocalDateTime.now()));
    Patient newPatient3 = patientService.createPatient(new Patient("patient3","password3"), new ElectronicHealthRecord("S9983424D","John","Smith", LocalDateTime.of(1990, 2, 28, 0, 0, 0), "Singapore", "Male", "Others", "Singapore Citizen", "Marina Bay Sands", "83571234"),new ImageDocument("id2.png", currentDate));
    nextOfKinRecordService.createNextOfKinRecord(newPatient3.getPatientId(),new NextOfKinRecord("Spouse", "S5882619D"));
    PrescriptionRecord pr1 = prescriptionRecordService.createPrescriptionRecord(newPatient3.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2010, 10, 5, 13, 15, 0), "Insulin", 1, 1, "Diabetes Management", "Take before meals", "Doctor Maria Garcia", PrescriptionStatusEnum.COLLECTED, null));
    problemRecordService.createProblemRecord(newPatient3.getPatientId(), new ProblemRecord("Type 2 Diabetes", "Doctor Maria Garcia", LocalDateTime.of(2009, 8, 12, 11, 20, 0), PriorityEnum.HIGH, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
    medicalHistoryRecordService.createMedicalHistoryRecord(newPatient3.getPatientId(), new MedicalHistoryRecord("High Blood Pressure", "Doctor Maria Garcia", LocalDateTime.of(2008, 4, 18, 14, 30, 0), LocalDateTime.of(2009, 9, 2, 9, 45, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.CARDIOVASCULAR));
    Patient newPatient4 = patientService.createPatient(new Patient("patient4","password4"), new ElectronicHealthRecord("S9983425D","Linda","Wong", LocalDateTime.of(1973, 11, 8, 0, 0, 0), "Singapore", "Female", "Chinese", "Singapore Citizen", "Jurong West St 71", "81126543"),new ImageDocument("id5.png", currentDate));
    nextOfKinRecordService.createNextOfKinRecord(newPatient4.getPatientId(),new NextOfKinRecord("Child", "S5882620D"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient4.getPatientId(),new NextOfKinRecord("Child", "S6882620D"));
    PrescriptionRecord pr2 = prescriptionRecordService.createPrescriptionRecord(newPatient4.getPatientId(), new PrescriptionRecord(LocalDateTime.of(1995, 3, 2, 10, 30, 0), "Painkillers", 2, 1, "Pain Relief for Arthritis", "Take as needed", "Doctor Kevin Tan", PrescriptionStatusEnum.COLLECTED, null));
    problemRecordService.createProblemRecord(newPatient4.getPatientId(), new ProblemRecord("Arthritis", "Doctor Kevin Tan", LocalDateTime.of(1994, 12, 15, 16, 0, 0), PriorityEnum.LOW, ProblemTypeEnum.OBSTETRIC));
    medicalHistoryRecordService.createMedicalHistoryRecord(newPatient4.getPatientId(), new MedicalHistoryRecord("Asthma", "Doctor Kevin Tan", LocalDateTime.of(1990, 7, 8, 9, 0, 0), LocalDateTime.of(1991, 5, 20, 14, 45, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.RESPIRATORY));
    Patient newPatient5 = patientService.createPatient(new Patient("patient5","password5"), new ElectronicHealthRecord("S9983426D","Megan","Chua", LocalDateTime.of(2000, 6, 15, 0, 0, 0), "Singapore", "Female", "Chinese", "Singapore Citizen", "Bukit Timah Rd", "87751234"), new ImageDocument("id9.png",currentDate));
    nextOfKinRecordService.createNextOfKinRecord(newPatient5.getPatientId(),new NextOfKinRecord("Father", "S5882621D"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient5.getPatientId(),new NextOfKinRecord("Mother", "S6882621D"));
    PrescriptionRecord pr3 = prescriptionRecordService.createPrescriptionRecord(newPatient5.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2021, 8, 10, 9, 45, 0), "Vitamin D", 1, 1, "Vitamin Supplement", "Take daily", "Doctor Sarah Tan", PrescriptionStatusEnum.COLLECTED, null));
    problemRecordService.createProblemRecord(newPatient5.getPatientId(), new ProblemRecord("Seasonal Allergies", "Doctor Sarah Tan", LocalDateTime.of(2021, 5, 3, 11, 15, 0), PriorityEnum.LOW, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
    Patient newPatient6 = patientService.createPatient(new Patient("patient6", "password6"), new ElectronicHealthRecord("S9983427D","Michael","Angelo", LocalDateTime.of(1999, 5, 22, 0, 0, 0), "Singapore", "Male", "Malay", "Singapore Citizen", "Pasir Ris St 9", "83571235"), new ImageDocument("id3.png",currentDate));
    nextOfKinRecordService.createNextOfKinRecord(newPatient6.getPatientId(),new NextOfKinRecord("Father", "S5882622D"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient6.getPatientId(),new NextOfKinRecord("Mother", "S6882622D"));
    PrescriptionRecord pr4 = prescriptionRecordService.createPrescriptionRecord(newPatient6.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2021, 8, 10, 9, 45, 0), "Amoxicillin", 3, 1, "Chewable tabs for ear infection", "Take daily", "Doctor Ash Ketchum", PrescriptionStatusEnum.COLLECTED, null));
    problemRecordService.createProblemRecord(newPatient6.getPatientId(), new ProblemRecord("Ear infection", "Doctor Ash Ketchum", LocalDateTime.of(2021, 5, 3, 11, 15, 0), PriorityEnum.LOW, ProblemTypeEnum.EYE_AND_EAR));
    Patient newPatient7 = patientService.createPatient(new Patient("patient7", "password7"), new ElectronicHealthRecord("S9983428D","Tan","Ah Tok", LocalDateTime.of(1985, 2, 28, 0, 0, 0), "Singapore", "Male", "Chinese", "Singapore Citizen", "Orchard Road", "83571236"), new ImageDocument("id7.png",currentDate));
    nextOfKinRecordService.createNextOfKinRecord(newPatient7.getPatientId(),new NextOfKinRecord("Father", "S5882623D"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient7.getPatientId(),new NextOfKinRecord("Mother", "S6882623D"));
    PrescriptionRecord pr5 = prescriptionRecordService.createPrescriptionRecord(newPatient7.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2021, 8, 10, 9, 45, 0), "Colace", 30, 1, "Stool softener", "Take as needed", "Doctor Brown Bum", PrescriptionStatusEnum.COLLECTED, null));
    problemRecordService.createProblemRecord(newPatient7.getPatientId(), new ProblemRecord("Explosive diarrhoea", "Doctor Brown Bum", LocalDateTime.of(2021, 5, 3, 11, 15, 0), PriorityEnum.LOW, ProblemTypeEnum.GASTROINTESTINAL));
    Patient newPatient8 = patientService.createPatient(new Patient("patient8", "password8"), new ElectronicHealthRecord("S9983429D","Parvati","Lakshmi", LocalDateTime.of(1991, 10, 2, 0, 0, 0), "Singapore", "Female", "Indian", "Singapore Citizen", "Punggol Ave 4", "83571237"), new ImageDocument("id16.png",currentDate));
    nextOfKinRecordService.createNextOfKinRecord(newPatient8.getPatientId(),new NextOfKinRecord("Father", "S5882624D"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient8.getPatientId(),new NextOfKinRecord("Mother", "S6882624D"));
    PrescriptionRecord pr6 = prescriptionRecordService.createPrescriptionRecord(newPatient8.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2021, 8, 10, 9, 45, 0), "Metformin HCL", 10, 1, "Tablet", "Take daily", "Doctor Dia Betes", PrescriptionStatusEnum.COLLECTED, null));
    problemRecordService.createProblemRecord(newPatient8.getPatientId(), new ProblemRecord("Type 2 Diabetes", "Doctor Dia Betes", LocalDateTime.of(2021, 5, 3, 11, 15, 0), PriorityEnum.HIGH, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
    Patient newPatient9 = patientService.createPatient(new Patient("patient9", "password9"), new ElectronicHealthRecord("S5882617D","Adamanthium","Lai", LocalDateTime.of(1965, 10, 2, 0, 0, 0), "Singapore", "Male", "Chinese", "Singapore Citizen", "Punggol Ave 23", "99971237"), new ImageDocument("id8.png",currentDate));
    nextOfKinRecordService.createNextOfKinRecord(newPatient9.getPatientId(),new NextOfKinRecord("Child", "S9983422D"));
    problemRecordService.createProblemRecord(newPatient9.getPatientId(), new ProblemRecord("Type 2 Diabetes", "Doctor Dia Loh", LocalDateTime.of(2021, 5, 3, 11, 15, 0), PriorityEnum.HIGH, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
    medicalHistoryRecordService.createMedicalHistoryRecord(newPatient9.getPatientId(), new MedicalHistoryRecord("Super Aids", "Doctor Kevin Tan", LocalDateTime.of(1990, 7, 8, 9, 0, 0), LocalDateTime.of(1991, 5, 20, 14, 45, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.RESPIRATORY));

    pr1.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr1.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr1);
    pr2.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr2.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr2);
    pr3.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr3.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr3);
    pr4.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr4.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr4);
    pr5.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr5.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr5);
    pr6.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr6.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr6);

    PrescriptionRecord pr7 = prescriptionRecordRepository.findById(Long.valueOf(1)).get();
    pr7.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr7.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr7);

    PrescriptionRecord pr8 = prescriptionRecordRepository.findById(Long.valueOf(2)).get();
    pr8.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    pr8.setLastCollectDate(LocalDateTime.now().minusDays(7L));
    prescriptionRecordRepository.save(pr8);

    PrescriptionRecord pr9 = prescriptionRecordRepository.findById(Long.valueOf(3)).get();
    pr9.setExpirationDate(LocalDateTime.of(2025, 9, 16, 15, 30, 0));
    prescriptionRecordRepository.save(pr9);
  }

  private void createAppointmentData() {

    System.out.println(LocalDateTime.now().plusDays(7L).toString());

    Patient patient1 = patientService.getPatientByUsername("patient1");
    Patient patient2 = patientService.getPatientByUsername("patient2");
    Patient patient3 = patientService.getPatientByUsername("patient3");
    Patient patient4 = patientService.getPatientByUsername("patient4");
    Patient patient5 = patientService.getPatientByUsername("patient5");
    Patient patient6 = patientService.getPatientByUsername("patient6");
    Patient patient7 = patientService.getPatientByUsername("patient7");
    Patient patient8 = patientService.getPatientByUsername("patient8");
//
    Staff d1 = staffService.getStaffByUsername("staff5");
    Staff p1 = staffService.getStaffByUsername("staff15");

    //for past appointments
//    LocalDateTime pastDate1 = LocalDateTime.now().minusDays(14).withHour(9).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("Chest pain",
//        pastDate1.toString(),
////        LocalDateTime.now().minusDays(7).toString(),
//        "LOW",
//        patient1.getElectronicHealthRecord().getNric(),
//        "Cardiology");
//    LocalDateTime pastDate2 = LocalDateTime.now().minusDays(14).withHour(15).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("Heart Palpitations",
//        pastDate2.toString(),
////        LocalDateTime.now().minusDays(6).toString(),
//        "LOW",
//        patient2.getElectronicHealthRecord().getNric(),
//        "Cardiology");
//    LocalDateTime pastDate3 = LocalDateTime.now().minusDays(17).withHour(15).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("Heart Murmurs",
//        pastDate3.toString(),
////        LocalDateTime.now().minusDays(5).toString(),
//        "MEDIUM",
//        patient3.getElectronicHealthRecord().getNric(),
//        "Cardiology");
//    LocalDateTime pastDate4 = LocalDateTime.now().minusDays(18).withHour(15).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("Valve disease",
//        pastDate4.toString(),
////        LocalDateTime.now().minusDays(8).toString(),
//        "LOW",
//        patient4.getElectronicHealthRecord().getNric(),
//        "Cardiology");
//    LocalDateTime pastDate5 = LocalDateTime.now().minusDays(21).withHour(13).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("Chest Palpitations",
//        pastDate5.toString(),
////        LocalDateTime.now().minusDays(15).toString(),
//        "LOW",
//        patient5.getElectronicHealthRecord().getNric(),
//        "Cardiology");
//    LocalDateTime pastDate6 = LocalDateTime.now().minusDays(21).withHour(16).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("Heart checkup",
//        pastDate6.toString(),
////        LocalDateTime.now().minusDays(15).toString(),
//        "LOW",
//        patient6.getElectronicHealthRecord().getNric(),
//        "Cardiology");
//    LocalDateTime pastDate7 = LocalDateTime.now().minusDays(30).withHour(16).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("High blood pressure",
//        pastDate7.toString(),
////        LocalDateTime.now().minusDays(25).toString(),
//        "LOW",
//        patient7.getElectronicHealthRecord().getNric(),
//        "Cardiology");
//    LocalDateTime pastDate8 = LocalDateTime.now().minusDays(42).withHour(11).withMinute(0)
//        .withSecond(0);
//    appointmentService.createNewAppointment("Chest discomfort",
//        pastDate8.toString(),
////        LocalDateTime.now().minusDays(23).toString(),
//        "LOW",
//        patient8.getElectronicHealthRecord().getNric(),
//        "Cardiology");

    //for today
    LocalDateTime date1 = LocalDateTime.now().withHour(9).withMinute(0).withSecond(0);
    Appointment a1 = appointmentService.createNewAppointment("Unstable angina",
        date1.toString(),
//            LocalDateTime.now().minusDays(7).toString(),
        "MEDIUM",
        patient1.getElectronicHealthRecord().getNric(),
        "Cardiology");
    Appointment a2 = appointmentService.createNewAppointment("Heart attack",
        date1.toString(),
//            LocalDateTime.now().minusDays(8).toString(),
        "HIGH",
        patient2.getElectronicHealthRecord().getNric(),
        "Cardiology");
    Appointment a3 = appointmentService.createNewAppointment("Heart failure",
        date1.toString(),
//            LocalDateTime.now().minusDays(9).toString(),
        "HIGH",
        patient3.getElectronicHealthRecord().getNric(),
        "Cardiology");

    LocalDateTime date2 = LocalDateTime.now().withHour(15).withMinute(0).withSecond(0);
    Appointment a4 = appointmentService.createNewAppointment("Valve disease",
        date2.toString(),
//            LocalDateTime.now().minusDays(10).toString(),
        "LOW",
        patient4.getElectronicHealthRecord().getNric(),
        "Cardiology");
    Appointment a5 = appointmentService.createNewAppointment("Arrhythmia",
        date2.toString(),
//            LocalDateTime.now().minusDays(11).toString(),
        "MEDIUM",
        patient5.getElectronicHealthRecord().getNric(),
        "Cardiology");
    Appointment a6 = appointmentService.createNewAppointment("Congenital heart conditions",
        date2.toString(),
//            LocalDateTime.now().minusDays(12).toString(),
        "MEDIUM",
        patient6.getElectronicHealthRecord().getNric(),
        "Cardiology");

    LocalDateTime date3 = LocalDateTime.now().withHour(19).withMinute(0).withSecond(0);
    Appointment a7 = appointmentService.createNewAppointment("High blood pressure",
        date3.toString(),
//            LocalDateTime.now().minusDays(13).toString(),
        "LOW",
        patient7.getElectronicHealthRecord().getNric(),
        "Cardiology");
    Appointment a8 = appointmentService.createNewAppointment("Inherited heart conditions",
        date3.toString(),
//            LocalDateTime.now().minusDays(14).toString(),
        "LOW",
        patient8.getElectronicHealthRecord().getNric(),
        "Cardiology");
//    appointmentService.assignAppointmentToStaff(a1.getAppointmentId(),p1.getStaffId(),d1.getStaffId());
//    appointmentService.updateAppointmentSwimlaneStatus(a1.getAppointmentId(),SwimlaneStatusEnum.PHARMACY);
//    appointmentService.updateAppointmentArrival(a1.getAppointmentId(),true,p1.getStaffId());

//    appointmentService.assignAppointmentToStaff(a2.getAppointmentId(),p1.getStaffId(),d1.getStaffId());
//    appointmentService.updateAppointmentSwimlaneStatus(a2.getAppointmentId(),SwimlaneStatusEnum.PHARMACY);
//    appointmentService.updateAppointmentArrival(a2.getAppointmentId(),true,p1.getStaffId());
//    appointmentService.updateAppointmentSwimlaneStatus(a3.getAppointmentId(),SwimlaneStatusEnum.PHARMACY);
//    appointmentService.updateAppointmentSwimlaneStatus(a4.getAppointmentId(),SwimlaneStatusEnum.PHARMACY);


    //for future dates
    LocalDateTime futureDate1 = LocalDateTime.now().plusDays(5L).withHour(19).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Unstable angina appointment 2",
        futureDate1.toString(),
//            LocalDateTime.now().toString(),
        "LOW",
        patient1.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate2 = LocalDateTime.now().plusDays(5L).withHour(12).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Heart attack appointment 2",
        futureDate2.toString(),
//            LocalDateTime.now().toString(),
        "HIGH",
        patient2.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate3 = LocalDateTime.now().plusDays(6L).withHour(12).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Heart failure appointment 2",
        futureDate3.toString(),
//            LocalDateTime.now().toString(),
        "HIGH",
        patient3.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate4 = LocalDateTime.now().plusDays(7L).withHour(10).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Valve disease appointment 2",
        futureDate4.toString(),
//            LocalDateTime.now().toString(),
        "LOW",
        patient4.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate5 = LocalDateTime.now().plusDays(7L).withHour(12).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Arrhythmia appointment 2",
        LocalDateTime.now().plusDays(7L).toString(),
//            LocalDateTime.now().toString(),
        "LOW",
        patient5.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate6 = LocalDateTime.now().plusDays(7L).withHour(12).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Congenital heart conditions appointment 2",
        futureDate6.toString(),
//            LocalDateTime.now().toString(),
        "LOW",
        patient6.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate7 = LocalDateTime.now().plusDays(9L).withHour(12).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("High blood pressure appointment 2",
        futureDate7.toString(),
//            LocalDateTime.now().plusDays(3L).toString(),
        "LOW",
        patient7.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate8 = LocalDateTime.now().plusDays(13L).withHour(12).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Inherited heart conditions appointment 2",
        futureDate8.toString(),
//            LocalDateTime.now().plusDays(5L).toString(),
        "LOW",
        patient8.getElectronicHealthRecord().getNric(),
        "Cardiology");
    LocalDateTime futureDate9 = LocalDateTime.now().plusDays(14L).withHour(12).withMinute(0)
        .withSecond(0);
    appointmentService.createNewAppointment("Unstable angina appointment 3",
        futureDate9.toString(),
//            LocalDateTime.now().plusDays(5L).toString(),
        "LOW",
        patient1.getElectronicHealthRecord().getNric(),
        "Cardiology");

    // SR3: Set appointments to start in Consultation
    appointmentService.updateAppointmentSwimlaneStatus(a1.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a1.getAppointmentId(), 5L, -1L);

    appointmentService.updateAppointmentSwimlaneStatus(a2.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a2.getAppointmentId(), 5L, -1L);

    appointmentService.updateAppointmentSwimlaneStatus(a3.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a3.getAppointmentId(), 5L, -1L);

    appointmentService.updateAppointmentSwimlaneStatus(a4.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a4.getAppointmentId(), 5L, -1L);

    appointmentService.updateAppointmentSwimlaneStatus(a5.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a5.getAppointmentId(), 5L, -1L);

    appointmentService.updateAppointmentSwimlaneStatus(a6.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a6.getAppointmentId(), 5L, -1L);

    appointmentService.updateAppointmentSwimlaneStatus(a7.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a7.getAppointmentId(), 5L, -1L);

    appointmentService.updateAppointmentSwimlaneStatus(a8.getAppointmentId(), SwimlaneStatusEnum.CONSULTATION);
    appointmentService.assignAppointmentToStaff(a8.getAppointmentId(), 5L, -1L);


  }

  private void createConsumableEquipmentData() {
    ConsumableEquipment newConsumableEquipment1 = consumableEquipmentService.createConsumableEquipment(
        new ConsumableEquipment("Latex Powder-Free Gloves", "1 Box 100pcs", ItemTypeEnum.CONSUMABLE,
            100, BigDecimal.TEN));
    ConsumableEquipment newConsumableEquipment2 = consumableEquipmentService.createConsumableEquipment(
        new ConsumableEquipment("Surgical Masks", "1 Box 100pcs", ItemTypeEnum.CONSUMABLE, 100,
            BigDecimal.valueOf(5)));
    ConsumableEquipment newConsumableEquipment3 = consumableEquipmentService.createConsumableEquipment(
        new ConsumableEquipment("Cotton Wool Pads", "1 Box 200pcs", ItemTypeEnum.CONSUMABLE, 50,
            BigDecimal.valueOf(4)));
    ConsumableEquipment newConsumableEquipment4 = consumableEquipmentService.createConsumableEquipment(
        new ConsumableEquipment("Tissue Paper", "1 Box 20pcs", ItemTypeEnum.CONSUMABLE, 1000,
            BigDecimal.valueOf(2)));
    ConsumableEquipment newConsumableEquipment5 = consumableEquipmentService.createConsumableEquipment(
        new ConsumableEquipment("Disposable Needles", "1 Box 5pcs", ItemTypeEnum.CONSUMABLE, 100,
            BigDecimal.valueOf(3)));

    Facility f = facilityService.findFacilityById(Long.parseLong("1"));
    Facility f1 = facilityService.findFacilityById(Long.parseLong("2"));

    AllocatedInventory item1 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(),
        newConsumableEquipment1.getInventoryItemId(), 10, 1);
    AllocatedInventory item2 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(),
        newConsumableEquipment2.getInventoryItemId(), 10, 1);
    AllocatedInventory item3 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(),
        newConsumableEquipment3.getInventoryItemId(), 10, 1);
    AllocatedInventory item4 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(),
        newConsumableEquipment4.getInventoryItemId(), 10, 1);
    AllocatedInventory item5 = allocatedInventoryService.createAllocatedInventory(f.getFacilityId(),
        newConsumableEquipment5.getInventoryItemId(), 10, 1);
//      AllocatedInventory item6 = allocatedInventoryService.createAllocatedInventory(f1.getFacilityId(), newConsumableEquipment5.getInventoryItemId(), 10, 1);

  }

  private void createMedicationData() {
    Collection<AllergenEnum> allergenList1 = new ArrayList<>();
    Collection<AllergenEnum> allergenList2 = new ArrayList<>();
    allergenList2.add(AllergenEnum.PENICILLIN_V);
    allergenList2.add(AllergenEnum.AMOXICILLIN);

    DrugRestriction newDrugRestriction1 = drugRestrictionService.createDrugRestriction(
            new DrugRestriction("Warfarin"));
    DrugRestriction newDrugRestriction2 = drugRestrictionService.createDrugRestriction(
            new DrugRestriction("Paracetamol"));
    List<DrugRestriction> drugList1 = new ArrayList<>();
    List<DrugRestriction> drugList2 = new ArrayList<>();
    drugList2.add(newDrugRestriction1);
    List<DrugRestriction> drugList3 = new ArrayList<>();
    drugList3.add(newDrugRestriction2);


    Medication newMedication1 = medicationService.createMedication(
            new Medication("Paracetamol 500 mg Tablets (12 pieces)", "500mg per piece", ItemTypeEnum.MEDICINE, 100,
                    BigDecimal.TEN, BigDecimal.TEN, allergenList1, "", drugList2));
    Medication newMedication2 = medicationService.createMedication(
            new Medication("Cetirizine 10mg Tablets (12 pieces)", "10mg per piece", ItemTypeEnum.MEDICINE, 100,
                    BigDecimal.valueOf(5), BigDecimal.TEN, allergenList1, "Do not take with alcohol", drugList1));
    Medication newMedication3 = medicationService.createMedication(
            new Medication("Augmentin 625mg Tablets (12 pieces)", "625mg per piece", ItemTypeEnum.MEDICINE, 50,
                    BigDecimal.valueOf(4), BigDecimal.TEN, allergenList2, "", drugList1));
    Medication newMedication4 = medicationService.createMedication(
            new Medication("Metformin 500mg Tablets (12 pieces)", "500mg per piece", ItemTypeEnum.MEDICINE, 1000,
                    BigDecimal.valueOf(2), BigDecimal.TEN, allergenList1, "Do not take with alcohol", drugList1));
    Medication newMedication5 = medicationService.createMedication(
            new Medication("Augmentin 228mg Suspension (1 bottle)", "5ml per bottle", ItemTypeEnum.MEDICINE, 100,
                    BigDecimal.valueOf(3), BigDecimal.TEN, allergenList2, "", drugList1));
    Medication newMedication6 = medicationService.createMedication(
            new Medication("Warfarin 1mg Tablet (1 piece)", "1mg per piece", ItemTypeEnum.MEDICINE, 10000,
                    BigDecimal.valueOf(1), BigDecimal.valueOf(1), allergenList1, "", drugList3));
    Medication newMedication7 = medicationService.createMedication(
            new Medication("Warfarin 3mg Tablet (28 pieces)", "3mg per piece", ItemTypeEnum.MEDICINE, 10000,
                    BigDecimal.valueOf(4), BigDecimal.valueOf(5), allergenList1, "", drugList3));
    Medication newMedication8 = medicationService.createMedication(
            new Medication("Warfarin 3mg Tablet (1 piece)", "1mg per piece", ItemTypeEnum.MEDICINE, 10000,
                    BigDecimal.valueOf(1), BigDecimal.valueOf(1), allergenList1, "", drugList3));
  }

  private void createServiceItemData() {

    Unit unit1 = unitRepository.findById(1L).get();
    Unit unit5 = unitRepository.findById(5L).get();
    Unit unit3 = unitRepository.findById(12L).get();


    ServiceItem newServiceItem1 = serviceItemService.createServiceItem(Long.parseLong("1"),
            new ServiceItem("General Cardiology Consultation", "Consultation", ItemTypeEnum.OUTPATIENT,
                    BigDecimal.valueOf(400)));
    newServiceItem1.setUnit(unit1);

    ServiceItem newServiceItem2 = serviceItemService.createServiceItem(Long.parseLong("1"),
            new ServiceItem("Heart Transplant", "Surgery", ItemTypeEnum.INPATIENT,
                    BigDecimal.valueOf(2500)));
    newServiceItem2.setUnit(unit1);

    ServiceItem newServiceItem3 = serviceItemService.createServiceItem(Long.parseLong("11"),
            new ServiceItem("ECG", "ECG", ItemTypeEnum.OUTPATIENT,
                    BigDecimal.valueOf(80)));
    newServiceItem3.setUnit(unit1);

    ServiceItem newServiceItem4 = serviceItemService.createServiceItem(Long.parseLong("5"),
            new ServiceItem("A&E consultation", "Consultation", ItemTypeEnum.OUTPATIENT,
                    BigDecimal.valueOf(100)));
    newServiceItem4.setUnit(unit5);

    ServiceItem newServiceItem5 = serviceItemService.createServiceItem(Long.parseLong("5"),
            new ServiceItem("1-way Ambulance", "Ambulance", ItemTypeEnum.OUTPATIENT,
                    BigDecimal.valueOf(90)));
    newServiceItem5.setUnit(unit5);
  }



  public void createSubsidyData() {
    // Calculate the minimum date of birth for individuals above 5 years old
    LocalDateTime minDOB = LocalDateTime.now();
    Subsidy subsidy1 = subsidyService.createSubsidy(BigDecimal.valueOf(0.5), ItemTypeEnum.MEDICINE, minDOB,
            "Male", "All", "All", "SG Males Medicine Subsidy",
            "Subsidised rates for all Singaporean Males");
    Subsidy subsidy2 = subsidyService.createSubsidy(BigDecimal.valueOf(0.5), ItemTypeEnum.INPATIENT, minDOB,
            "Male", "All", "All", "SG Males Service Subsidy",
            "Subsidised rates for all Singaporean Males");

    Subsidy subsidy3 = subsidyService.createSubsidy(BigDecimal.valueOf(0.7), ItemTypeEnum.INPATIENT, minDOB,
            "Female", "All", "All", "SG Females Service Subsidy",
            "Subsidised rates for all Singaporean Females");
    Subsidy subsidy4 = subsidyService.createSubsidy(BigDecimal.valueOf(0.7), ItemTypeEnum.MEDICINE, minDOB,
            "Female", "All", "All", "SG Females Medicine Subsidy",
            "Subsidised rates for all Singaporean Females");
  }

  public void createTransactionItems() {
    ConsumableEquipment consumableEquipment = (ConsumableEquipment) inventoryItemRepository.findById(Long.parseLong("1")).get();
    Medication medication= (Medication) inventoryItemRepository.findById(Long.parseLong("6")).get();
    ServiceItem serviceItem = (ServiceItem) inventoryItemRepository.findById(Long.parseLong("14")).get();

    InventoryItem inventoryItem = inventoryItemRepository.findById(Long.parseLong("6")).get();


    List<PrescriptionRecord> prList = prescriptionRecordService.getAllPrescriptionRecords();
    for (int i = 0; i < prList.size(); i ++) {
      PrescriptionRecord pr = prList.get(i);
      pr.setInventoryItem(inventoryItem);
      prescriptionRecordRepository.save(pr);
    }

    //For patient 1
//    transactionItemService.addToCartDataLoader(Long.parseLong("1"), new TransactionItem("Consumable",
//            "Consumable", 1,
//            consumableEquipment.getRestockPricePerQuantity().multiply(BigDecimal.valueOf(1)),
//            consumableEquipment));
    transactionItemService.addToCartDataLoader(Long.parseLong("1"), new TransactionItem(medication.getInventoryItemName(),
            "Medication", 1,
            medication.getRestockPricePerQuantity().multiply(BigDecimal.valueOf(1)),
            medication));
    transactionItemService.addToCartDataLoader(Long.parseLong("1"), new TransactionItem(serviceItem.getInventoryItemName(),
            "Service", 1,
            serviceItem.getRetailPricePerQuantity().multiply(BigDecimal.valueOf(1)),
            serviceItem));

    //For patient 2
//    transactionItemService.addToCartDataLoader(Long.parseLong("2"), new TransactionItem("Consumable",
//            "Consumable", 1,
//            consumableEquipment.getRestockPricePerQuantity().multiply(BigDecimal.valueOf(1)),
//            consumableEquipment));
    transactionItemService.addToCartDataLoader(Long.parseLong("2"), new TransactionItem(medication.getInventoryItemName(),
            "Medication", 1,
            medication.getRestockPricePerQuantity().multiply(BigDecimal.valueOf(1)),
            medication));
    transactionItemService.addToCartDataLoader(Long.parseLong("2"), new TransactionItem(serviceItem.getInventoryItemName(),
            "Service", 1,
            serviceItem.getRetailPricePerQuantity().multiply(BigDecimal.valueOf(1)),
            serviceItem));
  }

  public void createInvoice() {
    transactionItemService.checkout(Long.parseLong("1"));
    transactionItemService.checkout(Long.parseLong("2"));
    invoiceService.createInsuranceClaim(Long.parseLong("1"), BigDecimal.valueOf(95),
            "Great Eastern", true);
    invoiceService.createMedishieldClaim(Long.parseLong("1"), BigDecimal.valueOf(110));
  }

  private void createAdmissionData() {
    //admissionService.createAdmission(2,"Cancer", 1L, 5L);
  }
}