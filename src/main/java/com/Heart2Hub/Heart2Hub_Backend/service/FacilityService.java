package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.FacilityNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;

    public FacilityService(FacilityRepository facilityRepository, StaffRepository staffRepository, DepartmentRepository departmentRepository) {
        this.facilityRepository = facilityRepository;
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
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

    public Facility createFacility(Long departmentId, Facility newFacility) throws UnableToCreateFacilityException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateFacilityException("Staff cannot create facilities as he/she is not an admin.");
        }
        try {
            String name = newFacility.getName();
            if (name.trim().equals("")) {
                throw new UnableToCreateFacilityException("Name must be present.");
            }
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
        } catch (Exception ex) {
            throw new UnableToCreateFacilityException(ex.getMessage());
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

}
