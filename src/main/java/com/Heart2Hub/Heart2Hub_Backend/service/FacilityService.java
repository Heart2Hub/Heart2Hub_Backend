package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.FacilityNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;
    private final AllocatedInventoryService allocatedInventoryService;
    private final ShiftConstraintsRepository shiftConstraintsRepository;
    private final FacilityBookingService facilityBookingService;

    public FacilityService(FacilityRepository facilityRepository, StaffRepository staffRepository, DepartmentRepository departmentRepository, AllocatedInventoryService allocatedInventoryService, ShiftConstraintsRepository shiftConstraintsRepository, FacilityBookingService facilityBookingService) {
        this.facilityRepository = facilityRepository;
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
        this.allocatedInventoryService = allocatedInventoryService;
        this.shiftConstraintsRepository = shiftConstraintsRepository;
        this.facilityBookingService = facilityBookingService;
    }

    public boolean isLoggedInUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            Optional<Staff> currStaff = staffRepository.findByUsername(user.getUsername());
            if (currStaff.isPresent()) {
                StaffRoleEnum role = currStaff.get().getStaffRoleEnum();
                if (role == StaffRoleEnum.ADMIN) {
                    isAdmin = true;
                }
            }
        }
        return isAdmin;
    }

    public Facility createFacility(Long departmentId, Facility newFacility) {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateFacilityException("Staff cannot create facilities as he/she is not an admin.");
        }
            String name = newFacility.getName();
            Integer capacity = newFacility.getCapacity();
            if (capacity < 1) {
                throw new UnableToCreateFacilityException("Capacity must be above 1");
            }
            String location = newFacility.getLocation();
            if (location.trim().equals("")) {
                throw new UnableToCreateFacilityException("Location must be present");
            }
            FacilityStatusEnum facilityStatusEnum = newFacility.getFacilityStatusEnum();
            if (facilityStatusEnum == null) {
                throw new UnableToCreateFacilityException("Facility status must be present");
            }
            FacilityTypeEnum facilityTypeEnum = newFacility.getFacilityTypeEnum();
            if (facilityTypeEnum == null) {
                throw new UnableToCreateFacilityException("Facility type must be present");
            }
            if (facilityTypeEnum.equals(FacilityTypeEnum.CONSULTATION_ROOM) && capacity != 1) {
                throw new UnableToCreateFacilityException("Capacity of Consultation Room must be 1");
            }
            Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);
            if (optionalDepartment.isPresent()) {
                Department assignedDepartment = optionalDepartment.get();
                assignedDepartment.getListOfFacilities().add(newFacility);
                newFacility.setDepartment(assignedDepartment);
                departmentRepository.save(assignedDepartment);
                facilityRepository.save(newFacility);
                return newFacility;
            } else {
                throw new UnableToCreateFacilityException("Dept not found");
            }
    }

    public String deleteFacility(Long facilityId) throws FacilityNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateFacilityException("Staff cannot delete facilities as he/she is not an admin.");
        }
        try {
            Optional<Facility> facilityOptional = facilityRepository.findById(facilityId);
            if (facilityOptional.isPresent()) {
                Facility facility = facilityOptional.get();
                Department department = facility.getDepartment();
                department.getListOfFacilities().remove(facility);

                List<AllocatedInventory> allocatedInventories = new ArrayList<>(facility.getListOfAllocatedInventories());

                System.out.println("Hello is empty??");
                for (int i = allocatedInventories.size() - 1; i >= 0; i--) {
                    AllocatedInventory allocatedInventory = allocatedInventories.get(i);
                    System.out.println(allocatedInventory.getAllocatedInventoryId());
                    allocatedInventories.remove(i);
                    allocatedInventoryService.deleteAllocatedInventory(allocatedInventory.getAllocatedInventoryId());
                }

                System.out.println(allocatedInventories.size());
                System.out.println("allocatedInventory.getAllocatedInventoryId()");
                facility.getListOfAllocatedInventories().clear();

                List<FacilityBooking> facilityBookings = new ArrayList<>(facility.getListOfFacilityBookings());

                System.out.println("help??");
                if (!facilityBookings.isEmpty()) {
                    throw new FacilityNotFoundException("Bookings for Facility found. Facility cannot be deleted");
                }

                System.out.println("facility Booking done()");
                facility.getListOfFacilityBookings().clear();

                List<ShiftConstraints> shiftConstraintsList = shiftConstraintsRepository.findByFacility(facility);
                System.out.println("Hello??");
                for (int i = shiftConstraintsList.size() - 1; i >= 0; i--) {
                    ShiftConstraints shiftConstraint = shiftConstraintsList.get(i);
                    System.out.println(shiftConstraint.getFacility().getFacilityId());
                    shiftConstraint.setFacility(null);
                    shiftConstraintsRepository.save(shiftConstraint);
                    shiftConstraintsList.remove(i);
                }
                System.out.println("shiftConstraint");


                // TO-DO: CHECK AND REMOVE FACILITY BOOKINGS
                // TO-DO: CHECK AND REMOVE ALLOCATED INVENTORY
                facilityRepository.delete(facility);
                return "Facility with facilityId " + facilityId + " has been deleted successfully.";
            } else {
                throw new FacilityNotFoundException("Facility with ID: " + facilityId + " is not found");
            }
        } catch (Exception ex) {
            throw new FacilityNotFoundException(ex.getMessage());
        }
    }

    public Facility updateFacility(Long facilityId, Facility updatedFacility) throws FacilityNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateFacilityException("Staff cannot update facilities as he/she is not an Admin. " + updatedFacility.getName());
        }
        try {
            Optional<Facility> facilityOptional = facilityRepository.findById(facilityId);
            if (facilityOptional.isPresent()) {
                Facility facility = facilityOptional.get();
                String name = facility.getName();
                if (name.trim().equals("")) {
                    throw new UnableToCreateFacilityException("Name must be present.");
                }
                Integer capacity = facility.getCapacity();
                if (capacity < 1) {
                    throw new UnableToCreateFacilityException("Capacity must be above 1");
                }
                String location = facility.getLocation();
                if (location.trim().equals("")) {
                    throw new UnableToCreateFacilityException("Location must be present");
                }
                FacilityStatusEnum facilityStatusEnum = facility.getFacilityStatusEnum();
                if (facilityStatusEnum == null) {
                    throw new UnableToCreateFacilityException("Facility status must be present");
                }
                if (facility.getFacilityStatusEnum().equals(FacilityStatusEnum.BOOKABLE) && updatedFacility.getFacilityStatusEnum().equals(FacilityStatusEnum.NON_BOOKABLE)) {
                    if (!facility.getListOfFacilityBookings().isEmpty()) {
                        throw new UnableToCreateFacilityException("Bookings found for facility. Facility status cannot be changed");
                    }
                }
                FacilityTypeEnum facilityTypeEnum = facility.getFacilityTypeEnum();
                if (facilityTypeEnum == null) {
                    throw new UnableToCreateFacilityException("Facility type must be present");
                }
                if (updatedFacility.getName() != null) facility.setName(updatedFacility.getName());
                if (updatedFacility.getLocation() != null) facility.setLocation(updatedFacility.getLocation());
                if (updatedFacility.getDescription() != null) facility.setDescription(updatedFacility.getDescription());
                if (updatedFacility.getCapacity() != null) facility.setCapacity(updatedFacility.getCapacity());
                if (updatedFacility.getFacilityStatusEnum() != null) facility.setFacilityStatusEnum(updatedFacility.getFacilityStatusEnum());
                if (updatedFacility.getFacilityTypeEnum() != null) facility.setFacilityTypeEnum(updatedFacility.getFacilityTypeEnum());
                facilityRepository.save(facility);
                return facility;
            } else {
                throw new FacilityNotFoundException("Facility with ID: " + facilityId + " is not found");
            }
        } catch (Exception ex) {
            throw new FacilityNotFoundException(ex.getMessage());
        }
    }

    public List<Facility> getAllFacilitiesByFacilityStatus(String facilityStatus) throws FacilityNotFoundException {
        try {
            FacilityStatusEnum facilityStatusEnum = FacilityStatusEnum.valueOf(facilityStatus.toUpperCase());
            List<Facility> facilitiesList = facilityRepository.findByFacilityStatusEnum(facilityStatusEnum);
            return facilitiesList;
        } catch (Exception ex) {
            throw new FacilityNotFoundException(ex.getMessage());
        }
    }

    public List<Facility> getAllFacilitiesByName(String name) throws FacilityNotFoundException {
        try {
            List<Facility> facilitiesList = facilityRepository.findByNameContainingIgnoreCase(name);
            return facilitiesList;
        } catch (Exception ex) {
            throw new FacilityNotFoundException(ex.getMessage());
        }
    }

    public List<Facility> getAllFacilitiesByDepartmentName(String departmentName) throws FacilityNotFoundException {
        try {
            List<Facility> facilitiesList = facilityRepository.findByDepartmentNameContainingIgnoreCase(departmentName);
            return facilitiesList;
        } catch (Exception ex) {
            throw new FacilityNotFoundException(ex.getMessage());
        }
    }

    public Facility findFacilityById(Long id){
        return facilityRepository.findById(id).get();
    }

    public List<Facility> findAllFacilities() {
        return facilityRepository.findAll();
    }

    public Long[] getFacilityIdRange(String unit) {
        Long[] ans = new Long[2];
        if (unit.equalsIgnoreCase("Cardiology")) {
            ans[0] = 1L;
            ans[1] = 16L;
        } else if (unit.equalsIgnoreCase("Orthopedics")) {
            ans[0] = 17L;
            ans[1] = 26L;
        } else if (unit.equalsIgnoreCase("Pediatrics")) {
            ans[0] = 27L;
            ans[1] = 36L;
        } else if (unit.equalsIgnoreCase("Neurology")) {
            ans[0] = 37L;
            ans[1] = 46L;
        } else if (unit.equalsIgnoreCase("Emergency Medicine")) {
            ans[0] = 47L;
            ans[1] = 58L;
        } else if (unit.equalsIgnoreCase("Surgery")) {
            ans[0] = 59L;
            ans[1] = 68L;
        } else if (unit.equalsIgnoreCase("Ophthalmology")) {
            ans[0] = 69L;
            ans[1] = 78L;
        } else if (unit.equalsIgnoreCase("Psychiatry")) {
            ans[0] = 79L;
            ans[1] = 93L;
        } else if (unit.equalsIgnoreCase("Radiology")) {
            ans[0] = 94L;
            ans[1] = 103L;
        } else if (unit.equalsIgnoreCase("Pharmacy")) {
            ans[0] = 104L;
            ans[1] = 108L;
        }

        return ans;
    }

}
