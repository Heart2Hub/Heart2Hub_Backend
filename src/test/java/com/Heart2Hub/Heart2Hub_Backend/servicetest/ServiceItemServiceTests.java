package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.entity.ServiceItem;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.entity.TransactionItem;
import com.Heart2Hub.Heart2Hub_Backend.entity.Unit;
import com.Heart2Hub.Heart2Hub_Backend.exception.ServiceItemNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateServiceItemException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToDeleteServiceException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ServiceItemRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.UnitRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.ServiceItemService;
import com.Heart2Hub.Heart2Hub_Backend.service.TransactionItemService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

public class ServiceItemServiceTests {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private ServiceItemRepository serviceItemRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private TransactionItemService transactionItemService;

    @InjectMocks
    private ServiceItemService serviceItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsLoggedInUserAdmin() {
        // Mock data
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        User user = new User("username", "password", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(user);

        Staff staff = new Staff();
        staff.setStaffRoleEnum(StaffRoleEnum.ADMIN);
        when(staffRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(staff));

        // Test
        boolean result = serviceItemService.isLoggedInUserAdmin();
        assertTrue(result);
        verify(authentication).getPrincipal();
        verify(staffRepository).findByUsername(user.getUsername());
    }

    @Test
    void testIsLoggedInUserAdmin_UserNotFound() {
        // Mock data
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        User user = new User("nonexistentuser", "password", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(user);

        // Test
        assertThrows(ServiceItemNotFoundException.class, () -> serviceItemService.isLoggedInUserAdmin());

        // Verify
        verify(authentication).getPrincipal();
        verify(staffRepository).findByUsername(user.getUsername());
        verifyNoMoreInteractions(staffRepository);
    }


    @Test
    void testCreateServiceItem() {
        // Mock data
        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName("New Service Item");
        newServiceItem.setInventoryItemDescription("Description");
        newServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        newServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        Unit unit = new Unit();
        when(unitRepository.findById(unitId)).thenReturn(Optional.of(unit));

        // Test
        try {
            ServiceItem result = serviceItemService.createServiceItem(unitId, newServiceItem);
            assertNotNull(result);
            assertEquals("New Service Item", result.getInventoryItemName());
            assertEquals("Description", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.OUTPATIENT, result.getItemTypeEnum());
            assertEquals(BigDecimal.valueOf(25.0), result.getRetailPricePerQuantity());
            assertEquals(unit, result.getUnit());
            verify(unitRepository).findById(unitId);
            verify(serviceItemRepository).save(result);
        } catch (UnableToCreateServiceItemException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testCreateServiceItem_UnitNotFound() {
        // Mock data
        Long unitId = 1L;
        ServiceItem newServiceItem = new ServiceItem();
        newServiceItem.setInventoryItemName("New Service Item");
        newServiceItem.setInventoryItemDescription("Description");
        newServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        newServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        when(unitRepository.findById(unitId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateServiceItemException.class, () -> serviceItemService.createServiceItem(unitId, newServiceItem));

        // Verify
        verify(unitRepository).findById(unitId);
        verifyNoMoreInteractions(serviceItemRepository);
    }


    @Test
    void testDeleteServiceItem() {
        // Mock data
        Long inventoryItemId = 1L;

        ServiceItem mockServiceItem = new ServiceItem();
        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockServiceItem));

        List<TransactionItem> mockTransactionItems = new ArrayList<>();
        when(transactionItemService.getAllItems()).thenReturn(mockTransactionItems);

        // Test
        try {
            String result = serviceItemService.deleteServiceItem(inventoryItemId);
            assertNotNull(result);
            assertEquals("Service Item with inventoryItemId  1 has been deleted successfully.", result);
            verify(serviceItemRepository).findById(inventoryItemId);
            verify(transactionItemService).getAllItems();
            verify(serviceItemRepository).delete(mockServiceItem);
        } catch (ServiceItemNotFoundException | UnableToDeleteServiceException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testDeleteServiceItem_ServiceItemNotFound() {
        // Mock data
        Long inventoryItemId = 1L;

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ServiceItemNotFoundException.class, () -> serviceItemService.deleteServiceItem(inventoryItemId));

        // Verify
        verify(serviceItemRepository).findById(inventoryItemId);
        verifyNoMoreInteractions(transactionItemService, serviceItemRepository);
    }


    @Test
    void testUpdateServiceItem() {
        // Mock data
        Long inventoryItemId = 1L;
        ServiceItem existingServiceItem = new ServiceItem();
        existingServiceItem.setInventoryItemId(inventoryItemId);
        existingServiceItem.setInventoryItemName("Existing Service Item");
        existingServiceItem.setInventoryItemDescription("Description");
        existingServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        existingServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));

        ServiceItem updatedServiceItem = new ServiceItem();
        updatedServiceItem.setInventoryItemName("Updated Service Item");
        updatedServiceItem.setInventoryItemDescription("Updated Description");
        updatedServiceItem.setItemTypeEnum(ItemTypeEnum.OUTPATIENT);
        updatedServiceItem.setRetailPricePerQuantity(BigDecimal.valueOf(30.0));

        when(serviceItemRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingServiceItem));

        // Test
        try {
            ServiceItem result = serviceItemService.updateServiceItem(inventoryItemId, updatedServiceItem);
            assertNotNull(result);
            assertEquals(inventoryItemId, result.getInventoryItemId());
            assertEquals("Updated Service Item", result.getInventoryItemName());
            assertEquals("Updated Description", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.OUTPATIENT, result.getItemTypeEnum());
            assertEquals(BigDecimal.valueOf(30.0), result.getRetailPricePerQuantity());
            verify(serviceItemRepository).findById(inventoryItemId);
            verify(serviceItemRepository).save(result);
        } catch (ServiceItemNotFoundException | UnableToCreateServiceItemException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testUpdateServiceItem_ServiceItemNotFound() {
        // Mock data
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
        verifyNoMoreInteractions(serviceItemRepository);
    }


    @Test
    void testGetAllServiceItem() {
        // Mock data
        List<ServiceItem> serviceItemList = new ArrayList<>();
        when(serviceItemRepository.findAll()).thenReturn(serviceItemList);

        // Test
        try {
            List<ServiceItem> result = serviceItemService.getAllServiceItem();
            assertNotNull(result);
            assertEquals(serviceItemList, result);
            verify(serviceItemRepository).findAll();
        } catch (ServiceItemNotFoundException e) {
            fail("Exception not expected");
        }
    }


    @Test
    void testGetAllServiceItemInUnit() {
        // Mock data
        Long unitId = 1L;
        List<ServiceItem> serviceItemList = new ArrayList<>();
        serviceItemList.add(new ServiceItem());
        when(serviceItemRepository.findAll()).thenReturn(serviceItemList);

        // Test
        List<ServiceItem> result = serviceItemService.getAllServiceItemInUnit(unitId);
        assertNotNull(result);
        assertEquals(serviceItemList, result);
        verify(serviceItemRepository).findAll();
    }

    @Test
    void testGetAllServiceItemInUnit_NoItemsFound() {
        // Mock data
        Long unitId = 1L;
        when(serviceItemRepository.findAll()).thenReturn(new ArrayList<>());

        // Test
        List<ServiceItem> result = serviceItemService.getAllServiceItemInUnit(unitId);

        // Verify
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(serviceItemRepository).findAll();
    }

}
