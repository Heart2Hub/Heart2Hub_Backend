package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.exception.ServiceItemNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateServiceItemException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToDeleteServiceException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ServiceItemRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.UnitRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.ServiceItemService;
import com.Heart2Hub.Heart2Hub_Backend.service.TransactionItemService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class ServiceItemServiceTests {

    @InjectMocks
    private ServiceItemService serviceItemService;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private ServiceItemRepository serviceItemRepository;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private TransactionItemService transactionItemService;

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
        assertTrue(serviceItemService.isLoggedInUserAdmin());

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
        assertFalse(serviceItemService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("doctorCardiology1");
    }

    @Test
    void testCreateServiceItem() {
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

        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName("Service Item");
        newServiceItem.setInventoryItemDescription("Description");
        newServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        newServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        Unit unit = new Unit();
        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));

        // Test

            ServiceItem result = serviceItemService.createServiceItem(unitId, newServiceItem);
            assertNotNull(result);
            assertEquals(newServiceItem,result);

            verify(unitRepository).findById(unitId);
            verify(serviceItemRepository,times(1)).save(result);
    }
    @Test
    void testCreateServiceItem_NotAdmin() {
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

        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName(null);
        newServiceItem.setInventoryItemDescription("Description");
        newServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        newServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.createServiceItem(unitId, newServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(newServiceItem);
    }

    @Test
    void testCreateServiceItem_NullName() {
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

        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName(null);
        newServiceItem.setInventoryItemDescription("Description");
        newServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        newServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.createServiceItem(unitId, newServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(newServiceItem);
    }
    @Test
    void testCreateServiceItem_NullDescription() {
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

        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName("Service Item");
        newServiceItem.setInventoryItemDescription(null);
        newServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        newServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.createServiceItem(unitId, newServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(newServiceItem);
    }
    @Test
    void testCreateServiceItem_NullItemTypeEnum() {
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

        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName("Service Item");
        newServiceItem.setInventoryItemDescription("Description");
        newServiceItem.setItemTypeEnum(null);
        newServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.createServiceItem(unitId, newServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(newServiceItem);
    }
    @Test
    void testCreateServiceItem_NullRetailPricePerQuantity() {
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

        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName("Service Item");
        newServiceItem.setInventoryItemDescription("Description");
        newServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        newServiceItem.setRetailPricePerQuantity(null);

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.createServiceItem(unitId, newServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(newServiceItem);
    }

    @Test
    void testDeleteServiceItem() {
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

        Long inventoryItemId = 1L;

        ServiceItem mockServiceItem = new ServiceItem();
        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockServiceItem));

        List<TransactionItem> mockTransactionItems = new ArrayList<>();
        when(transactionItemService.getAllItems()).thenReturn(mockTransactionItems);

        // Test
            String result = serviceItemService.deleteServiceItem(inventoryItemId);
            assertNotNull(result);
            assertEquals("Service Item with inventoryItemId 1 has been deleted successfully.", result);
            verify(serviceItemRepository).findById(inventoryItemId);
            verify(transactionItemService).getAllItems();
            verify(serviceItemRepository,times(1)).delete(mockServiceItem);
    }
    @Test
    void testDeleteServiceItem_NotAdmin() {
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

        Long inventoryItemId = 1L;

// Test
        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.deleteServiceItem(inventoryItemId));

        // Verify
        verify(serviceItemRepository, never()).delete(any(ServiceItem.class)); }

    @Test
    void testDeleteServiceItem_IdNotFoundFailure() {
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

        Long inventoryItemId = 2L;

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ServiceItemNotFoundException.class, () -> serviceItemService.deleteServiceItem(inventoryItemId));

        // Verify
        verify(serviceItemRepository).findById(inventoryItemId);
        verify(serviceItemRepository, never()).delete(any(ServiceItem.class));
    }

    @Test
    void testDeleteServiceItem_ServiceInTransactionItem() {
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

        Long inventoryItemId = 1L;

        ServiceItem mockServiceItem = new ServiceItem();
        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockServiceItem));

//        InventoryItem mockInventoryItem = mockMedication;
        List<TransactionItem> mockTransactionItems = new ArrayList<>();
        TransactionItem mockTransactionItem = new TransactionItem(mockServiceItem.getInventoryItemName(),mockServiceItem.getInventoryItemDescription(),1,mockServiceItem.getRetailPricePerQuantity(),mockServiceItem);
        mockTransactionItems.add(mockTransactionItem);
        when(transactionItemService.getAllItems()).thenReturn(mockTransactionItems);

        // Test

//        String result = medicationService.deleteMedication(inventoryItemId);
        assertEquals(mockTransactionItem.getInventoryItem().getInventoryItemId(), mockServiceItem.getInventoryItemId());
        assertThrows(Exception.class, () -> {
            serviceItemService.deleteServiceItem(inventoryItemId);
        });

        // Verify
        verify(serviceItemRepository, never()).delete(any(ServiceItem.class));
    }

    @Test
    void testUpdateServiceItem() {
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

        Long inventoryItemId = 1L;
        ServiceItem existingServiceItem = new ServiceItem();
        existingServiceItem.setInventoryItemId(inventoryItemId);
        existingServiceItem.setInventoryItemName("Existing Service Item");
        existingServiceItem.setInventoryItemDescription("Description");
        existingServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        existingServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName("Update Service Item");
        updatedServiceItem.setInventoryItemDescription("Update Description");
        updatedServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        updatedServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(35.0));

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingServiceItem));

        // Test

            ServiceItem result = serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem);
            assertNotNull(result);
            assertEquals(inventoryItemId, result.getInventoryItemId());
            assertEquals("Update Service Item", result.getInventoryItemName());
            assertEquals("Update Description", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.OUTPATIENT, result.getItemTypeEnum());
            assertEquals(BigDecimal.valueOf(35.0), result.getRetailPricePerQuantity());

            verify(serviceItemRepository).findById(inventoryItemId);
            verify(serviceItemRepository,times(1)).save(result);

    }

    @Test
    void testUpdateServiceItem_NotAdmin() {
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

        Long inventoryItemId = 1L;
        ServiceItem existingServiceItem = new ServiceItem();
        existingServiceItem.setInventoryItemId(inventoryItemId);
        existingServiceItem.setInventoryItemName("Existing Service Item");
        existingServiceItem.setInventoryItemDescription("Description");
        existingServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        existingServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName("Update Service Item");
        updatedServiceItem.setInventoryItemDescription("Update Description");
        updatedServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        updatedServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(35.0));

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingServiceItem));

        // Test

        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(any(ServiceItem.class));

    }

    @Test
    void testUpdateServiceItem_ServiceItemNotFoundFailure() {
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

        Long inventoryItemId = 1L;
        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName("Updated Service Item");
        updatedServiceItem.setInventoryItemDescription("Updated Description");
        updatedServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        updatedServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(30.0));

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ServiceItemNotFoundException.class, () -> serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem));

        // Verify
        verify(serviceItemRepository).findById(inventoryItemId);
        verify(serviceItemRepository, never()).save(any(ServiceItem.class));
    }
    @Test
    void testUpdateServiceItem_NullName() {
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

        Long inventoryItemId = 1L;
        ServiceItem existingServiceItem = new ServiceItem();
        existingServiceItem.setInventoryItemId(inventoryItemId);
        existingServiceItem.setInventoryItemName("Existing Service Item");
        existingServiceItem.setInventoryItemDescription("Description");
        existingServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        existingServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName(null);
        updatedServiceItem.setInventoryItemDescription("Update Description");
        updatedServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        updatedServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(35.0));

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedServiceItem));

        // Test

        assertThrows(Exception.class, () -> serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(any(ServiceItem.class));

    }
    @Test
    void testUpdateServiceItem_NullDescription() {
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

        Long inventoryItemId = 1L;
        ServiceItem existingServiceItem = new ServiceItem();
        existingServiceItem.setInventoryItemId(inventoryItemId);
        existingServiceItem.setInventoryItemName("Existing Service Item");
        existingServiceItem.setInventoryItemDescription("Description");
        existingServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        existingServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName("Update Service Item");
        updatedServiceItem.setInventoryItemDescription(null);
        updatedServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        updatedServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(35.0));

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedServiceItem));

        // Test

        assertThrows(Exception.class, () -> serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(any(ServiceItem.class));

    }
    @Test
    void testUpdateServiceItem_NullItemTypeEnum() {
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

        Long inventoryItemId = 1L;
        ServiceItem existingServiceItem = new ServiceItem();
        existingServiceItem.setInventoryItemId(inventoryItemId);
        existingServiceItem.setInventoryItemName("Existing Service Item");
        existingServiceItem.setInventoryItemDescription("Description");
        existingServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        existingServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName("Update Service Item");
        updatedServiceItem.setInventoryItemDescription("Update Description");
        updatedServiceItem.setItemTypeEnum(null);
        updatedServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(35.0));

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedServiceItem));

        // Test

        assertThrows(Exception.class, () -> serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(any(ServiceItem.class));

    }

    @Test
    void testUpdateServiceItem_NullRetailPrice() {
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

        Long inventoryItemId = 1L;
        ServiceItem existingServiceItem = new ServiceItem();
        existingServiceItem.setInventoryItemId(inventoryItemId);
        existingServiceItem.setInventoryItemName("Existing Service Item");
        existingServiceItem.setInventoryItemDescription("Description");
        existingServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        existingServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName("Update Service Item");
        updatedServiceItem.setInventoryItemDescription("Update Description");
        updatedServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        updatedServiceItem.setRetailPricePerQuantity(null);

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedServiceItem));

        // Test

        assertThrows(Exception.class, () -> serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem));

        // Verify
        verify(serviceItemRepository, never()).save(any(ServiceItem.class));

    }

    @Test
    void testGetAllServiceItem() {
        // Mock data
        ServiceItem serviceItem = new ServiceItem();
        when(serviceItemRepository.findAll()).thenReturn(Collections.singletonList(serviceItem));

        // Test
            List<ServiceItem> result = serviceItemService.getAllServiceItem();
            assertEquals(1, result.size());
            verify(serviceItemRepository,times(1)).findAll();
    }


    @Test
    void testGetAllServiceItemInUnit() {
        // Mock data
        Long unitId = 1L;
        List<ServiceItem> serviceItemList = new ArrayList<>();
        ServiceItem serviceItem = new ServiceItem();
        Unit unit = new Unit();
        unit.setUnitId(unitId);
        serviceItem.setUnit(unit);
        serviceItemList.add(serviceItem);

        when(serviceItemRepository.findAll()).thenReturn(serviceItemList);

        // Test
        List<ServiceItem> result = serviceItemService.getAllServiceItemInUnit(unitId);
        assertEquals(1, result.size());
        verify(serviceItemRepository,times(1)).findAll();
    }

//    @Test
//    void testGetAllServiceItemInUnit_NoItemsFound() {
//        // Mock data
//        Long unitId = 1L;
//        when(serviceItemRepository.findAll()).thenReturn(new ArrayList<>());
//
//        // Test
//        List<ServiceItem> result = serviceItemService.getAllServiceItemInUnit(unitId);
//
//        // Verify
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(serviceItemRepository).findAll();
//    }

}
