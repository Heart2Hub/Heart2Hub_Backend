package com.Heart2Hub.Heart2Hub_Backend.data;

import com.Heart2Hub.Heart2Hub_Backend.Heart2HubBackendApplication;
import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.*;
import com.Heart2Hub.Heart2Hub_Backend.service.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Component("loader")
@Transactional
public class DataLoader implements CommandLineRunner {

  static Logger logger = Heart2HubBackendApplication.logger;

  private final StaffService staffService;

  private final DepartmentService departmentService;

  private final SubDepartmentService subDepartmentService;

  private final FacilityService facilityService;

  private final PatientService patientService;

  private final NextOfKinRecordService nextOfKinRecordService;

  private final PrescriptionRecordService prescriptionRecordService;

  private final ProblemRecordService problemRecordService;

  private final MedicalHistoryRecordService medicalHistoryRecordService;

  private final TreatmentPlanRecordService treatmentPlanRecordService;
  private final AuthenticationManager authenticationManager;

  public DataLoader(StaffService staffService, DepartmentService departmentService, SubDepartmentService subDepartmentService, FacilityService facilityService, PatientService patientService, NextOfKinRecordService nextOfKinRecordService, PrescriptionRecordService prescriptionRecordService, ProblemRecordService problemRecordService, MedicalHistoryRecordService medicalHistoryRecordService, TreatmentPlanRecordService treatmentPlanRecordService, AuthenticationManager authenticationManager) {
    this.staffService = staffService;
    this.departmentService = departmentService;
    this.subDepartmentService = subDepartmentService;
    this.facilityService = facilityService;
    this.patientService = patientService;
    this.nextOfKinRecordService = nextOfKinRecordService;
    this.prescriptionRecordService = prescriptionRecordService;
    this.problemRecordService = problemRecordService;
    this.medicalHistoryRecordService = medicalHistoryRecordService;
    this.treatmentPlanRecordService = treatmentPlanRecordService;
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

    // Create staff data
    createStaffData();

    // Set auth context using staff1
    Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("staff1", "password1"));
    SecurityContext sc = SecurityContextHolder.getContext();
    sc.setAuthentication(auth);

    // Create other data
    createPatientData();
    createDepartmentData();
    createSubDepartmentData();
    createFacilityData();

    long endTime = System.currentTimeMillis();
    String message =
        "Time taken to Load Initial Test Data: " + (endTime - startTime) / 1000 + " seconds";
    logger.log(Level.INFO, message);
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
//    TO-DO: WARD CREATION
//    departmentService.createDepartment(new Department("Ward A-1"));
//    departmentService.createDepartment(new Department("Ward A-2"));
//    departmentService.createDepartment(new Department("Ward A-3"));
//    departmentService.createDepartment(new Department("Ward B-1"));
//    departmentService.createDepartment(new Department("Ward B-2"));
//    departmentService.createDepartment(new Department("Ward B-3"));
//    departmentService.createDepartment(new Department("Ward C-1"));
//    departmentService.createDepartment(new Department("Ward C-2"));
//    departmentService.createDepartment(new Department("Ward C-3"));
  }

  private void createSubDepartmentData() {
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Interventional Cardiology", "Specializes in minimally invasive procedures to treat heart conditions"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Electrophysiology (EP) Lab", "Focuses on the diagnosis and treatment of heart rhythm disorders, including procedures like cardiac ablation"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Catheterization Lab", "Performs diagnostic procedures like cardiac catheterization and coronary angiography"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Heart Failure Clinic", "Provides comprehensive care for patients with congestive heart failure, including medication management and lifestyle counseling"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Rehabilitation", "Offers programs to help patients recover from heart-related surgeries or events through exercise and lifestyle modifications"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Echocardiography Unit", "Specializes in using echocardiograms (ultrasound of the heart) for diagnostic purposes"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Nuclear Cardiology Unit", "Utilizes nuclear imaging techniques to assess blood flow to the heart and cardiac function"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Imaging Center", "Provides various imaging services, including MRI and CT scans, for detailed cardiac assessments"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Cardiac Telemetry Unit", "Monitors patients' heart rhythms continuously, often used in critical care settings"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Adult Congenital Heart Disease Clinic", "Focuses on the care of adults born with congenital heart defects"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Preventive Cardiology Clinic", "Emphasizes heart disease prevention through risk assessment, lifestyle changes, and medication when necessary"));
    subDepartmentService.createSubDepartment(1L, new SubDepartment("Heart Transplantation Unit", "Manages patients in need of heart transplants and provides post-transplant care"));
    subDepartmentService.createSubDepartment(2L, new SubDepartment("Orthopedic Surgery", "Specializes in surgical treatments for musculoskeletal conditions"));
    subDepartmentService.createSubDepartment(2L, new SubDepartment("Physical Therapy", "Provides rehabilitation and physical therapy services for orthopedic patients"));
    subDepartmentService.createSubDepartment(2L, new SubDepartment("Sports Medicine", "Focuses on injuries related to sports and physical activity"));
    subDepartmentService.createSubDepartment(2L, new SubDepartment("Orthopedic Trauma Center", "Manages fractures and severe musculoskeletal injuries"));
    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Cardiology", "Specializes in heart conditions in children"));
    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Neurology", "Focuses on neurological conditions in children"));
    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Oncology", "Treats cancer and blood disorders in children"));
    subDepartmentService.createSubDepartment(3L, new SubDepartment("Pediatric Emergency Care", "Provides emergency medical care for children"));
    subDepartmentService.createSubDepartment(4L, new SubDepartment("Neurophysiology Lab", "Performs diagnostic tests to assess nervous system function"));
    subDepartmentService.createSubDepartment(4L, new SubDepartment("Neurocritical Care Unit", "Specializes in the intensive care of neurological patients"));
    subDepartmentService.createSubDepartment(4L, new SubDepartment("Stroke Center", "Provides advanced care for stroke patients"));
    subDepartmentService.createSubDepartment(4L, new SubDepartment("Neuropsychology Clinic", "Evaluates cognitive and behavioral aspects of neurological disorders"));
    subDepartmentService.createSubDepartment(5L, new SubDepartment("Emergency Room (ER)", "Offers immediate medical care for various emergencies"));
    subDepartmentService.createSubDepartment(5L, new SubDepartment("Trauma Center", "Handles severe injuries and trauma cases"));
    subDepartmentService.createSubDepartment(5L, new SubDepartment("Toxicology Unit", "Manages poisoning and overdose cases"));
    subDepartmentService.createSubDepartment(5L, new SubDepartment("Emergency Psychiatry", "Addresses psychiatric emergencies in the ER"));
    subDepartmentService.createSubDepartment(6L, new SubDepartment("General Surgery", "Performs a wide range of surgical procedures"));
    subDepartmentService.createSubDepartment(6L, new SubDepartment("Cardiothoracic Surgery", "Specializes in heart and lung surgeries"));
    subDepartmentService.createSubDepartment(6L, new SubDepartment("Plastic and Reconstructive Surgery", "Focuses on cosmetic and reconstructive procedures"));
    subDepartmentService.createSubDepartment(6L, new SubDepartment("Minimally Invasive Surgery", "Utilizes minimally invasive techniques for surgery"));
    subDepartmentService.createSubDepartment(7L, new SubDepartment("Ophthalmic Surgery", "Performs surgeries related to the eyes"));
    subDepartmentService.createSubDepartment(7L, new SubDepartment("Retina Clinic", "Specializes in diseases of the retina and vitreous"));
    subDepartmentService.createSubDepartment(7L, new SubDepartment("Cornea and External Disease Unit", "Focuses on corneal and external eye conditions"));
    subDepartmentService.createSubDepartment(7L, new SubDepartment("Pediatric Ophthalmology", "Provides eye care for children"));
    subDepartmentService.createSubDepartment(8L, new SubDepartment("Adult Psychiatry", "Treats mental health conditions in adults"));
    subDepartmentService.createSubDepartment(8L, new SubDepartment("Child and Adolescent Psychiatry", "Focuses on mental health care for children and teens"));
    subDepartmentService.createSubDepartment(8L, new SubDepartment("Addiction Psychiatry", "Addresses substance abuse and addiction disorders"));
    subDepartmentService.createSubDepartment(8L, new SubDepartment("Forensic Psychiatry", "Deals with mental health in the legal context"));
//    TO-DO: WARD ROOM CREATION
//    for (long L = 9L; L <= 17L; L++) {
//      subDepartmentService.createSubDepartment(L, new SubDepartment("Room 1", ""));
//      subDepartmentService.createSubDepartment(L, new SubDepartment("Room 2", ""));
//      subDepartmentService.createSubDepartment(L, new SubDepartment("Room 3", ""));
//    }
  }

  private void createFacilityData() {
    // For Sub Department Facility Creation
    for (long L = 1L; L <= 40L; L++) {
      facilityService.createFacility(L, new Facility("Consultation Room 1", "","",2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.CONSULTATION_ROOM));
      facilityService.createFacility(L, new Facility("Triage Room 1", "","",2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.TRIAGE_ROOM));
      facilityService.createFacility(L, new Facility("Triage Room 2", "","",2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.TRIAGE_ROOM));
    }
//    TO-DO: WARD BED CREATION
//    for (long L = 1L; L <= 40L; L++) {
//      facilityService.createFacility(L, new Facility("Consultation Room 1", "","",2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.CONSULTATION_ROOM));
//      facilityService.createFacility(L, new Facility("Bed 1", "","",2, FacilityStatusEnum.AVAILABLE, FacilityTypeEnum.WARD_BED));
//    }
  }

  private void createStaffData() {
    staffService.createStaff("staff1", "password1", "staff", "1", 90000001l, StaffRoleEnum.ADMIN);
    staffService.createStaff("staff2", "password2", "staff", "2", 90000002l, StaffRoleEnum.DOCTOR);
  }

  private void createPatientData() {
    Patient newPatient = patientService.createPatient(new Patient("patient1","password1"), new ElectronicHealthRecord("S9983422D","Adam","Lai", LocalDateTime.of(1978, 9, 16, 15, 30, 0), "Singapore", "Male", "Chinese", "Singaporean", "Pasir Ris St 42", "81348699"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient.getPatientId(),new NextOfKinRecord("Father", "S5882617D"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient.getPatientId(),new NextOfKinRecord("Mother", "S6882617D"));
    nextOfKinRecordService.createNextOfKinRecord(newPatient.getPatientId(),new NextOfKinRecord("Brother", "S7882617D"));
    prescriptionRecordService.createPrescriptionRecord(newPatient.getPatientId(),new PrescriptionRecord(LocalDateTime.of(1999, 9, 16, 15, 30, 0),"Panadol", 3, 1, "Pain Relief for Headache", "Stop when symptoms are gone", "Doctor Wen Jie", PrescriptionStatusEnum.UNCOLLECTED));
    prescriptionRecordService.createPrescriptionRecord(newPatient.getPatientId(), new PrescriptionRecord(LocalDateTime.of(2000, 3, 10, 14, 45, 0), "Aspirin", 2, 1, "Pain Relief for Backache", "Take with food", "Doctor Sarah Smith", PrescriptionStatusEnum.COLLECTED));
    problemRecordService.createProblemRecord(newPatient.getPatientId(), new ProblemRecord("AIDs", "Doctor Wen Jie", LocalDateTime.of(1999, 9, 16, 15, 30, 0), PriorityEnum.HIGH, ProblemTypeEnum.INFECTIOUS_DISEASES));
    problemRecordService.createProblemRecord(newPatient.getPatientId(), new ProblemRecord("Diabetes", "Doctor Sarah Smith", LocalDateTime.of(2001, 5, 20, 9, 15, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
    medicalHistoryRecordService.createMedicalHistoryRecord(newPatient.getPatientId(), new MedicalHistoryRecord("Smoking", "Doctor Wen Jie", LocalDateTime.of(1998, 9, 16, 15, 30, 0), LocalDateTime.of(1999, 9, 16, 15, 30, 0), PriorityEnum.LOW, ProblemTypeEnum.OTHERS));
    medicalHistoryRecordService.createMedicalHistoryRecord(newPatient.getPatientId(), new MedicalHistoryRecord("Allergies to Pollen", "Doctor Sarah Smith", LocalDateTime.of(2000, 8, 5, 11, 30, 0), LocalDateTime.of(2001, 3, 15, 13, 45, 0), PriorityEnum.HIGH, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC));
    treatmentPlanRecordService.createTreatmentPlanRecord(newPatient.getPatientId(), new TreatmentPlanRecord("Dialysis", "Doctor Wen Jie", new ArrayList<>(), LocalDateTime.of(1998, 9, 16, 15, 30, 0), LocalDateTime.of(1999, 9, 16, 15, 30, 0), TreatmentPlanTypeEnum.PREVENTIVE_CARE_PLAN));
  }

}

