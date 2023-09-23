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

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final SubDepartmentService subDepartmentService;

    private final StaffRepository staffRepository;
    private final FacilityService facilityService;

    public DepartmentService(DepartmentRepository departmentRepository, SubDepartmentService subDepartmentService, StaffRepository staffRepository, FacilityService facilityService) {
        this.departmentRepository = departmentRepository;
        this.subDepartmentService = subDepartmentService;
        this.staffRepository = staffRepository;
        this.facilityService = facilityService;
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

    public Department createDepartment(Department newDepartment) throws UnableToCreateDepartmentException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateDepartmentException("Staff cannot create departments as he/she is not an admin.");
        }
        try {
            System.out.println(newDepartment);
            String name = newDepartment.getName();
            if (name == null) {
                throw new UnableToCreateDepartmentException("Name must be present.");
            }
            departmentRepository.save(newDepartment);
            return newDepartment;
        } catch (Exception ex) {
            throw new UnableToCreateDepartmentException(ex.getMessage());
        }
    }

    public String deleteDepartment(Long departmentId) throws DepartmentNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateDepartmentException("Staff cannot delete departments as he/she is not an admin.");
        }
        try {
            Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
            if (departmentOptional.isPresent()) {
                Department department = departmentOptional.get();
                for (Facility facility : department.getListOfFacilities()) {
                    facilityService.deleteFacility(facility.getFacilityId());
                }
                departmentRepository.delete(department);
                return "Department with departmentId " + departmentId + " has been deleted successfully.";
            } else {
                throw new DepartmentNotFoundException("Department with ID: " + departmentId + " is not found");
            }
        } catch (Exception ex) {
            throw new DepartmentNotFoundException(ex.getMessage());
        }
    }

    public Department updateDepartment(Long departmentId, Department updatedDepartment) throws DepartmentNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateDepartmentException("Staff cannot update sub departments as he/she is not an Admin.");
        }
        try {
            Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
            if (departmentOptional.isPresent()) {
                Department department = departmentOptional.get();
                if (updatedDepartment.getName() != null) department.setName(updatedDepartment.getName());
                departmentRepository.save(department);
                return department;
            } else {
                throw new DepartmentNotFoundException("Department with ID: " + departmentId + " is not found");
            }
        } catch (Exception ex) {
            throw new DepartmentNotFoundException(ex.getMessage());
        }
    }

    public List<Department> getAllDepartmentsByName(String name) throws DepartmentNotFoundException {
        try {
            List<Department> departmentList = departmentRepository.findByNameContainingIgnoreCase(name);
            return departmentList;
        } catch (Exception ex) {
            throw new SubDepartmentNotFoundException(ex.getMessage());
        }
    }

    //public Optional<Department> findByDepartmentName(String departmentName) { return departmentRepository.findByDepartmentName(departmentName); }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
