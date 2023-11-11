package com.Heart2Hub.Heart2Hub_Backend.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WardServiceTests {

    @Mock
    private WardRepository wardRepository;

    @Mock
    private WardClassRepository wardClassRepository;

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private WardService wardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsLoggedInUserAdmin() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mocking the principal to be a user with admin role
        User user = new User("admin", "password", List.of(() -> "ROLE_ADMIN"));
        when(authentication.getPrincipal()).thenReturn(user);

        assertTrue(wardService.isLoggedInUserAdmin());
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

}
