package com.Heart2Hub.Heart2Hub_Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.entity.AllocatedInventory;
import com.Heart2Hub.Heart2Hub_Backend.entity.ConsumableEquipment;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.ConsumableEquipmentNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateConsumableEquipmentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.AllocatedInventoryRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ConsumableEquipmentRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.ConsumableEquipmentService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

@SpringBootTest
public class ConsumableEquipmentServiceTests {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private ConsumableEquipmentRepository consumableEquipmentRepository;

    @Mock
    private AllocatedInventoryRepository allocatedInventoryRepository;

    @InjectMocks
    private ConsumableEquipmentService consumableEquipmentService;

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
        boolean result = consumableEquipmentService.isLoggedInUserAdmin();
        assertTrue(result);
        verify(authentication).getPrincipal();
        verify(staffRepository).findByUsername(user.getUsername());
    }

    @Test
    void testCreateConsumableEquipment() {
        // Mock data
        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        // Test
        try {
            ConsumableEquipment result = consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
            assertNotNull(result);
            assertEquals("New Consumable Equipment", result.getInventoryItemName());
            assertEquals("Description", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.CONSUMABLE, result.getItemTypeEnum());
            assertEquals(10, result.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(25.0), result.getRestockPricePerQuantity());
            verify(consumableEquipmentRepository).save(result);
        } catch (UnableToCreateConsumableEquipmentException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testDeleteConsumableEquipment() {
        // Mock data
        Long inventoryItemId = 1L;

        ConsumableEquipment mockConsumableEquipment = new ConsumableEquipment();
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockConsumableEquipment));

        List<AllocatedInventory> mockAllocatedInventoryList = new ArrayList<>();
        when(allocatedInventoryRepository.findAllocatedInventoriesByConsumableEquipment(mockConsumableEquipment)).thenReturn(mockAllocatedInventoryList);

        // Test
        try {
            String result = consumableEquipmentService.deleteConsumableEquipment(inventoryItemId);
            assertNotNull(result);
            assertEquals("Consumable Equipment with consumableEquipmentId 1 has been deleted successfully.", result);
            verify(consumableEquipmentRepository).findById(inventoryItemId);
            verify(allocatedInventoryRepository).findAllocatedInventoriesByConsumableEquipment(mockConsumableEquipment);
            verify(allocatedInventoryRepository, times(mockAllocatedInventoryList.size())).delete(any(AllocatedInventory.class));
            verify(consumableEquipmentRepository).delete(mockConsumableEquipment);
        } catch (ConsumableEquipmentNotFoundException | UnableToCreateConsumableEquipmentException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testUpdateConsumableEquipment() {
        // Mock data
        Long inventoryItemId = 1L;
        ConsumableEquipment existingConsumableEquipment = new ConsumableEquipment();
        existingConsumableEquipment.setInventoryItemId(inventoryItemId);
        existingConsumableEquipment.setInventoryItemName("Existing Consumable Equipment");
        existingConsumableEquipment.setInventoryItemDescription("Description");
        existingConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        existingConsumableEquipment.setQuantityInStock(20);
        existingConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(30.0));

        ConsumableEquipment updatedConsumableEquipment = new ConsumableEquipment();
        updatedConsumableEquipment.setInventoryItemName("Updated Consumable Equipment");
        updatedConsumableEquipment.setInventoryItemDescription("Updated Description");
        updatedConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        updatedConsumableEquipment.setQuantityInStock(30);
        updatedConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(35.0));

        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingConsumableEquipment));

        // Test
        try {
            ConsumableEquipment result = consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
            assertNotNull(result);
            assertEquals(inventoryItemId, result.getInventoryItemId());
            assertEquals("Updated Consumable Equipment", result.getInventoryItemName());
            assertEquals("Updated Description", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.CONSUMABLE, result.getItemTypeEnum());
            assertEquals(30, result.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(35.0), result.getRestockPricePerQuantity());
            verify(consumableEquipmentRepository).findById(inventoryItemId);
            verify(consumableEquipmentRepository).save(result);
        } catch (ConsumableEquipmentNotFoundException | UnableToCreateConsumableEquipmentException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testGetAllConsumableEquipmentByName() {
        // Mock data
        List<ConsumableEquipment> consumableEquipmentList = new ArrayList<>();
        when(consumableEquipmentRepository.findAll()).thenReturn(consumableEquipmentList);

        // Test
        try {
            List<ConsumableEquipment> result = consumableEquipmentService.getAllConsumableEquipmentByName();
            assertNotNull(result);
            assertEquals(consumableEquipmentList, result);
            verify(consumableEquipmentRepository).findAll();
        } catch (ConsumableEquipmentNotFoundException e) {
            fail("Exception not expected");
        }
    }

}
