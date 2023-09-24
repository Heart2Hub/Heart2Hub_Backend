package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Shift;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftConstraints;
import com.Heart2Hub.Heart2Hub_Backend.entity.Ward;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftConstraintsRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.WardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class ShiftConstraintsService {


  private final ShiftConstraintsRepository shiftConstraintsRepository;
  private final ShiftService shiftService;
  private final FacilityRepository facilityRepository;
  private final WardRepository wardRepository;

  public ShiftConstraintsService(ShiftConstraintsRepository shiftConstraintsRepository, ShiftService shiftService, FacilityRepository facilityRepository, WardRepository wardRepository) {
    this.shiftConstraintsRepository = shiftConstraintsRepository;
    this.shiftService = shiftService;
    this.facilityRepository = facilityRepository;
    this.wardRepository = wardRepository;
  }

  public ShiftConstraints createShiftConstraints(ShiftConstraints newShiftConstraints, String facilityName) throws UnableToCreateShiftConstraintsException {
    try {
      if (newShiftConstraints.getMinPax() < 1) {
        throw new UnableToCreateShiftConstraintsException("Min pax must be greater than or equal to 1");
      }
      ShiftConstraints shiftConstraints = new ShiftConstraints(newShiftConstraints.getStartTime(),
                                                                newShiftConstraints.getEndTime(),
                                                                newShiftConstraints.getMinPax(),
                                                                newShiftConstraints.getStaffRoleEnum());
      if (newShiftConstraints.getStaffRoleEnum() != StaffRoleEnum.NURSE) {
        List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndFacilityNameAndStaffRoleEnumEquals(newShiftConstraints.getEndTime(), newShiftConstraints.getStartTime(), facilityName, newShiftConstraints.getStaffRoleEnum());
        if (!listOfShiftConstraints.isEmpty()) {
          ShiftConstraints sc = listOfShiftConstraints.get(0);
          LocalTime shiftStart = LocalTime.of(0,0,0);
          LocalTime shiftEnd = LocalTime.of(23,59,0);
          if (!(sc.getStartTime().equals(shiftStart) && sc.getEndTime().equals(shiftEnd))) {
            throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + newShiftConstraints.getStaffRoleEnum() + " and facility " + facilityName);
          }
        }

        List<Facility> facilityList = facilityRepository.findByNameContainingIgnoreCase(facilityName);
        if (facilityList.size() == 0) {
          throw new UnableToCreateShiftConstraintsException("Facility cannot be empty!");
        }
        Facility facility = facilityRepository.findByNameContainingIgnoreCase(facilityName).get(0);
        shiftConstraints.setFacility(facility);
        shiftConstraintsRepository.save(shiftConstraints);
        return shiftConstraints;
      } else {
        List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndWardNameAndStaffRoleEnumEquals(newShiftConstraints.getEndTime(), newShiftConstraints.getStartTime(), facilityName, newShiftConstraints.getStaffRoleEnum());
        if (!listOfShiftConstraints.isEmpty()) {
          ShiftConstraints sc = listOfShiftConstraints.get(0);
          LocalTime shiftStart = LocalTime.of(0,0,0);
          LocalTime shiftEnd = LocalTime.of(23,59,0);
          if (!(sc.getStartTime().equals(shiftStart) && sc.getEndTime().equals(shiftEnd))) {
            throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + newShiftConstraints.getStaffRoleEnum() + " and facility " + facilityName);
          }
        }
        List<Ward> wardList = wardRepository.findByNameContainingIgnoreCase(facilityName);
        if (wardList.size() == 0) {
          throw new UnableToCreateShiftConstraintsException("Ward cannot be empty!");
        }
        Ward ward = wardList.get(0);
        shiftConstraints.setWard(ward);
        shiftConstraintsRepository.save(shiftConstraints);
        return shiftConstraints;
      }
    } catch (NullPointerException ex) {
      throw new UnableToCreateShiftConstraintsException("Min pax field is compulsory!");
    } catch (Exception ex) {
      throw new UnableToCreateShiftConstraintsException(ex.getMessage());
    }
  }

  public List<ShiftConstraints> getAllShiftConstraintsByRole(String role, String departmentName) throws StaffRoleNotFoundException {
    try {
      List<ShiftConstraints> scList = new ArrayList<>();
      if (role.equalsIgnoreCase("NURSE")) {
        List<Ward> wList = wardRepository.findByNameContainingIgnoreCase(departmentName);
        for (Ward ward : wList) {
          List<ShiftConstraints> temp = shiftConstraintsRepository.findByStaffRoleEnumAndWardName(StaffRoleEnum.valueOf(role.toUpperCase()), ward.getName());
          scList.addAll(temp);
        }
      } else {
        List<Facility> fList = facilityRepository.findByDepartmentNameContainingIgnoreCase(departmentName);
        for (Facility facility : fList) {
          List<ShiftConstraints> temp = shiftConstraintsRepository.findByStaffRoleEnumAndFacilityName(StaffRoleEnum.valueOf(role.toUpperCase()), facility.getName());
          scList.addAll(temp);
        }
      }
      return scList;
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }

  public void deleteShiftConstraints(Long shiftConstraintsId) throws ShiftConstraintsNotFoundException {
    try {
      Optional<ShiftConstraints> shiftConstraintsOptional = shiftConstraintsRepository.findById(shiftConstraintsId);
      if (shiftConstraintsOptional.isPresent()) {
        ShiftConstraints sc = shiftConstraintsOptional.get();
        sc.setFacility(null);
        sc.setWard(null);
        shiftConstraintsRepository.delete(sc);
      } else {
        throw new ShiftConstraintsNotFoundException("Shift constraints ID " + shiftConstraintsId + " not found!");
      }
    } catch (Exception ex) {
      throw new ShiftConstraintsNotFoundException(ex.getMessage());
    }
  }

  public ShiftConstraints updateShiftConstraints(Long shiftConstraintsId, ShiftConstraints updatedShiftConstraints, String facilityName) throws ShiftConstraintsNotFoundException, UnableToCreateShiftConstraintsException {
    try {
      if (updatedShiftConstraints.getMinPax() < 1) {
        throw new UnableToCreateShiftConstraintsException("Min pax must be greater than or equal to 1");
      }
      Optional<ShiftConstraints> shiftConstraintsOptional = shiftConstraintsRepository.findById(shiftConstraintsId);
      if (shiftConstraintsOptional.isPresent()) {
        ShiftConstraints sc = shiftConstraintsOptional.get();
        if (sc.getStaffRoleEnum() != StaffRoleEnum.NURSE) {
          List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndFacilityNameAndStaffRoleEnumEquals(updatedShiftConstraints.getEndTime(), updatedShiftConstraints.getStartTime(), facilityName, sc.getStaffRoleEnum());
          if (listOfShiftConstraints.isEmpty() || (listOfShiftConstraints.size() == 1 && listOfShiftConstraints.get(0).getShiftConstraintsId() == shiftConstraintsId)) {
            if (updatedShiftConstraints.getStartTime() != null) sc.setStartTime(updatedShiftConstraints.getStartTime());
            if (updatedShiftConstraints.getEndTime() != null) sc.setEndTime(updatedShiftConstraints.getEndTime());
            if (updatedShiftConstraints.getMinPax() != null) sc.setMinPax(updatedShiftConstraints.getMinPax());
            List<Facility> facilityList = facilityRepository.findByNameContainingIgnoreCase(facilityName);
            if (facilityList.size() == 0) {
              throw new UnableToCreateShiftConstraintsException("Facility cannot be empty!");
            }
            Facility facility = facilityRepository.findByNameContainingIgnoreCase(facilityName).get(0);
            sc.setFacility(facility);
            shiftConstraintsRepository.save(sc);
            return sc;
          } else {
            throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + sc.getStaffRoleEnum() + ".");
          }
        } else {
          List<ShiftConstraints> listOfShiftConstraints = shiftConstraintsRepository.findShiftConstraintsByStartTimeLessThanAndEndTimeGreaterThanAndWardNameAndStaffRoleEnumEquals(updatedShiftConstraints.getEndTime(), updatedShiftConstraints.getStartTime(), facilityName, sc.getStaffRoleEnum());
          if (listOfShiftConstraints.isEmpty() || (listOfShiftConstraints.size() == 1 && listOfShiftConstraints.get(0).getShiftConstraintsId() == shiftConstraintsId)) {
            if (updatedShiftConstraints.getStartTime() != null) sc.setStartTime(updatedShiftConstraints.getStartTime());
            if (updatedShiftConstraints.getEndTime() != null) sc.setEndTime(updatedShiftConstraints.getEndTime());
            if (updatedShiftConstraints.getMinPax() != null) sc.setMinPax(updatedShiftConstraints.getMinPax());
            List<Ward> wardList = wardRepository.findByNameContainingIgnoreCase(facilityName);
            if (wardList.size() == 0) {
              throw new UnableToCreateShiftConstraintsException("Ward cannot be empty!");
            }
            Ward ward = wardList.get(0);
            sc.setWard(ward);
            shiftConstraintsRepository.save(sc);
            return sc;
          } else {
            throw new UnableToCreateShiftConstraintsException("There is an overlapping shift constraint with this time for role " + sc.getStaffRoleEnum() + ".");
          }
        }
      } else {
        throw new ShiftConstraintsNotFoundException("Shift with ID: " + shiftConstraintsId + " is not found");
      }
    } catch (NullPointerException ex) {
      throw new UnableToCreateShiftConstraintsException("Min pax field is compulsory!");
    } catch (Exception ex) {
      throw new ShiftConstraintsNotFoundException(ex.getMessage());
    }
  }

  public boolean isValidWorkDay(String role, String date, String department) throws StaffRoleNotFoundException {
    try {
      List<Shift> listOfShifts = shiftService.viewDailyRoster(date, role, department);
      List<ShiftConstraints> listOfShiftConstraints = getAllShiftConstraintsByRole(role, department);
      HashMap<String, Integer> mapOfMinPax = new HashMap<>();
      LocalTime shift1Start = LocalTime.of(0,0,0);
      LocalTime shift2Start = LocalTime.of(8,0,0);
      LocalTime shift3Start = LocalTime.of(16,0,0);
      LocalTime shift3End = LocalTime.of(23,59,0);
      for (ShiftConstraints sc : listOfShiftConstraints) {
        LocalTime start = sc.getStartTime();
        LocalTime end = sc.getEndTime();
        if (start.equals(shift1Start) && end.equals(shift2Start)) {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("1+"+sc.getWard().getName()), mapOfMinPax.getOrDefault(("1+"+sc.getWard().getName()), 0)+sc.getMinPax());
          } else {
            mapOfMinPax.put(("1+"+sc.getFacility().getName()), mapOfMinPax.getOrDefault(("1+"+sc.getFacility().getName()), 0)+sc.getMinPax());
          }
        } else if (start.equals(shift2Start) && end.equals(shift3Start)) {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("2+"+sc.getWard().getName()), mapOfMinPax.getOrDefault(("2+"+sc.getWard().getName()), 0)+sc.getMinPax());
          } else {
            mapOfMinPax.put(("2+"+sc.getFacility().getName()), mapOfMinPax.getOrDefault(("2+"+sc.getFacility().getName()), 0)+sc.getMinPax());
          }
        } else if (start.equals(shift3Start) && end.equals(shift3End)) {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("3+"+sc.getWard().getName()), mapOfMinPax.getOrDefault(("3+"+sc.getWard().getName()), 0)+sc.getMinPax());
          } else {
            mapOfMinPax.put(("3+"+sc.getFacility().getName()), mapOfMinPax.getOrDefault(("3+"+sc.getFacility().getName()), 0)+sc.getMinPax());
          }
        } else {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("4+"+sc.getWard().getName()), mapOfMinPax.getOrDefault(("4+"+sc.getWard().getName()), 0)+sc.getMinPax());
          } else {
            mapOfMinPax.put(("4+"+sc.getFacility().getName()), mapOfMinPax.getOrDefault(("4+"+sc.getFacility().getName()), 0)+sc.getMinPax());
          }
        }
      }
      for (Shift shift : listOfShifts) {
        LocalTime start = shift.getStartTime().toLocalTime();
        LocalTime end = shift.getEndTime().toLocalTime();
        if (start.equals(shift1Start) && end.equals(shift2Start)) {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("1+"+shift.getStaff().getUnit().getName()), mapOfMinPax.getOrDefault(("1+"+shift.getStaff().getUnit().getName()), 0)-1);
          } else {
            mapOfMinPax.put(("1+"+shift.getFacilityBooking().getFacility().getName()), mapOfMinPax.getOrDefault(("1+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
          }
        } else if (start.equals(shift2Start) && end.equals(shift3Start)) {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("2+"+shift.getStaff().getUnit().getName()), mapOfMinPax.getOrDefault(("2+"+shift.getStaff().getUnit().getName()), 0)-1);
          } else {
            mapOfMinPax.put(("2+"+shift.getFacilityBooking().getFacility().getName()), mapOfMinPax.getOrDefault(("2+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
          }
        } else if (start.equals(shift3Start) && end.equals(shift3End)) {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("3+"+shift.getStaff().getUnit().getName()), mapOfMinPax.getOrDefault(("3+"+shift.getStaff().getUnit().getName()), 0)-1);
          } else {
            mapOfMinPax.put(("3+"+shift.getFacilityBooking().getFacility().getName()), mapOfMinPax.getOrDefault(("3+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
          }
        } else {
          if (role.equalsIgnoreCase("NURSE")) {
            mapOfMinPax.put(("4+"+shift.getStaff().getUnit().getName()), mapOfMinPax.getOrDefault(("4+"+shift.getStaff().getUnit().getName()), 0)-1);
          } else {
            mapOfMinPax.put(("4+"+shift.getFacilityBooking().getFacility().getName()), mapOfMinPax.getOrDefault(("4+"+shift.getFacilityBooking().getFacility().getName()), 0)-1);
          }
        }
      }
      for (Map.Entry<String,Integer> entry : mapOfMinPax.entrySet()) {
        if (entry.getValue() > 0) {
          System.out.println(entry.getKey() + " " + entry.getValue());
          return false;
        }
      }
      return true;
    } catch (Exception ex) {
      throw new StaffRoleNotFoundException(ex.getMessage());
    }
  }
}
