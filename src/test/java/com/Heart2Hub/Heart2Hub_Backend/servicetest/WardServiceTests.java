package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.DepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.SubDepartmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateWardException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateDepartmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.WardClassRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.WardRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.WardService;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
class WardServiceTests {

    @InjectMocks
    private WardService wardService;
    @Mock
    private WardRepository wardRepository;
    @Mock
    private WardClassRepository wardClassRepository;
    @Mock
    private StaffRepository staffRepository;

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
        assertTrue(wardService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("staff1");
    }

    @Test
    void testIsLoggedInUserAdmin_NotAdmin() {
        // Mocking (Authentication)
        UsernamePasswordAuthenticationToken authentication = mock(UsernamePasswordAuthenticationToken.class);
        User user = new User("staff1", "password1", Collections.emptyList());
        when(authentication.getPrincipal())
                .thenReturn(user);
        Optional<Staff> toReturn = Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.DOCTOR, true));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(toReturn);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Test
        assertFalse(wardService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("staff1");
    }

    @Test
    void testCreateWard() throws UnableToCreateWardException {
        Ward newWard = new Ward();
        String wardClassName = "A";

        // Mocking the logged-in user as an admin
        Staff staff = new Staff();
        staff.setStaffRoleEnum(StaffRoleEnum.ADMIN);
        when(staffRepository.findByUsername(anyString())).thenReturn(Optional.of(staff));

        // Mocking the ward class repository to return a valid WardClass
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName("A");
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(List.of(wardClass));

        // Mocking the ward repository to save the new ward
        when(wardRepository.save(any())).thenReturn(newWard);

        // Performing the test
        Ward result = wardService.createWard(newWard, wardClassName);

        assertNotNull(result);
        // Add additional assertions based on your implementation
    }

    @Test
    void testCreateWard_StaffNotFound() {
        // Mock data
        Ward newWard = new Ward();
        String wardClassName = "A";

        // Mocking the logged-in user as a non-existent staff
        when(staffRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard, wardClassName));
    }


    @Test
    void testCreateWardNonAdmin() {
        Ward newWard = new Ward();
        String wardClassName = "A";

        // Mocking the logged-in user as a non-admin
        when(staffRepository.findByUsername(anyString())).thenReturn(Optional.of(new Staff()));

        // Performing the test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard, wardClassName));
    }

    @Test
    void testCreateWardWardClassNotFound() {
        Ward newWard = new Ward();
        String wardClassName = "A";

        // Mocking the logged-in user as an admin
        when(staffRepository.findByUsername(anyString())).thenReturn(Optional.of(new Staff()));

        // Mocking the ward class repository to return an empty list
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(new ArrayList<>());

        // Performing the test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard, wardClassName));
    }

    @Test
    void testGetAllWardsByName() throws DepartmentNotFoundException {
        String wardName = "Ward1";
        List<Ward> wardList = new ArrayList<>();
        wardList.add(new Ward());
        when(wardRepository.findByNameContainingIgnoreCase(wardName)).thenReturn(wardList);

        List<Ward> result = wardService.getAllWardsByName(wardName);

        assertEquals(wardList, result);
    }

    @Test
    void testGetAllWardsByName_NoWardsFound() {
        // Mock data
        String wardName = "NonExistentWard";
        when(wardRepository.findByNameContainingIgnoreCase(wardName)).thenReturn(new ArrayList<>());

        // Test
        assertThrows(DepartmentNotFoundException.class, () -> wardService.getAllWardsByName(wardName));
    }

    @Test
    void testGetAllWardsByName_EmptyName() {
        // Mock data
        String wardName = "";
        when(wardRepository.findByNameContainingIgnoreCase(wardName)).thenReturn(new ArrayList<>());

        // Test
        assertThrows(DepartmentNotFoundException.class, () -> wardService.getAllWardsByName(wardName));
    }

    @Test
    void testGetAllWardsByWardClass() {
        String wardClassName = "A";
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName(wardClassName);
        List<Ward> wardList = new ArrayList<>();
        wardList.add(new Ward());
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(List.of(wardClass));
        when(wardRepository.findByWardClass(wardClass)).thenReturn(wardList);

        List<Ward> result = wardService.getAllWardsByWardClass(wardClassName);

        assertEquals(wardList, result);
    }

    @Test
    void testGetAllWardsByWardClass_NoWardsFound() {
        // Mock data
        String wardClassName = "NonExistentWardClass";
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(new ArrayList<>());

        // Test
        List<Ward> result = wardService.getAllWardsByWardClass(wardClassName);

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllWardsByWardClass_EmptyClassName() {
        // Mock data
        String wardClassName = "";
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(new ArrayList<>());

        // Test
        List<Ward> result = wardService.getAllWardsByWardClass(wardClassName);

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


}
