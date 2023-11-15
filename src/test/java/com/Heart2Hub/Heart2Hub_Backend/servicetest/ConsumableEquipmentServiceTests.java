package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class ConsumableEquipmentServiceTests {

    @InjectMocks
    private ConsumableEquipmentService consumableEquipmentService;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private ConsumableEquipmentRepository consumableEquipmentRepository;
    @Mock
    private AllocatedInventoryRepository allocatedInventoryRepository;

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
        assertTrue(consumableEquipmentService.isLoggedInUserAdmin());

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
        assertFalse(consumableEquipmentService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("staff1");
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
    void testCreateConsumableEquipment_Failure() {
        // Mock data
        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        // Mocking the repository to return null, simulating a failure to create
        when(consumableEquipmentRepository.save(newConsumableEquipment)).thenReturn(null);

        // Test
        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> {
            consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
        });

        // Verify that the repository save method was called
        verify(consumableEquipmentRepository).save(newConsumableEquipment);
        // Verify that no other interactions occurred with the repository
        verifyNoMoreInteractions(consumableEquipmentRepository);
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
    void testDeleteConsumableEquipment_ConsumableEquipmentNotFound() {
        // Mock data
        Long inventoryItemId = 1L;

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ConsumableEquipmentNotFoundException.class, () -> {
            consumableEquipmentService.deleteConsumableEquipment(inventoryItemId);
        });

        // Verify
        verify(consumableEquipmentRepository).findById(inventoryItemId);
        verifyNoMoreInteractions(consumableEquipmentRepository, allocatedInventoryRepository);
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
    void testUpdateConsumableEquipment_ConsumableEquipmentNotFound() {
        // Mock data
        Long inventoryItemId = 1L;
        ConsumableEquipment updatedConsumableEquipment = new ConsumableEquipment();

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ConsumableEquipmentNotFoundException.class, () -> {
            consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        });

        // Verify
        verify(consumableEquipmentRepository).findById(inventoryItemId);
        verifyNoMoreInteractions(consumableEquipmentRepository);
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

    @Test
    void testGetAllConsumableEquipmentByName_ConsumableEquipmentNotFound() {
        // Mocking no consumable equipment found
        when(consumableEquipmentRepository.findAll()).thenReturn(Collections.emptyList());

        // Test
        assertThrows(ConsumableEquipmentNotFoundException.class, () -> {
            consumableEquipmentService.getAllConsumableEquipmentByName();
        });

        // Verify
        verify(consumableEquipmentRepository).findAll();
        verifyNoMoreInteractions(consumableEquipmentRepository);
    }


}
