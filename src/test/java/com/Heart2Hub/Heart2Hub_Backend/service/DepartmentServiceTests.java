package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.util.TestAuthenticationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DepartmentServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private StaffService staffService;
    @Mock
    private FacilityService facilityService;
    @InjectMocks
    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    //@WithMockUser(username = "staff1", password = "password1", roles="ADMIN")
    void testIsLoggedInUserAdmin() {
        // Mocking
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal())
                .thenReturn(new User("staff1", "password1", Collections.emptyList()));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true)));
        //ockito.when(auth.getName()).thenReturn("aName");

        // Test
        assertTrue(departmentService.isLoggedInUserAdmin());

        // Verify
//        verify(authenticationManager, times(1)).authenticate(any());
//        verify(authentication, times(1)).getPrincipal();
//        verify(staffRepository, times(1)).findByUsername("staff1");
    }

    @Test
    void testCreateDepartment() {
        // Mocking
        when(departmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(departmentService.isLoggedInUserAdmin()).thenReturn(true);
        when(staffService.findByUsername(any())).thenReturn(Optional.of(new Staff()));

        // Test
        Department department = new Department();
        department.setName("Test Department");

        assertDoesNotThrow(() -> departmentService.createDepartment(department));

        // Verify
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testDeleteDepartment() {
        // Mocking
        Long departmentId = 1L;
        Department department = new Department();
        department.setListOfFacilities(Collections.singletonList(new Facility()));
        Optional<Department> departmentOptional = Optional.of(department);
        when(departmentRepository.findById(departmentId)).thenReturn(departmentOptional);

        // Test
        assertDoesNotThrow(() -> departmentService.deleteDepartment(departmentId));

        // Verify
        verify(facilityService, times(1)).deleteFacility(anyLong());
        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    void testUpdateDepartment() {
        // Mocking
        Long departmentId = 1L;
        Department existingDepartment = new Department();
        existingDepartment.setName("Old Department");

        Department updatedDepartment = new Department();
        updatedDepartment.setName("New Department");

        Optional<Department> departmentOptional = Optional.of(existingDepartment);
        when(departmentRepository.findById(departmentId)).thenReturn(departmentOptional);

        // Test
        Department result = departmentService.updateDepartment(departmentId, updatedDepartment);

        // Verify
        assertEquals(updatedDepartment.getName(), result.getName());
        verify(departmentRepository, times(1)).save(existingDepartment);
    }

    @Test
    void testGetAllDepartmentsByName() {
        // Mocking
        String name = "Test";
        when(departmentRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());

        // Test
        assertDoesNotThrow(() -> departmentService.getAllDepartmentsByName(name));

        // Verify
        verify(departmentRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void testGetDepartmentByName() {
        // Mocking
        String name = "Test";
        when(departmentRepository.findByName(name)).thenReturn(Optional.empty());

        // Test
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentByName(name));

        // Verify
        verify(departmentRepository, times(1)).findByName(name);
    }

    @Test
    void testGetAllDepartments() {
        // Mocking
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        // Test
        assertNotNull(departmentService.getAllDepartments());

        // Verify
        verify(departmentRepository, times(1)).findAll();
    }

}