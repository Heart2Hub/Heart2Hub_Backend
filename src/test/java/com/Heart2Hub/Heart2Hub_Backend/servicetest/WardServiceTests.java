package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
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
        assertFalse(wardService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("doctorCardiology1");
    }

    @Test
    void testCreateWard() throws UnableToCreateWardException {
        // Mocking the logged-in user as an admin
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

        // Mocking the ward class repository to return a valid WardClass
        Ward newWard = new Ward();
        String wardClassName = "Ward 1A";
        newWard.setName(wardClassName);
        newWard.setLocation("Level 2");
        newWard.setCapacity(24);
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName("Ward 1A");
        wardClass.setBedsPerRoom(10);
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(Collections.singletonList(wardClass));

        // Performing the test
        Ward result = wardService.createWard(newWard, wardClassName);

        assertNotNull(result);
        assertEquals(newWard,result);
        //verify
        verify(wardRepository, times(1)).save(result);


    }

    @Test
    void testCreateWard_NotAdmin() {
        // Mock data
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

        // Mocking the ward class repository to return a valid WardClass
        Ward newWard = new Ward();
        String wardClassName = "Ward 1A";
        newWard.setName(wardClassName);
        newWard.setLocation("Level 2");
        newWard.setCapacity(24);
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName("Ward 1A");
        wardClass.setBedsPerRoom(10);
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(Collections.singletonList(wardClass));

        // Performing the test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard,wardClassName));

        //verify
        verify(wardRepository, never()).save(newWard);
    }

    @Test
    void testCreateWard_NullName() {
        // Mock data
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

        // Mocking the ward class repository to return a valid WardClass
        Ward newWard = new Ward();
        String wardClassName = "Ward 1A";
        newWard.setName(null);
        newWard.setLocation("Level 2");
        newWard.setCapacity(24);
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName("Ward 1A");
        wardClass.setBedsPerRoom(10);
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(Collections.singletonList(wardClass));

        // Performing the test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard,wardClassName));

        //verify
        verify(wardRepository, never()).save(newWard);
    }
    @Test
    void testCreateWard_CapacityFailure() {
        // Mock data
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

        // Mocking the ward class repository to return a valid WardClass
        Ward newWard = new Ward();
        String wardClassName = "Ward 1A";
        newWard.setName(wardClassName);
        newWard.setLocation("Level 2");
        newWard.setCapacity(null);
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName("Ward 1A");
        wardClass.setBedsPerRoom(10);
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(Collections.singletonList(wardClass));

        // Performing the test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard,wardClassName));

        //verify
        verify(wardRepository, never()).save(newWard);
    }
    @Test
    void testCreateWard_NullLocation() {
        // Mock data
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

        // Mocking the ward class repository to return a valid WardClass
        Ward newWard = new Ward();
        String wardClassName = "Ward 1A";
        newWard.setName(wardClassName);
        newWard.setLocation(null);
        newWard.setCapacity(24);
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName("Ward 1A");
        wardClass.setBedsPerRoom(10);
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(Collections.singletonList(wardClass));

        // Performing the test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard,wardClassName));

        //verify
        verify(wardRepository, never()).save(newWard);
    }
    @Test
    void testCreateWard_WardClassNotFound() {
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

        Ward newWard = new Ward();
        String wardClassName = "Ward 1A";
        newWard.setName(wardClassName);
        newWard.setLocation(null);
        newWard.setCapacity(24);

        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(Collections.emptyList());

        // Performing the test
        assertThrows(UnableToCreateWardException.class, () -> wardService.createWard(newWard, wardClassName));
        verify(wardRepository, never()).save(newWard);

    }

    @Test
    void testGetAllWardsByName() throws DepartmentNotFoundException {
        String wardName = "Ward1";
        List<Ward> wardList = new ArrayList<>();
        wardList.add(new Ward());
        when(wardRepository.findByNameContainingIgnoreCase(wardName)).thenReturn(wardList);

        List<Ward> result = wardService.getAllWardsByName(wardName);

        assertEquals(1, result.size());
        verify(wardRepository, times(1)).findByNameContainingIgnoreCase(wardName);

    }

    @Test
    void testGetAllWardsByWardClass() {
        String wardClassName = "B2";
        WardClass wardClass = new WardClass();
        wardClass.setWardClassName(wardClassName);
        List<Ward> wardList = new ArrayList<>();
        wardList.add(new Ward());
        when(wardClassRepository.findByWardClassNameContainingIgnoreCase(wardClassName))
                .thenReturn(List.of(wardClass));
        when(wardRepository.findByWardClass(wardClass)).thenReturn(wardList);

        List<Ward> result = wardService.getAllWardsByWardClass(wardClassName);

        assertEquals(1, result.size());

        verify(wardClassRepository,times(1)).findByWardClassNameContainingIgnoreCase(wardClassName);
        verify(wardRepository,times(1)).findByWardClass(wardClass);
    }


}
