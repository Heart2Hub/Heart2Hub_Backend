package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.stripe.model.tax.Registration;
import junit.runner.Version;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
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
        assertFalse(consumableEquipmentService.isLoggedInUserAdmin());

        // Verify
        verify(authentication, times(1)).getPrincipal();
        verify(staffRepository, times(1)).findByUsername("doctorCardiology1");
    }

    @Test
    void testCreateConsumableEquipment() {
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

        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        // Test
//        try {
            ConsumableEquipment result = consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
            assertNotNull(result);
            assertEquals(newConsumableEquipment, result);
//            assertEquals("New Consumable Equipment", result.getInventoryItemName());
//            assertEquals("Description", result.getInventoryItemDescription());
//            assertEquals(ItemTypeEnum.CONSUMABLE, result.getItemTypeEnum());
//            assertEquals(10, result.getQuantityInStock());
//            assertEquals(BigDecimal.valueOf(25.0), result.getRestockPricePerQuantity());
            verify(consumableEquipmentRepository, times(1)).save(result);
//        } catch (UnableToCreateConsumableEquipmentException e) {
//            fail("Exception not expected");
//        }
    }
    @Test
    void testCreateConsumableEquipment_NotAdmin() {
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
        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> consumableEquipmentService.createConsumableEquipment(newConsumableEquipment));

        // Verify
        verify(consumableEquipmentRepository, never()).save(newConsumableEquipment);
    }

    @Test
    void testCreateConsumableEquipment_NullNameFailure() {
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

        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName(null);
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        // Test
        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> {
            consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
        });

        verify(consumableEquipmentRepository, never()).save(newConsumableEquipment);
    }

    @Test
    void testCreateConsumableEquipment_NullDescriptionFailure() {
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

        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription(null);
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        // Test
        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> {
            consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
        });

        verify(consumableEquipmentRepository, never()).save(newConsumableEquipment);
    }

    @Test
    void testCreateConsumableEquipment_NullItemTypeEnumFailure() {
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

        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(null);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        // Test
        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> {
            consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
        });

        verify(consumableEquipmentRepository, never()).save(newConsumableEquipment);
    }

    @Test
    void testCreateConsumableEquipment_NullQuantityInStockFailure() {
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

        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(null);
        newConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(25.0));

        // Test
        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> {
            consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
        });

        verify(consumableEquipmentRepository, never()).save(newConsumableEquipment);
    }

    @Test
    void testCreateConsumableEquipment_NullRestockPricePerQuantityFailure() {
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

        ConsumableEquipment newConsumableEquipment = new ConsumableEquipment();
        newConsumableEquipment.setInventoryItemName("New Consumable Equipment");
        newConsumableEquipment.setInventoryItemDescription("Description");
        newConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        newConsumableEquipment.setQuantityInStock(10);
        newConsumableEquipment.setRestockPricePerQuantity(null);

        // Test
        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> {
            consumableEquipmentService.createConsumableEquipment(newConsumableEquipment);
        });

        verify(consumableEquipmentRepository, never()).save(newConsumableEquipment);
    }
    @Test
    void testDeleteConsumableEquipment() {
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

        ConsumableEquipment mockConsumableEquipment = new ConsumableEquipment();
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockConsumableEquipment));

        List<AllocatedInventory> mockAllocatedInventoryList = new ArrayList<>();
        when(allocatedInventoryRepository.findAllocatedInventoriesByConsumableEquipment(mockConsumableEquipment)).thenReturn(mockAllocatedInventoryList);

        // Test
            String result = consumableEquipmentService.deleteConsumableEquipment(inventoryItemId);
            assertNotNull(result);
            assertEquals("Consumable Equipment with consumableEquipmentId 1 has been deleted successfully.", result);

            verify(allocatedInventoryRepository,times(mockAllocatedInventoryList.size())).delete(any(AllocatedInventory.class));
            verify(consumableEquipmentRepository,times(1)).delete(mockConsumableEquipment);
    }

    @Test
    void testDeleteConsumableEquipment_NotAdmin() {
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
        Long inventoryItemId = 1L;
        ConsumableEquipment mockConsumableEquipment = new ConsumableEquipment();
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockConsumableEquipment));

        List<AllocatedInventory> mockAllocatedInventoryList = new ArrayList<>();
        when(allocatedInventoryRepository.findAllocatedInventoriesByConsumableEquipment(mockConsumableEquipment)).thenReturn(mockAllocatedInventoryList);

        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> consumableEquipmentService.deleteConsumableEquipment(inventoryItemId));

        // Verify
        verify(allocatedInventoryRepository, never()).delete(any(AllocatedInventory.class));
        verify(consumableEquipmentRepository, never()).delete(any(ConsumableEquipment.class));
    }
    @Test
    void testDeleteConsumableEquipment_IdNotFoundFailure() {
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

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ConsumableEquipmentNotFoundException.class, () -> {
            consumableEquipmentService.deleteConsumableEquipment(inventoryItemId);
        });

        // Verify
        verify(allocatedInventoryRepository, never()).delete(any(AllocatedInventory.class));
        verify(consumableEquipmentRepository, never()).delete(any(ConsumableEquipment.class));
    }



    @Test
    void testUpdateConsumableEquipment() {
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
        ConsumableEquipment result = consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        assertNotNull(result);
        assertEquals("Updated Consumable Equipment", result.getInventoryItemName());
        assertEquals("Updated Description", result.getInventoryItemDescription());
        assertEquals(updatedConsumableEquipment.getQuantityInStock(),result.getQuantityInStock());
        assertEquals(updatedConsumableEquipment.getRestockPricePerQuantity(),result.getRestockPricePerQuantity());
//        assertEquals(updatedConsumableEquipment,result);

        verify(consumableEquipmentRepository).findById(inventoryItemId);
        verify(consumableEquipmentRepository,times(1)).save(result);
    }

    @Test
    void testUpdateConsumableEquipment_NotAdmin() {
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
        assertThrows(UnableToCreateConsumableEquipmentException.class, () -> consumableEquipmentService.updateConsumableEquipment(inventoryItemId,updatedConsumableEquipment));

        // Verify
        verify(consumableEquipmentRepository, never()).save(any(ConsumableEquipment.class));
    }

    @Test
    void testUpdateConsumableEquipment_ConsumableEquipmentNotFound() {
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
        ConsumableEquipment updatedConsumableEquipment = new ConsumableEquipment();
        updatedConsumableEquipment.setInventoryItemName("Updated Consumable Equipment");
        updatedConsumableEquipment.setInventoryItemDescription("Updated Description");
        updatedConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        updatedConsumableEquipment.setQuantityInStock(30);
        updatedConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(35.0));

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ConsumableEquipmentNotFoundException.class, () -> {
            consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        });

        // Verify
        verify(consumableEquipmentRepository, never()).save(any(ConsumableEquipment.class));
    }

    @Test
    void testUpdateConsumableEquipment_NullNameFailure() {
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
        ConsumableEquipment existingConsumableEquipment = new ConsumableEquipment();
        existingConsumableEquipment.setInventoryItemId(inventoryItemId);
        existingConsumableEquipment.setInventoryItemName("Existing Consumable Equipment");
        existingConsumableEquipment.setInventoryItemDescription("Description");
        existingConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        existingConsumableEquipment.setQuantityInStock(20);
        existingConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(30.0));

        ConsumableEquipment updatedConsumableEquipment = new ConsumableEquipment();
        updatedConsumableEquipment.setInventoryItemName(null);
        updatedConsumableEquipment.setInventoryItemDescription("Updated Description");
        updatedConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        updatedConsumableEquipment.setQuantityInStock(30);
        updatedConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(35.0));

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedConsumableEquipment));

        // Test
        assertThrows(Exception.class, () -> {
            consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        });

        // Verify
        verify(consumableEquipmentRepository, never()).save(any(ConsumableEquipment.class));

    }
    @Test
    void testUpdateConsumableEquipment_NullDescriptionFailure() {
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
        ConsumableEquipment existingConsumableEquipment = new ConsumableEquipment();
        existingConsumableEquipment.setInventoryItemId(inventoryItemId);
        existingConsumableEquipment.setInventoryItemName("Existing Consumable Equipment");
        existingConsumableEquipment.setInventoryItemDescription("Description");
        existingConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        existingConsumableEquipment.setQuantityInStock(20);
        existingConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(30.0));

        ConsumableEquipment updatedConsumableEquipment = new ConsumableEquipment();
        updatedConsumableEquipment.setInventoryItemName("Updated Consumable Equipment");
        updatedConsumableEquipment.setInventoryItemDescription(null);
        updatedConsumableEquipment.setItemTypeEnum(ItemTypeEnum.CONSUMABLE);
        updatedConsumableEquipment.setQuantityInStock(30);
        updatedConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(35.0));

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedConsumableEquipment));

        // Test
        assertThrows(Exception.class, () -> {
            consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        });

        // Verify
        verify(consumableEquipmentRepository, never()).save(any(ConsumableEquipment.class));
    }

    @Test
    void testUpdateConsumableEquipment_NullItemTypeEnumFailure() {
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
        updatedConsumableEquipment.setItemTypeEnum(null);
        updatedConsumableEquipment.setQuantityInStock(30);
        updatedConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(35.0));

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedConsumableEquipment));

        // Test
        assertThrows(Exception.class, () -> {
            consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        });

        // Verify
        verify(consumableEquipmentRepository, never()).save(any(ConsumableEquipment.class));
    }

    @Test
    void testUpdateConsumableEquipment_NullQuantityInStockFailure() {
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
        updatedConsumableEquipment.setQuantityInStock(null);
        updatedConsumableEquipment.setRestockPricePerQuantity(BigDecimal.valueOf(35.0));

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedConsumableEquipment));

        // Test
        assertThrows(Exception.class, () -> {
            consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        });

        // Verify
        verify(consumableEquipmentRepository, never()).save(any(ConsumableEquipment.class));
    }

    @Test
    void testUpdateConsumableEquipment_NullRestockPricePerQuantityFailure() {
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
        updatedConsumableEquipment.setRestockPricePerQuantity(null);

        // Mocking consumable equipment not found
        when(consumableEquipmentRepository.findById(inventoryItemId)).thenReturn(Optional.of(updatedConsumableEquipment));

        // Test
        assertThrows(Exception.class, () -> {
            consumableEquipmentService.updateConsumableEquipment(inventoryItemId, updatedConsumableEquipment);
        });

        // Verify
        verify(consumableEquipmentRepository, never()).save(any(ConsumableEquipment.class));
    }
    @Test
    void testGetAllConsumableEquipmentByName() {
        // Mock data
        ConsumableEquipment consumableEquipment = new ConsumableEquipment();
        when(consumableEquipmentRepository.findAll()).thenReturn(Collections.singletonList(consumableEquipment));

        // Test

            List<ConsumableEquipment> result = consumableEquipmentService.getAllConsumableEquipmentByName();
            assertEquals(1, result.size());
        verify(consumableEquipmentRepository, times(1)).findAll();
    }

//    @Test
//    void testGetAllConsumableEquipmentByName_ConsumableEquipmentNotFound() {
//        // Mocking no consumable equipment found
//        when(consumableEquipmentRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Test
//        assertThrows(ConsumableEquipmentNotFoundException.class, () -> {
//            consumableEquipmentService.getAllConsumableEquipmentByName();
//        });
//
//        // Verify
//        verify(consumableEquipmentRepository).findAll();
//        verifyNoMoreInteractions(consumableEquipmentRepository);
//    }


}
