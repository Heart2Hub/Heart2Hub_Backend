package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import com.Heart2Hub.Heart2Hub_Backend.entity.Department;
import com.Heart2Hub.Heart2Hub_Backend.entity.Facility;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToDeleteDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToUpdateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DepartmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.FacilityRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.DepartmentService;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import junit.runner.Version;
import org.springframework.test.context.junit4.SpringRunner;

//@SpringBootTest
@RunWith(SpringRunner.class)
class DepartmentServiceTests {

    @InjectMocks
    private DepartmentService departmentService;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private StaffService staffService;
    @Mock
    private FacilityService facilityService;
    @Mock
    private FacilityRepository facilityRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

    @Test
    void testIsLoggedInUserAdmin() {
        // Mocking (Authentication)
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Test
        assertTrue(departmentService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("staff1");
    }

    @Test
    void testIsLoggedInUserAdmin_NotAdmin() {
        // Mocking (Authentication)
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("doctorCardiology1", "password", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("doctorCardiology1", "password", "Ernest", "Chan", 97882145L, StaffRoleEnum.DOCTOR, true));
        when(staffRepository.findByUsername("doctorCardiology1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Test
        assertFalse(departmentService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("doctorCardiology1");
    }

    @Test
    void testCreateDepartment() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Department department = new Department();
        department.setName("Test Department");
        department.setListOfFacilities(Collections.singletonList(new Facility()));

        // Test
        Department result = departmentService.createDepartment(department);
//        assertDoesNotThrow(() -> departmentService.createDepartment(department));
        assertNotNull(result);
        assertEquals(department, result);

//
//        // Verify
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testCreateDepartment_NotAdmin() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("doctorCardiology1", "password", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("doctorCardiology1", "password", "Ernest", "Chan", 97882145L, StaffRoleEnum.DOCTOR, true));
        when(staffRepository.findByUsername("doctorCardiology1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Test
        Department department = new Department();
        department.setName("Test Department");
        department.setListOfFacilities(Collections.singletonList(new Facility()));

        assertThrows(UnableToCreateDepartmentException.class, () -> departmentService.createDepartment(department));

        // Verify
        verify(departmentRepository, never()).save(department);
    }

    @Test
    void testCreateDepartment_NullNameFailure() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Department department = new Department();
        department.setName(null);
        department.setListOfFacilities(Collections.singletonList(new Facility()));

        // Test
        assertThrows(UnableToCreateDepartmentException.class, () -> departmentService.createDepartment(department));

        // Verify
        verify(departmentRepository, never()).save(department);
    }

    @Test
    void testDeleteDepartment() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long departmentId = 1L;
        Department department = new Department();
        Optional<Department> departmentOptional = Optional.of(department);
        when(departmentRepository.findById(departmentId)).thenReturn(departmentOptional);

        Facility facility = new Facility();
        facility.setFacilityId(1L);
        department.getListOfFacilities().add(facility);
        System.out.println(department.getListOfFacilities().get(0));


        // Test
        String result = departmentService.deleteDepartment(departmentId);
        assertNotNull(result);
        assertEquals("Department with departmentId 1 has been deleted successfully.", result);


        // Verify
        verify(facilityService, times(1)).deleteFacility(anyLong());
        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    void testDeleteDepartment_NotAdmin() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("doctorCardiology1", "password", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("doctorCardiology1", "password", "Ernest", "Chan", 97882145L, StaffRoleEnum.DOCTOR, true));
        when(staffRepository.findByUsername("doctorCardiology1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long departmentId = 1L;
        Department department = new Department();
        Optional<Department> departmentOptional = Optional.of(department);
        when(departmentRepository.findById(departmentId)).thenReturn(departmentOptional);

        Facility facility = new Facility();
        facility.setFacilityId(1L);
        department.getListOfFacilities().add(facility);
        System.out.println(department.getListOfFacilities().get(0));

        assertThrows(UnableToDeleteDepartmentException.class, () -> departmentService.deleteDepartment(departmentId));

        // Verify
        verify(facilityService, never()).deleteFacility(anyLong());
        verify(departmentRepository, never()).delete(any(Department.class));
}

    @Test
    void testDeleteDepartment_IdNotFound() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long departmentId = 2L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Test
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.deleteDepartment(departmentId));

        // Verify
        verify(facilityService, never()).deleteFacility(anyLong());
        verify(departmentRepository, never()).delete(any(Department.class));
    }

    @Test
    void testUpdateDepartment() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long departmentId = 1L;
        Department existingDepartment = new Department();
        existingDepartment.setName("Cardiology");

        Department updatedDepartment = new Department();
        updatedDepartment.setName("Updated Cardiology");

        Optional<Department> departmentOptional = Optional.of(existingDepartment);
        when(departmentRepository.findById(departmentId)).thenReturn(departmentOptional);

        // Test
        Department result = departmentService.updateDepartment(departmentId, updatedDepartment);
        assertNotNull(result);
        assertEquals(updatedDepartment.getName(), result.getName());

        // Verify
        verify(departmentRepository, times(1)).save(existingDepartment);
    }

    @Test
    void testUpdateDepartment_NotAdmin() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("doctorCardiology1", "password", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("doctorCardiology1", "password", "Ernest", "Chan", 97882145L, StaffRoleEnum.DOCTOR, true));
        when(staffRepository.findByUsername("doctorCardiology1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long departmentId = 1L;
        Department existingDepartment = new Department();
        existingDepartment.setName("Cardiology");

        Department updatedDepartment = new Department();
        updatedDepartment.setName("Updated Cardiology");

        Optional<Department> departmentOptional = Optional.of(existingDepartment);
        when(departmentRepository.findById(departmentId)).thenReturn(departmentOptional);

        // Test
        assertThrows(UnableToUpdateDepartmentException.class, () -> departmentService.updateDepartment(departmentId, updatedDepartment));

        // Verify
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void testUpdateDepartment_IdNotFound() {
        // Mocking
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long departmentId = 2L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Test
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.updateDepartment(departmentId, new Department()));

        // Verify
        verify(departmentRepository, never()).save(any(Department.class));
    }


    @Test
    void testGetAllDepartmentsByName() {
        // Mocking
        Department department1 = new Department();
        department1.setName("C");

        String name = "C";
        when(departmentRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.singletonList(department1));

        // Test
       List<Department> result = departmentService.getAllDepartmentsByName(name);
        assertEquals(1, result.size());

        // Verify
        verify(departmentRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

//    @Test
//    void testGetAllDepartmentsByName_DepartmentNotFound() {
//        // Mocking
//        String name = "null";
//        when(departmentRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());
//
//        // Test
//        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getAllDepartmentsByName(name));
//
//        // Verify - No interaction with departmentRepository
//        verifyNoInteractions(departmentRepository);
//    }

    @Test
    void testGetDepartmentByName() {
        // Mocking
        Department cardiologyDepartment = new Department();
        cardiologyDepartment.setName("Cardiology");

        String name = "Cardiology";
        Optional<Department> departmentOptional = Optional.of(cardiologyDepartment);
        when(departmentRepository.findByName(name)).thenReturn(departmentOptional);

        // Test
//        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentByName(name));
        Department result = departmentService.getDepartmentByName(name);
        assertNotNull(result);
        assertEquals(cardiologyDepartment, result);

        // Verify
        verify(departmentRepository, times(1)).findByName(name);
    }

    @Test
    void testGetDepartmentByName_DepartmentNotFound() {
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
        Department department1 = new Department();

        when(departmentRepository.findAll()).thenReturn(Collections.singletonList(department1));


        // Test
        List<Department> result = departmentService.getAllDepartments();
//        assertNotNull(departmentService.getAllDepartments());
        assertEquals(1, result.size());

        // Verify
        verify(departmentRepository, times(1)).findAll();
    }

//    @Test
//    void testGetAllDepartments_DepartmentNotFoundException() {
//        // Mocking
//
//        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
//        // Test
//        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getAllDepartments());
//
//        // Verify
//        verify(departmentRepository, times(1)).findAll();
//    }

}