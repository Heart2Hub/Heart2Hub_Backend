package com.Heart2Hub.Heart2Hub_Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ItemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.StaffRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.exception.MedicationNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicationException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToDeleteServiceException;
import com.Heart2Hub.Heart2Hub_Backend.repository.DrugRestrictionRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

@SpringBootTest
public class MedicationServiceTests {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private DrugRestrictionRepository drugRestrictionRepository;

    @Mock
    private TransactionItemService transactionItemService;

    @Mock
    private ElectronicHealthRecordRepository electronicHealthRecordRepository;

    @InjectMocks
    private MedicationService medicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIsLoggedInPharmacist() {
        // Mock data
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
        User user = new User("username", "password", new ArrayList<>());
        when(authentication.getPrincipal()).thenReturn(user);

        Staff staff = new Staff();
        staff.setStaffRoleEnum(StaffRoleEnum.PHARMACIST);
        when(staffRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(staff));

        // Test
        boolean result = medicationService.isLoggedInPharmacist();
        assertTrue(result);
        verify(authentication).getPrincipal();
        verify(staffRepository).findByUsername(user.getUsername());
    }

    @Test
    void testGetAllergenEnums() {
        // Test
        List<String> result = medicationService.getAllergenEnums();
        assertNotNull(result);
        assertEquals(6, result.size());
    }

    @Test
    void testCreateMedication() {
        // Mock data
        Medication newMedication = new Medication();
        newMedication.setInventoryItemName("New Medication");
        newMedication.setInventoryItemDescription("Description");
        newMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        newMedication.setQuantityInStock(10);
        newMedication.setRestockPricePerQuantity(BigDecimal.valueOf(20.0));
        newMedication.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        newMedication.setDrugRestrictions(drugRestrictions);

        Medication existingMedication = new Medication();
        when(medicationRepository.findAll()).thenReturn(List.of(existingMedication));

        // Test
        try {
            Medication result = medicationService.createMedication(newMedication);
            assertNotNull(result);
            assertEquals("New Medication", result.getInventoryItemName());
            assertEquals("Description", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.MEDICINE, result.getItemTypeEnum());
            assertEquals(10, result.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(20.0), result.getRestockPricePerQuantity());
            assertEquals(BigDecimal.valueOf(25.0), result.getRetailPricePerQuantity());
            assertEquals(drugRestrictions, result.getDrugRestrictions());
            verify(medicationRepository).findAll();
            verify(drugRestrictionRepository, times(2)).findAll();
            verify(drugRestrictionRepository, times(2)).delete(any());
            verify(medicationRepository, times(2)).save(any());

        } catch (UnableToCreateMedicationException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testDeleteMedication() {
        // Mock data
        Long inventoryItemId = 1L;

        Medication mockMedication = new Medication();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockMedication));

        List<TransactionItem> mockTransactionItems = new ArrayList<>();
        when(transactionItemService.getAllItems()).thenReturn(mockTransactionItems);

        // Test
        try {
            String result = medicationService.deleteMedication(inventoryItemId);
            assertEquals("Consumable Equipment with inventoryItemId  1 has been deleted successfully.", result);
            verify(medicationRepository).findById(inventoryItemId);
            verify(transactionItemService).getAllItems();
            verify(drugRestrictionRepository).delete(any());
            verify(medicationRepository).delete(mockMedication);

        } catch (MedicationNotFoundException | UnableToDeleteServiceException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testUpdateMedication() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Medication");
        updatedMedication.setInventoryItemDescription("Updated Description");
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(10);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(20.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(25.0));
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        updatedMedication.setDrugRestrictions(drugRestrictions);

        Medication mockMedication = new Medication();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockMedication));

        // Test
        try {
            Medication result = medicationService.updateMedication(inventoryItemId, updatedMedication);
            assertNotNull(result);
            assertEquals("Updated Medication", result.getInventoryItemName());
            assertEquals("Updated Description", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.MEDICINE, result.getItemTypeEnum());
            assertEquals(10, result.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(20.0), result.getRestockPricePerQuantity());
            assertEquals(BigDecimal.valueOf(25.0), result.getRetailPricePerQuantity());
            assertEquals(drugRestrictions, result.getDrugRestrictions());
            verify(medicationRepository).findById(inventoryItemId);
            verify(drugRestrictionRepository).findAll();
            verify(drugRestrictionRepository).delete(any());
            verify(medicationRepository).save(mockMedication);

        } catch (MedicationNotFoundException | UnableToCreateMedicationException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testFindMedicationByInventoryItemId() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication mockMedication = new Medication();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockMedication));

        // Test
        Medication result = medicationService.findMedicationByInventoryItemId(inventoryItemId);
        assertNotNull(result);
        assertEquals(mockMedication, result);
        verify(medicationRepository).findById(inventoryItemId);
    }

    @Test
    void testGetAllMedicationsByAllergy() {
        // Mock data
        Long pId = 1L;
        ElectronicHealthRecord ehr = new ElectronicHealthRecord();
        ehr.setListOfMedicalHistoryRecords(new ArrayList<>());
        when(electronicHealthRecordRepository.findById(pId)).thenReturn(Optional.of(ehr));

        List<Medication> medicationList = new ArrayList<>();
        when(medicationRepository.findAll()).thenReturn(medicationList);

        // Test
        List<Medication> result = medicationService.getAllMedicationsByAllergy(pId);
        assertNotNull(result);
        assertEquals(medicationList, result);
        verify(electronicHealthRecordRepository).findById(pId);
        verify(medicationRepository).findAll();
    }
}
