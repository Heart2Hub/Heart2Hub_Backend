package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.SubDepartment;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.SubDepartmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubDepartmentService {

    private final SubDepartmentRepository subDepartmentRepository;
    private final DepartmentRepository departmentRepository;

//    public Optional<SubDepartment> findBySubDepartmentName(String subDepartmentName) {
//        return subDepartmentRepository.findBySubDepartmentName(subDepartmentName);
//    }


    public SubDepartmentService(SubDepartmentRepository subDepartmentRepository, DepartmentRepository departmentRepository) {
        this.subDepartmentRepository = subDepartmentRepository;
        this.departmentRepository = departmentRepository;
    }

    public SubDepartment createSubDepartment(String subDepartmentName, String departmentName) {
        SubDepartment newSubDepartment = new SubDepartment(subDepartmentName);
        Department department = departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> new DepartmentNotFoundException("Department does not exist"));
        newSubDepartment.setDepartment(department);
        department.getListOfSubDepartments().add(newSubDepartment);
        subDepartmentRepository.save(newSubDepartment);
        departmentRepository.save(department);
        return newSubDepartment;
    }

    public List<SubDepartment> getAllSubDepartments() {
        return subDepartmentRepository.findAll();
    }

    public List<SubDepartment> getSubDepartmentsByDepartment(String departmentName) {
        Department department = departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> new DepartmentNotFoundException("Department does not exist"));
        return department.getListOfSubDepartments();

//        List<SubDepartment> subDepartmentsInSelectedDepartment = new ArrayList<>();
//        List<SubDepartment> allSubDepartments = getAllSubDepartments();
//        for (SubDepartment subDepartment : allSubDepartments) {
//            if (subDepartment.getDepartment().getDepartmentName().equals(departmentName)) {
//                subDepartmentsInSelectedDepartment.add(subDepartment);
//            }
//        }
//        return subDepartmentsInSelectedDepartment;
    }
}
