package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.ShiftPreference;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.exception.ShiftPreferenceNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateShiftPreferenceException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ShiftPreferenceRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {


  private final DepartmentRepository departmentRepository;
  private final SubDepartmentRepository subDepartmentRepository;

  public DepartmentService(DepartmentRepository departmentRepository, SubDepartmentRepository subDepartmentRepository) {
    this.departmentRepository = departmentRepository;
    this.subDepartmentRepository = subDepartmentRepository;
  }

  public Department createDepartment(String name) {
    Department department = new Department(name);
    return departmentRepository.save(department);
  }

  public Long createSubDepartment(String name, String desc, Long departmentId) {
    Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);
    if (optionalDepartment.isPresent()) {
      Department d = optionalDepartment.get();
      SubDepartment subDepartment = new SubDepartment(name, desc);
      subDepartment.setDepartment(d);
      List<SubDepartment> list = d.getListOfSubDepartments();
      list.add(subDepartment);
      return subDepartmentRepository.save(subDepartment).getSubDepartmentId();
    } else {
      return -1L;
    }
  }

}
