package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class ShiftService {


  private final ShiftRepository shiftRepository;
  private final StaffRepository staffRepository;
  private final FacilityBookingRepository facilityBookingRepository;
  private final LeaveRepository leaveRepository;
  private final FacilityRepository facilityRepository;
  private final FacilityBookingService facilityBookingService;
  private final FacilityService facilityService;
  private final ShiftConstraintsRepository shiftConstraintsRepository;
  private final WardRepository wardRepository;

  public ShiftService(ShiftRepository shiftRepository, StaffRepository staffRepository, FacilityBookingService facilityBookingService, FacilityBookingRepository facilityBookingRepository, LeaveRepository leaveRepository, FacilityRepository facilityRepository, FacilityService facilityService, ShiftConstraintsRepository shiftConstraintsRepository, WardRepository wardRepository) {
    this.shiftRepository = shiftRepository;
    this.staffRepository = staffRepository;
    this.facilityBookingService = facilityBookingService;
    this.facilityBookingRepository = facilityBookingRepository;
    this.leaveRepository = leaveRepository;
    this.facilityRepository = facilityRepository;
    this.facilityService = facilityService;
    this.shiftConstraintsRepository = shiftConstraintsRepository;
    this.wardRepository = wardRepository;
  }

  public boolean isLoggedInUserHead() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean isHead = false;
    if (authentication != null) {
      User user = (User) authentication.getPrincipal();
      Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
      if (currStaff.isPresent()) {
        isHead = currStaff.get().getIsHead();
      }
    }
    return isHead;
  }

  public Shift createShift(String staffUsername, Long facilityId, Shift newShift) throws UnableToCreateShiftException, StaffNotFoundException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot allocate shifts as he/she is not a head.");
    }
    try {
        Optional<Staff> optionalStaff = staffRepository.findByUsername(staffUsername);
        if (optionalStaff.isPresent()) {
          Staff assignedStaff = optionalStaff.get();
          if (checkShiftConditions(newShift, assignedStaff)) {
            assignedStaff.getListOfShifts().add(newShift);
            newShift.setStaff(assignedStaff);

            if (facilityId != null) {
              Optional<Facility> f = facilityRepository.findById(facilityId);
              if (f.isPresent()) {
                Facility facility = f.get();
                List<FacilityBooking> facilityBookingList = facilityBookingService.getAllFacilityBookingsWithinTime(facility.getName(), newShift.getStartTime(), newShift.getEndTime());
                System.out.println(facilityBookingList.size());
                if (facilityBookingList.size() >= facility.getCapacity()) {
                  throw new UnableToCreateShiftException("Staff cannot work at this facility as it has reached its maximum capacity.");
                }
                FacilityBooking fb = new FacilityBooking(newShift.getStartTime(), newShift.getEndTime(), "Shift for staff " + assignedStaff.getUsername());
                fb.setStaffUsername(staffUsername);
                FacilityBooking newFacilityBooking = facilityBookingService.createBooking(fb, facilityId);
                newShift.setFacilityBooking(newFacilityBooking);
                newFacilityBooking.setShift(newShift);
              }
            }

            return shiftRepository.save(newShift);
          } else {
            throw new UnableToCreateShiftException("Shift does not meet predefined shift constraints");
          }
        } else {
          throw new StaffNotFoundException("Staff with username " + staffUsername + " not found!");
        }
    } catch (Exception ex) {
      throw new UnableToCreateShiftException(ex.getMessage());
    }
  }

  public boolean checkShiftConditions(Shift newShift, Staff assignedStaff) throws UnableToCreateShiftException, StaffNotFoundException {
    LocalDateTime startTime = newShift.getStartTime();
    LocalDateTime endTime = newShift.getEndTime();
    if (startTime == null || endTime == null) {
      throw new UnableToCreateShiftException("Start time and end time must be present.");
    }
    List<Shift> shifts = shiftRepository.findShiftsByStaff(assignedStaff);

    if (startTime.isAfter(endTime)) {
      throw new UnableToCreateShiftException("Start time cannot be later than end time.");
    }
    if (!startTime.toLocalDate().equals(endTime.toLocalDate())) {
      throw new UnableToCreateShiftException("Shifts have to be allocated within the same day.");
    }
    int HOURS_BETWEEN_DAY_NIGHT_SHIFTS = 8;
    int HOURS_AFTER_24HR_SHIFT = 24;
    long MAX_HOURS_PER_WEEK = 56;

    for (Shift shift : shifts) {
      if (newShift.getShiftId() == null || newShift.getShiftId() != shift.getShiftId()) {
        if (startTime.toLocalDate().equals(shift.getStartTime().toLocalDate())) {
          throw new UnableToCreateShiftException("There is already a shift allocated to staff " + assignedStaff.getUsername() + " on " + startTime.toLocalDate());
        }
        // If staff has a 24 hour shift
        if (shift.getStartTime().getHour() == 0 && shift.getStartTime().getMinute() == 0 && shift.getEndTime().getHour() == 23 && shift.getEndTime().getMinute() == 59) {
          if (shift.getEndTime().compareTo(startTime) == -1 && shift.getEndTime().until(startTime, ChronoUnit.HOURS) < HOURS_AFTER_24HR_SHIFT) {
            throw new UnableToCreateShiftException("Staff has worked a 24h shift the previous day, and cannot work on this day.");
          }
        }
        if (shift.getEndTime().compareTo(startTime) == -1 && shift.getEndTime().until(startTime, ChronoUnit.HOURS) < HOURS_BETWEEN_DAY_NIGHT_SHIFTS) {
          throw new UnableToCreateShiftException("Staff has worked a night shift the previous day, and cannot work the morning shift today.");
        }
        if (shift.getStartTime().compareTo(endTime) == 1 && endTime.until(shift.getStartTime(), ChronoUnit.HOURS) < HOURS_BETWEEN_DAY_NIGHT_SHIFTS) {
          throw new UnableToCreateShiftException("Staff has a day shift the following the day, and cannot work the night shift today.");
        }
      }
    }

    List<Shift> weeklyShifts = viewWeeklyRoster(assignedStaff.getUsername(), newShift.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    if (newShift.getShiftId() == null && weeklyShifts.size() == 6) {
      throw new UnableToCreateShiftException("Staff cannot work today as he/she requires at least 1 day of rest per week.");
    }
    long totalHours = 0;
    for (Shift shift : weeklyShifts) {
      totalHours += shift.getStartTime().until(shift.getEndTime(), ChronoUnit.HOURS);
    }
    totalHours += newShift.getStartTime().until(newShift.getEndTime(), ChronoUnit.HOURS);
    if (totalHours > MAX_HOURS_PER_WEEK) {
      throw new UnableToCreateShiftException("Staff cannot work today as he/she exceeds the maximum working hours per week (56 hours).");
    }

    // TODO: CHECK FOR LEAVES
    List<Leave> listOfLeaves = leaveRepository.findByStaff(assignedStaff);
    for (Leave leave : listOfLeaves) {
      if (startTime.isEqual(leave.getStartDate()) || startTime.isEqual(leave.getEndDate()) ||
              startTime.isAfter(leave.getStartDate()) && startTime.isBefore(leave.getEndDate())) {
        throw new UnableToCreateShiftException("Staff is on leave on this date.");
      }
    }

    return true;
  }

  public List<Shift> getAllShiftsByRole(String role, String unit) throws StaffRoleNotFoundException, UnableToCreateShiftException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot view all shifts as he/she is not a head.");
    }
    try {
      StaffRoleEnum staffRoleEnum = StaffRoleEnum.valueOf(role.toUpperCase());
      List<Staff> staffList = staffRepository.findByStaffRoleEnumAndUnitNameEqualsIgnoreCase(staffRoleEnum, unit);
      List<Shift> listOfShifts = new ArrayList<>();
      for (Staff staff : staffList) {
        List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
        listOfShifts.addAll(shifts);
      }
      return listOfShifts;
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }

  public void deleteShift(Long shiftId) throws ShiftNotFoundException, UnableToCreateShiftException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot delete shifts as he/she is not a head.");
    }
    try {
      Optional<Shift> shiftOptional = shiftRepository.findById(shiftId);
      if (shiftOptional.isPresent()) {
        Shift shift = shiftOptional.get();
        Staff staff = shift.getStaff();
        staff.getListOfShifts().remove(shift);
        if (shift.getFacilityBooking() != null) {
          facilityBookingRepository.delete(shift.getFacilityBooking());
        }
        shiftRepository.delete(shift);
      } else {
        throw new ShiftNotFoundException("Shift with ID: " + shiftId + " is not found");
      }
    } catch (Exception ex) {
      throw new ShiftNotFoundException(ex.getMessage());
    }
  }

  public Shift updateShift(Long shiftId, Long facilityId, Shift updatedShift) throws ShiftNotFoundException, UnableToCreateShiftException {
    if (!isLoggedInUserHead()) {
      throw new UnableToCreateShiftException("Staff cannot update shifts as he/she is not a head.");
    }
    try {
      Optional<Shift> shiftOptional = shiftRepository.findById(shiftId);
      if (shiftOptional.isPresent()) {
        Shift shift = shiftOptional.get();
        if (updatedShift.getStartTime() != null) shift.setStartTime(updatedShift.getStartTime());
        if (updatedShift.getEndTime() != null) shift.setEndTime(updatedShift.getEndTime());
        if (updatedShift.getComments() != null) shift.setComments(updatedShift.getComments());
        if (shift.getStaff().getStaffRoleEnum() != StaffRoleEnum.NURSE || shift.getFacilityBooking() != null) {
          if (shift.getFacilityBooking().getFacility().getFacilityId() != facilityId) {
            Optional<Facility> f = facilityRepository.findById(facilityId);
            if (f.isPresent()) {
              Facility facility = f.get();
              List<FacilityBooking> facilityBookingList = facilityBookingService.getAllFacilityBookingsWithinTime(facility.getName(), shift.getStartTime(), shift.getEndTime());
              if (facilityBookingList.size() >= facility.getCapacity()) {
                throw new UnableToCreateShiftException("Staff cannot work at this facility as it has reached its maximum capacity.");
              }
              facilityBookingService.updateBooking(shift.getFacilityBooking().getFacilityBookingId(), facilityId);
            }
          }
        }
        if (checkShiftConditions(shift, shift.getStaff())) {
          shiftRepository.save(shift);
        } else {
          throw new UnableToCreateShiftException("Shift does not meet predefined shift constraints");
        }
        return shift;
      } else {
        throw new ShiftNotFoundException("Shift with ID: " + shiftId + " is not found");
      }
    } catch (Exception ex) {
      throw new ShiftNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewMonthlyRoster(String username, Integer month, Integer year) throws StaffNotFoundException {
    try {
      Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
      if (optionalStaff.isPresent()) {
        Staff staff = optionalStaff.get();
        List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
        List<Shift> listOfShifts = new ArrayList<>();
        for (Shift shift : shifts) {
          if (shift.getStartTime().getMonthValue() == month && shift.getStartTime().getYear() == year) {
            listOfShifts.add(shift);
          }
        }
        return listOfShifts;
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " is not found.");
      }
    } catch (Exception ex) {
      throw new StaffNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewWeeklyRoster(String username, String date) throws StaffNotFoundException {
    try {
      Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
      if (optionalStaff.isPresent()) {
        Staff staff = optionalStaff.get();
        List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
        List<Shift> listOfShifts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = now.with(DayOfWeek.SUNDAY).withHour(23).withMinute(59).withSecond(59);
        for (Shift shift : shifts) {
          if (!shift.getStartTime().isBefore(startOfWeek) && !shift.getEndTime().isAfter(endOfWeek)) {
            listOfShifts.add(shift);
          }
        }
        listOfShifts.sort(Comparator.comparing(Shift::getStartTime));
        return listOfShifts;
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " is not found.");
      }
    } catch (Exception ex) {
      throw new StaffNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewDailyRoster(String date, String role, String unitName) throws StaffRoleNotFoundException {
    try {
      StaffRoleEnum staffRoleEnum = StaffRoleEnum.valueOf(role.toUpperCase());
      LocalDateTime start = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      List<Shift> shiftList = shiftRepository.findByStaffStaffRoleEnumAndStaffUnitNameContainsIgnoreCase(staffRoleEnum, unitName);
      List<Shift> shifts = new ArrayList<>();
      for (Shift shift : shiftList) {
        if (shift.getStartTime().toLocalDate().isEqual(start.toLocalDate())) {
          shifts.add(shift);
        }
      }
      return shifts;
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> viewOverallRoster(String username) throws StaffRoleNotFoundException {
    try {
      Optional<Staff> optionalStaff = staffRepository.findByUsername(username);
      if (optionalStaff.isPresent()) {
        Staff staff = optionalStaff.get();
        return shiftRepository.findShiftsByStaff(staff);
      } else {
        throw new StaffNotFoundException("Staff with username " + username + " is not found.");
      }
    } catch (Exception ex) {
      throw new StaffNotFoundException(ex.getMessage());
    }
  }

  public List<Shift> getAllShiftsForStaffFromDates(String username, String start, String end) throws StaffRoleNotFoundException {
    try {
      LocalDateTime startDate = LocalDateTime.parse(start + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
      LocalDateTime endDate = LocalDateTime.parse(end + " 23:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return shiftRepository.findByStaffUsernameAndStartTimeBetween(username, startDate, endDate);
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }

  public void automaticallyAllocateShifts(String start, String end, String role, String department, Integer shift1, Integer shift2, Integer shift3) throws InterruptedException {
    LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
    long weeks = daysBetween / 7;
    int numShifts = shift1 + shift2 + shift3;

    LocalDate currentDate = startDate;

    List<Staff> staffList = staffRepository.findByStaffRoleEnumAndUnitNameEqualsIgnoreCase(StaffRoleEnum.valueOf(role.toUpperCase()), department);
    List<Facility> fList = facilityRepository.findByDepartmentNameContainingIgnoreCase(department);
    boolean isWard = false;
    if (fList.isEmpty() && role.equalsIgnoreCase("NURSE")) isWard = true;
    Long[] range = facilityService.getFacilityIdRange(department);
    HashMap<Long, Integer> capacityMap = new HashMap<>();
    long s = range[0], e = range[1];
    if (!isWard) {
      if (role.equalsIgnoreCase("DOCTOR")) {
        e = s + 2;
      } else if (role.equalsIgnoreCase("NURSE")) {
        s = s + 3;
        e = s + 5;
      } else if (role.equalsIgnoreCase("ADMIN")) {
        s = s + 8;
        e = s + 9;
      } else if (role.equalsIgnoreCase("DIAGNOSTIC_RADIOGRAPHERS")) {
        s = s + 10;
        e = s + 11;
      } else if (role.equalsIgnoreCase("DIETITIANS")) {
        s = s + 12;
        e = s + 13;
      } else if (role.equalsIgnoreCase("OCCUPATIONAL_THERAPISTS")) {
        s = s + 14;
        e = s + 15;
      } else if (role.equalsIgnoreCase("PSYCHOLOGISTS")) {
        s = e - 4;
      } else if (role.equalsIgnoreCase("PHARMACIST")) {
        e = s + 2;
      }
      for (long a=s; a<=e; a++) {
        capacityMap.put(a,0);
      }
    }

    for (int week=0; week<weeks; week++) {
      for (int i=0; i<7; i++) {
        int day = currentDate.getDayOfMonth();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        long id = 0;
        for (Staff staff : staffList) {
          String dayStr = day + "";
          int shiftToPick = -1;
          if (day < 10) dayStr = "0" + day;
          String date = year+"-"+month+"-"+dayStr;
          List<Shift> listOfShifts = viewDailyRoster(date, role, department);
          List<ShiftConstraints> listOfShiftConstraints = new ArrayList<>();
          List<Facility> facilityList = facilityRepository.findByDepartmentNameContainingIgnoreCase(department);
          for (Facility facility : facilityList) {
            List<ShiftConstraints> temp = shiftConstraintsRepository.findByStaffRoleEnumAndFacilityName(StaffRoleEnum.valueOf(role.toUpperCase()), facility.getName());
            listOfShiftConstraints.addAll(temp);
          }
          HashMap<String, Integer> mapOfMinPax = new HashMap<>();
          LocalTime shift1Start = LocalTime.of(0,0,0);
          LocalTime shift2Start = LocalTime.of(8,0,0);
          LocalTime shift3Start = LocalTime.of(16,0,0);
          LocalTime shift3End = LocalTime.of(23,59,0);
          for (ShiftConstraints sc : listOfShiftConstraints) {
            LocalTime startTime1 = sc.getStartTime();
            LocalTime endTime1 = sc.getEndTime();
            if (startTime1.equals(shift1Start) && endTime1.equals(shift2Start)) {
              mapOfMinPax.put(("1+"+sc.getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("1+"+sc.getFacility().getName()), 0)+sc.getMinPax());
            } else if (startTime1.equals(shift2Start) && endTime1.equals(shift3Start)) {
              mapOfMinPax.put(("2+"+sc.getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("2+"+sc.getFacility().getName()), 0)+sc.getMinPax());
            } else if (startTime1.equals(shift3Start) && endTime1.equals(shift3End)) {
              mapOfMinPax.put(("3+"+sc.getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("3+"+sc.getFacility().getName()), 0)+sc.getMinPax());
            } else {
              mapOfMinPax.put(("4+"+sc.getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("4+"+sc.getFacility().getName()), 0)+sc.getMinPax());
            }
          }
          for (Shift shift : listOfShifts) {
            LocalTime startTime1 = shift.getStartTime().toLocalTime();
            LocalTime endTime1 = shift.getEndTime().toLocalTime();
            if (startTime1.equals(shift1Start) && endTime1.equals(shift2Start)) {
              mapOfMinPax.put(("1+"+shift.getFacilityBooking().getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("1+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
            } else if (startTime1.equals(shift2Start) && endTime1.equals(shift3Start)) {
              mapOfMinPax.put(("2+"+shift.getFacilityBooking().getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("2+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
            } else if (startTime1.equals(shift3Start) && endTime1.equals(shift3End)) {
              mapOfMinPax.put(("3+"+shift.getFacilityBooking().getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("3+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
            } else {
              mapOfMinPax.put(("4+"+shift.getFacilityBooking().getFacility().getFacilityId()), mapOfMinPax.getOrDefault(("4+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
            }
          }
          List<String> list = new ArrayList<>();
          for (Map.Entry<String,Integer> entry : mapOfMinPax.entrySet()) {
            if (entry.getValue() > 0) {
              String num = entry.getKey().substring(0,1);
              String key = entry.getKey().substring(2);
              list.add(num + "," + key);
            }
          }
          long dayOff = (staff.getStaffId()*2) % 7;
          if (i != dayOff) {
            if (isWard) {
              if (numShifts == 1) {
                if (shift1 == 1) {
                  createShift(staff.getUsername(), null,
                          new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                  LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                } else if (shift2 == 1) {
                  createShift(staff.getUsername(), null,
                          new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                } else {
                  createShift(staff.getUsername(), null,
                          new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                  LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                }
              } else if (numShifts == 2) {
                if (shift1 == 1 && shift3 == 1) {
                  boolean randomOfTwo = new Random().nextBoolean();
                  if (randomOfTwo) {
                    createShift(staff.getUsername(), null,
                            new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                    LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                  } else {
                    List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
                    LocalDateTime startTime = LocalDateTime.of(year, month, day, 0, 0, 0);
                    boolean canCreate = true;
                    for (Shift prevShift : shifts) {
                      if (prevShift.getEndTime().compareTo(startTime) == -1 && prevShift.getEndTime().until(startTime, ChronoUnit.HOURS) < 8) {
                        canCreate = false;
                      }
                    }
                    if (canCreate) {
                      createShift(staff.getUsername(), null,
                              new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                      LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                    }
                  }
                } else {
                  // shift 1 and shift 2 or shift 2 and shift 3
                  boolean randomOfTwo = new Random().nextBoolean();
                  if (randomOfTwo) {
                    createShift(staff.getUsername(), null,
                            new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                    LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                  } else {
                    if (shift1 == 1) {
                      createShift(staff.getUsername(), null,
                              new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                      LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                    } else {
                      createShift(staff.getUsername(), null,
                              new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                      LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                    }
                  }
                }
              }
              else if (numShifts == 3) {
                int shiftToAdd = new Random().nextInt(3);
                if (shiftToAdd == 0) {
                  List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
                  LocalDateTime startTime = LocalDateTime.of(year, month, day, 0, 0, 0);
                  boolean canCreate = true;
                  for (Shift prevShift : shifts) {
                    if (prevShift.getEndTime().compareTo(startTime) == -1 && prevShift.getEndTime().until(startTime, ChronoUnit.HOURS) < 8) {
                      canCreate = false;
                    }
                  }
                  if (canCreate) {
                    createShift(staff.getUsername(), null,
                            new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                    LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                  }
                } else if (shiftToAdd == 1) {
                  createShift(staff.getUsername(), null,
                          new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                } else {
                  createShift(staff.getUsername(), null,
                          new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                  LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                }
              }
            } else {
              int min = Integer.MAX_VALUE;
              for (Map.Entry<Long,Integer> entry : capacityMap.entrySet()) {
                min = Math.min(min, entry.getValue());
              }
              for (Map.Entry<Long,Integer> entry : capacityMap.entrySet()) {
                if (min == entry.getValue()) {
                  id = entry.getKey();
                }
              }

              for (String sc : list) {
                String[] scArr = sc.split(",");
                int shiftNum = Integer.parseInt(scArr[0]);
                Long facId = Long.parseLong(scArr[1]);
                shiftToPick = shiftNum;
                id = facId;
              }

              if (numShifts == 1) {
                if (shift1 == 1) {
                  createShift(staff.getUsername(), id,
                          new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                  LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                } else if (shift2 == 1) {
                  createShift(staff.getUsername(), id,
                          new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                  LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                } else {
                  createShift(staff.getUsername(), id,
                          new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                  LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                }
              } else if (numShifts == 2) {
                if (shift1 == 1 && shift3 == 1) {
                  if (shiftToPick > 0 && shiftToPick != 2) {
                    if (shiftToPick == 3) {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                      LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                    } else {
                      List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
                      LocalDateTime startTime = LocalDateTime.of(year, month, day, 0, 0, 0);
                      boolean canCreate = true;
                      for (Shift prevShift : shifts) {
                        if (prevShift.getEndTime().compareTo(startTime) == -1 && prevShift.getEndTime().until(startTime, ChronoUnit.HOURS) < 8) {
                          canCreate = false;
                        }
                      }
                      if (canCreate) {
                        createShift(staff.getUsername(), id,
                                new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                      } else {
                        continue;
                      }
                    }
                  } else {
                    boolean randomOfTwo = new Random().nextBoolean();
                    if (randomOfTwo) {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                      LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                    } else {
                      List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
                      LocalDateTime startTime = LocalDateTime.of(year, month, day, 0, 0, 0);
                      boolean canCreate = true;
                      for (Shift prevShift : shifts) {
                        if (prevShift.getEndTime().compareTo(startTime) == -1 && prevShift.getEndTime().until(startTime, ChronoUnit.HOURS) < 8) {
                          canCreate = false;
                        }
                      }
                      if (canCreate) {
                        createShift(staff.getUsername(), id,
                                new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                      } else {
                        continue;
                      }
                    }
                  }
                } else {
                  // shift 1 and shift 2 or shift 2 and shift 3
                  if (shiftToPick > 0) {
                    if (shiftToPick == 2) {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                      LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                    } else if (shiftToPick == 1) {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                      LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                    } else {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                      LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                    }
                  } else {
                    boolean randomOfTwo = new Random().nextBoolean();
                    if (randomOfTwo) {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                      LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                    } else {
                      if (shift1 == 1) {
                        createShift(staff.getUsername(), id,
                                new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                        LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                      } else {
                        createShift(staff.getUsername(), id,
                                new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                        LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                      }
                    }
                  }
                }
              }
              else if (numShifts == 3) {
                int shiftToAdd = new Random().nextInt(3);
                if (shiftToPick > 0) {
                  if (shiftToPick == 1) {
                    List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
                    LocalDateTime startTime = LocalDateTime.of(year, month, day, 0, 0, 0);
                    boolean canCreate = true;
                    for (Shift prevShift : shifts) {
                      if (prevShift.getEndTime().compareTo(startTime) == -1 && prevShift.getEndTime().until(startTime, ChronoUnit.HOURS) < 8) {
                        canCreate = false;
                      }
                    }
                    if (canCreate) {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                      LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                    } else {
                      continue;
                    }
                  } else if (shiftToPick == 2) {
                    createShift(staff.getUsername(), id,
                            new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                    LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                  } else if (shiftToPick == 3) {
                    createShift(staff.getUsername(), id,
                            new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                    LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                  }
                } else {
                  if (shiftToAdd == 0) {
                    List<Shift> shifts = shiftRepository.findShiftsByStaff(staff);
                    LocalDateTime startTime = LocalDateTime.of(year, month, day, 0, 0, 0);
                    boolean canCreate = true;
                    for (Shift prevShift : shifts) {
                      if (prevShift.getEndTime().compareTo(startTime) == -1 && prevShift.getEndTime().until(startTime, ChronoUnit.HOURS) < 8) {
                        canCreate = false;
                      }
                    }
                    if (canCreate) {
                      createShift(staff.getUsername(), id,
                              new Shift(LocalDateTime.of(year, month, day, 0, 0, 0),
                                      LocalDateTime.of(year, month, day, 8, 0, 0), "Staff is working shift 1"));
                    } else {
                      continue;
                    }
                  } else if (shiftToAdd == 1) {
                    createShift(staff.getUsername(), id,
                            new Shift(LocalDateTime.of(year, month, day, 8, 0, 0),
                                    LocalDateTime.of(year, month, day, 16, 0, 0), "Staff is working shift 2"));
                  } else {
                    createShift(staff.getUsername(), id,
                            new Shift(LocalDateTime.of(year, month, day, 16, 0, 0),
                                    LocalDateTime.of(year, month, day, 23, 59, 0), "Staff is working shift 3"));
                  }
                }
              }
              capacityMap.put(id, capacityMap.getOrDefault(id,0)+1);
            }
          }
        }
        currentDate = currentDate.plusDays(1);
      }
    }
  }
}
