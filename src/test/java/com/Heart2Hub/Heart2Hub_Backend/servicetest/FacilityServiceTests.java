package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityStatusEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.FacilityTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.repository.*;
import com.Heart2Hub.Heart2Hub_Backend.service.AllocatedInventoryService;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityBookingService;
import com.Heart2Hub.Heart2Hub_Backend.service.FacilityService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;

import junit.runner.Version;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
class FacilityServiceTests {

    @InjectMocks
    private FacilityService facilityService;
    @Mock
    private FacilityRepository facilityRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private AllocatedInventoryService allocatedInventoryService;
    @Mock
    private FacilityBookingService facilityBookingService;
    @Mock
    private ShiftConstraintsRepository shiftConstraintsRepository;


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
        assertTrue(facilityService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("staff1");
    }

    @Test
    void testIsLoggedInUserAdmin_RoleFailure() {
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
        assertFalse(facilityService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("doctorCardiology1");
    }

    @Test
    void testCreateFacility() {
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
//        department.setListOfFacilities(Collections.singletonList(new Facility()));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(10);
        newFacility.setLocation("1A");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        Facility result = facilityService.createFacility(departmentId, newFacility);

        assertNotNull(result);
        assertEquals(newFacility, result);
        // Verify
        verify(departmentRepository, times(1)).save(department);
        verify(facilityRepository, times(1)).save(newFacility);
    }

    @Test
    void testCreateFacility_NotAdmin() {
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
//        department.setListOfFacilities(Collections.singletonList(new Facility()));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(10);
        newFacility.setLocation("1A");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, newFacility));

        // Verify
        verify(facilityRepository, never()).save(newFacility);
    }

    @Test
    void testCreateFacility_DepartmentNotFound() {
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
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(10);
        newFacility.setLocation("1A");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, newFacility));

        // Verify
//        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, never()).save(newFacility);
    }
    @Test
    void testCreateFacility_CapacityFailure() {
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
//        Department department = new Department();
//        department.setListOfFacilities(Collections.singletonList(new Facility()));
//        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(-1);
        newFacility.setLocation("1A");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, newFacility));

        // Verify
//        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, never()).save(newFacility);
    }
    @Test
    void testCreateFacility_LocationFailure() {
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
//        Department department = new Department();
//        department.setListOfFacilities(Collections.singletonList(new Facility()));
//        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(10);
        newFacility.setLocation("");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, newFacility));

        // Verify
//        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, never()).save(newFacility);
    }
    @Test
    void testCreateFacility_FacilityStatusEnumFailure() {
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

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(10);
        newFacility.setLocation("1A");
        newFacility.setFacilityStatusEnum(null);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, newFacility));

        // Verify
//        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, never()).save(newFacility);
    }
    @Test
    void testCreateFacility_FacilityTypeEnumFailure() {
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

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(10);
        newFacility.setLocation("1A");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(null);

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, newFacility));

        // Verify
//        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, never()).save(newFacility);
    }
    @Test
    void testCreateFacility_RoomTypeAndCapacityConflict() {
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

        // Test
        Facility newFacility = new Facility();
        newFacility.setName("Consultation Room");
        newFacility.setCapacity(10);
        newFacility.setLocation("1A");
        newFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        newFacility.setFacilityTypeEnum(FacilityTypeEnum.CONSULTATION_ROOM);

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.createFacility(departmentId, newFacility));

        // Verify
//        verify(departmentRepository, times(1)).findById(departmentId);
        verify(facilityRepository, never()).save(newFacility);
    }
    @Test
    void testDeleteFacility() {
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
//        ShiftConstraints shiftConstraints = new ShiftConstraints();

        Long facilityId = 1L;
        Facility facility = new Facility();
//        shiftConstraints.setFacility(facility);
        facility.setDepartment(department);
        facility.setListOfFacilityBookings(Collections.emptyList());
        facility.setListOfAllocatedInventories(Collections.emptyList());

        Optional<Facility> facilityOptional = Optional.of(facility);
        when(facilityRepository.findById(facilityId)).thenReturn(facilityOptional);
        when(shiftConstraintsRepository.findByFacility(facility)).thenReturn(Collections.emptyList());


        // Test
        String result = facilityService.deleteFacility(facilityId);
        assertNotNull(result);
        assertEquals("Facility with facilityId 1 has been deleted successfully.", result);

        // Verify
        verify(allocatedInventoryService, times(0)).deleteAllocatedInventory(anyLong()); // To be added if needed
        verify(facilityBookingService, times(0)).deleteFacilityBooking(anyLong()); // To be added if needed
        verify(facilityRepository, times(1)).delete(facility);
    }
    @Test
    void testDeleteFacility_NotAdmin() {
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

        Department department = new Department();
//        ShiftConstraints shiftConstraints = new ShiftConstraints();

        Long facilityId = 1L;
        Facility facility = new Facility();
//        shiftConstraints.setFacility(facility);
        facility.setDepartment(department);
        facility.setListOfFacilityBookings(Collections.emptyList());
        facility.setListOfAllocatedInventories(Collections.emptyList());

        Optional<Facility> facilityOptional = Optional.of(facility);
        when(facilityRepository.findById(facilityId)).thenReturn(facilityOptional);
        when(shiftConstraintsRepository.findByFacility(facility)).thenReturn(Collections.emptyList());


        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.deleteFacility(facilityId));

        // Verify
        verify(facilityRepository, never()).delete(any(Facility.class));
    }
    @Test
    void testDeleteFacility_IdNotFoundFailure() {
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

        Long facilityId = 2L;
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        // Test
        assertThrows(FacilityNotFoundException.class, () -> facilityService.deleteFacility(facilityId));

        // Verify
        verify(facilityRepository, never()).delete(any(Facility.class));
    }
    @Test
    void testDeleteFacility_WithBookings() {
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
//        ShiftConstraints shiftConstraints = new ShiftConstraints();

        Long facilityId = 1L;
        Facility facility = new Facility();
//        shiftConstraints.setFacility(facility);
        facility.setDepartment(department);
        FacilityBooking facilityBooking = new FacilityBooking();
        facility.getListOfFacilityBookings().add(facilityBooking);
        facility.setListOfAllocatedInventories(Collections.emptyList());

        Optional<Facility> facilityOptional = Optional.of(facility);
        when(facilityRepository.findById(facilityId)).thenReturn(facilityOptional);
        when(shiftConstraintsRepository.findByFacility(facility)).thenReturn(Collections.emptyList());

        // Test
        assertThrows(FacilityNotFoundException.class, () -> facilityService.deleteFacility(facilityId));

        // Verify
        verify(facilityRepository, never()).delete(any(Facility.class));
    }

    @Test
    void testUpdateFacility() {
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
        updatedFacility.setName("Updated Consultation Room");
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("Updated 1A");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        Facility result = facilityService.updateFacility(facilityId, updatedFacility);

        // Verify
        assertNotNull(result);
        assertEquals(updatedFacility.getName(), result.getName());
        assertEquals(updatedFacility.getCapacity(), result.getCapacity());
        assertEquals(updatedFacility.getLocation(), result.getLocation());
        assertEquals(updatedFacility.getFacilityStatusEnum(), result.getFacilityStatusEnum());
        assertEquals(updatedFacility.getFacilityTypeEnum(), result.getFacilityTypeEnum());
        verify(facilityRepository, times(1)).save(result);
    }
    @Test
    void testUpdateFacility_NotAdmin() {
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
        updatedFacility.setName("Updated Consultation Room");
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("Updated 1A");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        // Test
        assertThrows(UnableToCreateFacilityException.class, () -> facilityService.updateFacility(facilityId, updatedFacility));

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));
    }
    @Test
    void testUpdateFacility_IdNotFoundFailure() {
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

        Long facilityId = 2L;
        Facility existingFacility = new Facility();
        existingFacility.setName("Old Facility");
        existingFacility.setCapacity(10);
        existingFacility.setLocation("Location");
        existingFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        existingFacility.setFacilityTypeEnum(FacilityTypeEnum.BURN_UNIT);

        // Test
        Facility updatedFacility = new Facility();
        updatedFacility.setName("Updated Consultation Room");
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("Updated 1A");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);
        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());

        // Test
        assertThrows(FacilityNotFoundException.class, () -> facilityService.updateFacility(facilityId, new Facility()));

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));
    }
    @Test
    void testUpdateFacility_NullName() {
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
        updatedFacility.setName(null);
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("Updated 1A");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(updatedFacility));

        // Test
        assertThrows(Exception.class, () -> {
            facilityService.updateFacility(facilityId, updatedFacility);
        });

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));

    }
    @Test
    void testUpdateFacility_CapacityFailure() {
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
        updatedFacility.setName("Updated Facility");
        updatedFacility.setCapacity(-1);
        updatedFacility.setLocation("Updated 1A");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(updatedFacility));

        // Test
        assertThrows(Exception.class, () -> {
            facilityService.updateFacility(facilityId, updatedFacility);
        });

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));
    }
    @Test
    void testUpdateFacility_LocationFailure() {
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
        updatedFacility.setName("Updated Facility");
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(updatedFacility));

        // Test
        assertThrows(Exception.class, () -> {
            facilityService.updateFacility(facilityId, updatedFacility);
        });

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));
    }
    @Test
    void testUpdateFacility_FacilityStatusEnumFailure() {
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
        updatedFacility.setName("Updated Facility");
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("1A");
        updatedFacility.setFacilityStatusEnum(null);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CARDIOLOGY_UNIT);

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(updatedFacility));

        // Test
        assertThrows(Exception.class, () -> {
            facilityService.updateFacility(facilityId, updatedFacility);
        });

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));
    }
    @Test
    void testUpdateFacility_FacilityTypeEnumFailure() {
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
        updatedFacility.setName("Updated Facility");
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("1A");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        updatedFacility.setFacilityTypeEnum(null);

        when(facilityRepository.findById(facilityId)).thenReturn(Optional.of(updatedFacility));

        // Test
        assertThrows(Exception.class, () -> {
            facilityService.updateFacility(facilityId, updatedFacility);
        });

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));
    }
    @Test
    void testUpdateFacility_CannotMakeNonBookable() {
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

        Long facilityId = 1L;
        Facility existingFacility = new Facility();
        existingFacility.setName("Old Facility");
        existingFacility.setCapacity(10);
        existingFacility.setLocation("Location");
        existingFacility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        existingFacility.setFacilityTypeEnum(FacilityTypeEnum.BURN_UNIT);
        FacilityBooking facilityBooking = new FacilityBooking();
        existingFacility.getListOfFacilityBookings().add(facilityBooking);
        Optional<Facility> facilityOptional = Optional.of(existingFacility);
        when(facilityRepository.findById(facilityId)).thenReturn(facilityOptional);

        // Test
        Facility updatedFacility = new Facility();
        updatedFacility.setName("Updated Facility");
        updatedFacility.setCapacity(10);
        updatedFacility.setLocation("1A");
        updatedFacility.setFacilityStatusEnum(FacilityStatusEnum.NON_BOOKABLE);
        updatedFacility.setFacilityTypeEnum(FacilityTypeEnum.CONSULTATION_ROOM);

        // Test
        assertThrows(Exception.class, () -> {
            facilityService.updateFacility(facilityId, updatedFacility);
        });

        // Verify
        verify(facilityRepository, never()).save(any(Facility.class));
    }
    @Test
    void testGetAllFacilitiesByFacilityStatus() {
        // Mocking
        String facilityStatus = "BOOKABLE";
        FacilityStatusEnum facilityStatusEnum = FacilityStatusEnum.valueOf(facilityStatus.toUpperCase());
        Facility facility = new Facility();
        facility.setFacilityStatusEnum(FacilityStatusEnum.BOOKABLE);
        when(facilityRepository.findByFacilityStatusEnum(facilityStatusEnum)).thenReturn(Collections.singletonList(facility));

        // Test
        List<Facility> result = facilityService.getAllFacilitiesByFacilityStatus(facilityStatus);
        assertEquals(1, result.size());
        // Verify
        verify(facilityRepository, times(1)).findByFacilityStatusEnum(facilityStatusEnum);
    }

    @Test
    void testGetAllFacilitiesByName() {
        // Mocking
        String name = "Consultation";
        Facility facility = new Facility();
        facility.setName("Consultation");
        when(facilityRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.singletonList(facility));

        // Test
        List<Facility> result = facilityService.getAllFacilitiesByName(name);
        assertEquals(1, result.size());
        // Verify
        verify(facilityRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void testGetAllFacilitiesByDepartmentName() {
        // Mocking
        String departmentName = "Cardiology";
        Department department = new Department();
        department.setName("Cardiology");
        Facility facility = new Facility();
        facility.setDepartment(department);
        department.getListOfFacilities().add(facility);
        when(facilityRepository.findByDepartmentNameContainingIgnoreCase(departmentName)).thenReturn(Collections.singletonList(facility));

        // Test
        List<Facility> result = facilityService.getAllFacilitiesByDepartmentName(departmentName);
        assertEquals(1, result.size());
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

//    @Test
//    void testFindFacilityById_FacilityNotFound() {
//        // Mocking
//        Long facilityId = 1L;
//        when(facilityRepository.findById(facilityId)).thenReturn(Optional.empty());
//
//        // Test
//        assertThrows(FacilityNotFoundException.class, () -> facilityService.findFacilityById(facilityId));
//
//        // Verify
//        verify(facilityRepository, times(1)).findById(facilityId);
//    }


    @Test
    void testFindAllFacilities() {
        // Mocking
        Facility facility = new Facility();
        when(facilityRepository.findAll()).thenReturn(Collections.singletonList(facility));

        // Test
        List<Facility> result = facilityService.findAllFacilities();
        assertEquals(1, result.size());

        // Verify
        verify(facilityRepository, times(1)).findAll();
    }
    @Test
    void testGetFacilityIdRange() {
        // Mocking
        String unit = "Cardiology";
        Long[] idRange = new Long[2];
        idRange[0] = 1L;
        idRange[1] = 16L;

        // Test
        Long[] result = facilityService.getFacilityIdRange(unit);
        assertEquals(idRange[0], result[0]);
        assertEquals(idRange[1], result[1]);

    }

}
