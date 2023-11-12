package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.FacilityNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateFacilityException;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import com.Heart2Hub.Heart2Hub_Backend.service.AllocatedInventoryService;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityBookingService;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import com.Heart2Hub.Heart2Hub_Backend.util.TestAuthenticationUtil;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest
public class FacilityServiceTests {

    private AuthenticationManager authenticationManager;
    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StaffService staffService;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AllocatedInventoryService allocatedInventoryService;

    @Mock
    private ShiftConstraintsRepository shiftConstraintsRepository;

    @Mock
    private FacilityBookingService facilityBookingService;

    @InjectMocks
    private FacilityService facilityService;

    private TestAuthenticationUtil testAuthenticationUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testAuthenticationUtil = new TestAuthenticationUtil(authenticationManager, staffService);

        // Mock the SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Set up authentication for the test user
        Authentication authentication = testAuthenticationUtil.authenticateUser("staff1", "password1");

        // Set the mock authentication in the SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testIsLoggedInUserAdmin() {
        // Mocking
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal())
                .thenReturn(new User("staff1", "password1", Collections.emptyList()));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.ADMIN, true)));

        // Test
        assertTrue(facilityService.isLoggedInUserAdmin());

        // Verify
        verify(authenticationManager, times(1)).authenticate(any());
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("staff1");
    }

    @Test
    void testIsLoggedInUserAdmin_UserNotAdmin() {
        // Mocking
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal())
                .thenReturn(new User("staff1", "password1", Collections.emptyList()));
        when(staffRepository.findByUsername("staff1"))
                .thenReturn(Optional.of(new Staff("staff1", "password1", "Elgin", "Chan", 97882145L, StaffRoleEnum.DOCTOR, true)));

        // Test
        assertFalse(facilityService.isLoggedInUserAdmin());

        // Verify
        verify(authenticationManager, times(1)).authenticate(any());
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("staff1");
    }


    @Test
    void testCreateFacility() {
        // Mocking
        Long departmentId = 1L;
        Department department = new Department();
        department.setListOfFacilities(Collections.emptyList());
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(facilityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("New Facility");
        newFacility.setCapacity(10);
        newFacility.setLocation("Location");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        assertDoesNotThrow(() -> facilityService.createFacility(departmentId, newFacility));

        // Verify
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, times(1)).save(newFacility);
    }

    @Test
    void testCreateFacility_DepartmentNotFound() {
        // Mocking
        Long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, new Facility()));

        // Verify
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, times(0)).save(any());
    }


    @Test
    void testDeleteFacility() {
        // Mocking
        Long facilityId = 1L;
        Facility facility = new Facility();
        facility.setListOfFacilityBookings(Collections.emptyList());
        facility.setListOfAllocatedInventories(Collections.emptyList());
        Optional<Facility> facilityOptional = Optional.of(facility);
        when(facilityRepository.findById(facilityId)).thenReturn(facilityOptional);

        // Test
        assertDoesNotThrow(() -> facilityService.deleteFacility(facilityId));

        // Verify
        verify(allocatedInventoryService, times(0)).deleteAllocatedInventory(anyLong()); // To be added if needed
        verify(facilityBookingService, times(0)).deleteFacilityBooking(anyLong()); // To be added if needed
        verify(facilityRepository, times(1)).delete(facility);
    }

    @Test
    void testDeleteFacility_FacilityNotFound() {
        // Mocking
        Long facilityId = 1L;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        // Test
        assertThrows(FacilityNotFoundException.class, () -> facilityService.deleteFacility(facilityId));

        // Verify
        verify(facilityRepository, times(0)).delete(any());
    }


    @Test
    void testUpdateFacility() {
        // Mocking
        Long facilityId = 1L;
        Facility existingFacility = new Facility();
        existingFacility.setName("Old Facility");
        existingFacility.setCapacity(10);
        existingFacility.setLocation("Location");
        existingFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        existingFacility.setFacilityTypeEnum(FacilityTypeEnum.BURN_UNIT);
        Optional<Facility> facilityOptional = Optional.of(existingFacility);
        when(facilityRepository.findById(facilityId)).thenReturn(facilityOptional);

        // Test
        Facility updatedFacility = new Facility();
        updatedFacility.setName("New Facility");
        updatedFacility.setCapacity(20);
        updatedFacility.setLocation("New Location");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.NON_BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        Facility result = facilityService.updateFacility(facilityId, updatedFacility);

        // Verify
        assertEquals(updatedFacility.getName(), result.getName());
        assertEquals(updatedFacility.getCapacity(), result.getCapacity());
        assertEquals(updatedFacility.getLocation(), result.getLocation());
        assertEquals(updatedFacility.getFacilityStatusEnum(), result.getFacilityStatusEnum());
        assertEquals(updatedFacility.getFacilityTypeEnum(), result.getFacilityTypeEnum());
        verify(facilityRepository, times(1)).save(existingFacility);
    }

    @Test
    void testUpdateFacility_FacilityNotFound() {
        // Mocking
        Long facilityId = 1L;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        // Test
        assertThrows(FacilityNotFoundException.class, () -> facilityService.updateFacility(facilityId, new Facility()));

        // Verify
        verify(facilityRepository, times(0)).save(any());
    }


    @Test
    void testGetAllFacilitiesByFacilityStatus() {
        // Mocking
        String facilityStatus = "BOOKABLE";
        FacilityStatusEnum facilityStatusEnum = FacilityStatusEnum.valueOf(facilityStatus.toUpperCase());
        when(facilityRepository.findByFacilityStatusEnum(facilityStatusEnum)).thenReturn(Collections.emptyList());

        // Test
        assertDoesNotThrow(() -> facilityService.getAllFacilitiesByFacilityStatus(facilityStatus));

        // Verify
        verify(facilityRepository, times(1)).findByFacilityStatusEnum(facilityStatusEnum);
    }

    @Test
    void testGetAllFacilitiesByName() {
        // Mocking
        String name = "Test";
        when(facilityRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());

        // Test
        assertDoesNotThrow(() -> facilityService.getAllFacilitiesByName(name));

        // Verify
        verify(facilityRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void testGetAllFacilitiesByDepartmentName() {
        // Mocking
        String departmentName = "Test Department";
        when(facilityRepository.findByDepartmentNameContainingIgnoreCase(departmentName)).thenReturn(Collections.emptyList());

        // Test
        assertDoesNotThrow(() -> facilityService.getAllFacilitiesByDepartmentName(departmentName));

        // Verify
        verify(facilityRepository, times(1)).findByDepartmentNameContainingIgnoreCase(departmentName);
    }

    @Test
    void testFindFacilityById() {
        // Mocking
        Long facilityId = 1L;
        Facility facility = new Facility();
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        // Test
        Facility result = facilityService.findFacilityById(facilityId);

        // Verify
        assertEquals(facility, result);
        verify(facilityRepository, times(1)).findById(facilityId);
    }

    @Test
    void testFindFacilityById_FacilityNotFound() {
        // Mocking
        Long facilityId = 1L;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        // Test
        assertThrows(FacilityNotFoundException.class, () -> facilityService.findFacilityById(facilityId));

        // Verify
        verify(facilityRepository, times(1)).findById(facilityId);
    }


    @Test
    void testFindAllFacilities() {
        // Mocking
        when(facilityRepository.findAll()).thenReturn(Collections.emptyList());

        // Test
        assertNotNull(facilityService.findAllFacilities());

        // Verify
        verify(facilityRepository, times(1)).findAll();
    }

}
