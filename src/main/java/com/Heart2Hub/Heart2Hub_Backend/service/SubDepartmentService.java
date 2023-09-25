package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.SubDepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateSubDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubDepartmentService {

    private final SubDepartmentRepository subDepartmentRepository;

    private final DepartmentRepository departmentRepository;

    private final FacilityService facilityService;
    private final StaffRepository staffRepository;

    public SubDepartmentService(SubDepartmentRepository subDepartmentRepository, DepartmentRepository departmentRepository, FacilityService facilityService, StaffRepository staffRepository) {
        this.subDepartmentRepository = subDepartmentRepository;
        this.departmentRepository = departmentRepository;
        this.facilityService = facilityService;
        this.staffRepository = staffRepository;
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

    public SubDepartment createSubDepartment(Long departmentId, SubDepartment newSubDepartment) throws UnableToCreateSubDepartmentException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateSubDepartmentException("Staff cannot create sub departments as he/she is not an admin.");
        }
        return newSubDepartment;
//        try {
//            String name = newSubDepartment.getName();
//            if (name == null) {
//                throw new UnableToCreateSubDepartmentException("Name must be present.");
//            }
//            Department assignedDepartment =  departmentRepository.findById(departmentId).get();
//            newSubDepartment.setDepartment(assignedDepartment);
//            assignedDepartment.getListOfSubDepartments().add(newSubDepartment);
//            subDepartmentRepository.save(newSubDepartment);
//            departmentRepository.save(assignedDepartment);
//            return newSubDepartment;
//        } catch (Exception ex) {
//            throw new UnableToCreateSubDepartmentException(ex.getMessage());
//        }
    }

    public String deleteSubDepartment(Long subDepartmentId) throws SubDepartmentNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateFacilityException("Staff cannot delete sub departments as he/she is not an admin.");
        }
//        try {
//            Optional<SubDepartment> subDepartmentOptional = subDepartmentRepository.findById(subDepartmentId);
//            if (subDepartmentOptional.isPresent()) {
//                SubDepartment subDepartment = subDepartmentOptional.get();
//                Department department = subDepartment.getDepartment();
//                department.getListOfSubDepartments().remove(subDepartment);
//                for (Facility facility : subDepartment.getListOfFacilities()) {
//                    facilityService.deleteFacility(facility.getFacilityId());
//                }
//                subDepartmentRepository.delete(subDepartment);
//                return "Sub Department with subDepartmentId " + subDepartmentId + " has been deleted successfully.";
//            } else {
//                throw new SubDepartmentNotFoundException("Sub department with ID: " + subDepartmentId + " is not found");
//            }
//        } catch (Exception ex) {
//            throw new SubDepartmentNotFoundException(ex.getMessage());
//        }
        return "";
    }

    public SubDepartment updateSubDepartment(Long subdepartmentId, SubDepartment updatedSubDepartment) throws SubDepartmentNotFoundException {
        if (!isLoggedInUserAdmin()) {
            throw new UnableToCreateSubDepartmentException("Staff cannot update sub departments as he/she is not an Admin.");
        }
//        try {
//            Optional<SubDepartment> subDepartmentOptional = subDepartmentRepository.findById(subdepartmentId);
//            if (subDepartmentOptional.isPresent()) {
//                SubDepartment subDepartment = subDepartmentOptional.get();
//                if (updatedSubDepartment.getName() != null) subDepartment.setName(updatedSubDepartment.getName());
//                if (updatedSubDepartment.getDescription()!= null) subDepartment.setDescription(updatedSubDepartment.getDescription());
//                subDepartmentRepository.save(subDepartment);
//                return subDepartment;
//            } else {
//                throw new SubDepartmentNotFoundException("Sub Department with ID: " + subdepartmentId + " is not found");
//            }
//        } catch (Exception ex) {
//            throw new SubDepartmentNotFoundException(ex.getMessage());
//        }
        return updatedSubDepartment;
    }

    public List<SubDepartment> getAllSubDepartmentsByName(String name) throws SubDepartmentNotFoundException {
        try {
            List<SubDepartment> subDepartmentsList = subDepartmentRepository.findByNameContainingIgnoreCase(name);
            return subDepartmentsList;
        } catch (Exception ex) {
            throw new SubDepartmentNotFoundException(ex.getMessage());
        }
    }

    public List<SubDepartment> getSubDepartmentsByDepartment(String departmentName)  {
//        try {
//            Department department = departmentRepository.findByNameContainingIgnoreCase(departmentName).get(0);
//            return department.getListOfSubDepartments();
//        } catch (Exception ex) {
//            throw new DepartmentNotFoundException(ex.getMessage());
//        }
        return new ArrayList<>();
    }

}
