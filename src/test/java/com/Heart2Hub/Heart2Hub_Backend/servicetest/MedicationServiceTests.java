package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.*;
import com.Heart2Hub.Heart2Hub_Backend.exception.*;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicationService;
import com.Heart2Hub.Heart2Hub_Backend.service.TransactionItemService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.Heart2Hub.Heart2Hub_Backend.entity.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.repository.DrugRestrictionRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicationRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.StaffRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class MedicationServiceTests {

    @InjectMocks
    private MedicationService medicationService;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

    @Test
    void testGetAllergenEnums() {
        // Test
        List<String> result = medicationService.getAllergenEnums();
        assertNotNull(result);
        assertEquals(20, result.size());
    }

    @Test
    void testCreateMedication() {
        // Mock data
        Medication newMedication = new Medication();
        newMedication.setInventoryItemName("Cetirizine 10mg Tablets (12 pieces)");
        newMedication.setInventoryItemDescription("10mg per piece");
        newMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        newMedication.setQuantityInStock(100);
        newMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        newMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        newMedication.setComments("Do not take with alcohol");
        newMedication.setAllergenEnumList(new ArrayList<>());
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        newMedication.setDrugRestrictions(drugRestrictions);

//        Medication existingMedication = new Medication();
        when(medicationRepository.findAll()).thenReturn(List.of(newMedication));
//
//        // Test
//        try {
            Medication result = medicationService.createMedication(newMedication);
            assertNotNull(result);
            assertEquals(newMedication,result);
//            assertEquals("New Medication", result.getInventoryItemName());
//            assertEquals("Description", result.getInventoryItemDescription());
//            assertEquals(ItemTypeEnum.MEDICINE, result.getItemTypeEnum());
//            assertEquals(10, result.getQuantityInStock());
//            assertEquals(BigDecimal.valueOf(20.0), result.getRestockPricePerQuantity());
//            assertEquals(BigDecimal.valueOf(25.0), result.getRetailPricePerQuantity());
//            assertEquals(drugRestrictions, result.getDrugRestrictions());


//            verify(medicationRepository).findAll();
//            verify(drugRestrictionRepository, times(2)).findAll();
//            verify(drugRestrictionRepository, times(2)).delete(any());
            verify(medicationRepository, times(1)).save(result);

//        } catch (UnableToCreateMedicationException e) {
//            fail("Exception not expected");
//        }
    }

    @Test
    void testCreateMedication_NullDescriptionFailure() {
        // Mock data
        Medication newMedication = new Medication();
        newMedication.setInventoryItemName("Cetirizine 10mg Tablets (12 pieces)");
        newMedication.setInventoryItemDescription(null);
        newMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        newMedication.setQuantityInStock(100);
        newMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        newMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        newMedication.setAllergenEnumList(new ArrayList<>());
        newMedication.setComments("Do not take with alcohol");
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        newMedication.setDrugRestrictions(drugRestrictions);

//        Medication existingMedication = new Medication();
        when(medicationRepository.findAll()).thenReturn(List.of(newMedication));
//
//        // Test

        assertThrows(UnableToCreateMedicationException.class, () -> {
            medicationService.createMedication(newMedication);
        });

        verify(medicationRepository, never()).save(newMedication);

    }

    @Test
    void testCreateMedication_NullItemTypeEnumFailure() {
        // Mock data
        Medication newMedication = new Medication();
        newMedication.setInventoryItemName("Cetirizine 10mg Tablets (12 pieces)");
        newMedication.setInventoryItemDescription("10mg per piece");
        newMedication.setItemTypeEnum(null);
        newMedication.setQuantityInStock(100);
        newMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        newMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        newMedication.setAllergenEnumList(new ArrayList<>());
        newMedication.setComments("Do not take with alcohol");
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        newMedication.setDrugRestrictions(drugRestrictions);

//        Medication existingMedication = new Medication();
        when(medicationRepository.findAll()).thenReturn(List.of(newMedication));
//
//        // Test

        assertThrows(UnableToCreateMedicationException.class, () -> {
            medicationService.createMedication(newMedication);
        });

        verify(medicationRepository, never()).save(newMedication);

    }

    @Test
    void testCreateMedication_NullQuantityInStockFailure() {
        // Mock data
        Medication newMedication = new Medication();
        newMedication.setInventoryItemName("Cetirizine 10mg Tablets (12 pieces)");
        newMedication.setInventoryItemDescription("10mg per piece");
        newMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        newMedication.setQuantityInStock(null);
        newMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        newMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        newMedication.setAllergenEnumList(new ArrayList<>());
        newMedication.setComments("Do not take with alcohol");
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        newMedication.setDrugRestrictions(drugRestrictions);

//        Medication existingMedication = new Medication();
        when(medicationRepository.findAll()).thenReturn(List.of(newMedication));
//
//        // Test

        assertThrows(UnableToCreateMedicationException.class, () -> {
            medicationService.createMedication(newMedication);
        });

        verify(medicationRepository, never()).save(newMedication);

    }

    @Test
    void testCreateMedication_NullRestockPricePerQuantityFailure() {
        // Mock data
        Medication newMedication = new Medication();
        newMedication.setInventoryItemName("Cetirizine 10mg Tablets (12 pieces)");
        newMedication.setInventoryItemDescription("10mg per piece");
        newMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        newMedication.setQuantityInStock(100);
        newMedication.setRestockPricePerQuantity(null);
        newMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        newMedication.setAllergenEnumList(new ArrayList<>());
        newMedication.setComments("Do not take with alcohol");
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        newMedication.setDrugRestrictions(drugRestrictions);

//        Medication existingMedication = new Medication();
        when(medicationRepository.findAll()).thenReturn(List.of(newMedication));
       // Test

        assertThrows(UnableToCreateMedicationException.class, () -> {
            medicationService.createMedication(newMedication);
        });

        verify(medicationRepository, never()).save(newMedication);

    }
    @Test
    void testCreateMedication_NullRetailPricePerQuantityFailure() {
        // Mock data
        Medication newMedication = new Medication();
        newMedication.setInventoryItemName("Cetirizine 10mg Tablets (12 pieces)");
        newMedication.setInventoryItemDescription("10mg per piece");
        newMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        newMedication.setQuantityInStock(100);
        newMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        newMedication.setRetailPricePerQuantity(null);
        newMedication.setAllergenEnumList(new ArrayList<>());
        newMedication.setComments("Do not take with alcohol");
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        drugRestrictions.add(new DrugRestriction("Drug1"));
        newMedication.setDrugRestrictions(drugRestrictions);

//        Medication existingMedication = new Medication();
        when(medicationRepository.findAll()).thenReturn(List.of(newMedication));
        // Test

        assertThrows(UnableToCreateMedicationException.class, () -> {
            medicationService.createMedication(newMedication);
        });

        verify(medicationRepository, never()).save(newMedication);

    }
//    @Test
//    void testCreateMedication_MedicationAlreadyExists() {
//        // Mock data
//        Medication newMedication = new Medication();
//        newMedication.setInventoryItemName("Existing Medication");
//
//        Medication existingMedication = new Medication();
//        existingMedication.setInventoryItemName("Existing Medication");
//        when(medicationRepository.findAll()).thenReturn(List.of(existingMedication));
//
//        // Test
//        assertThrows(UnableToCreateMedicationException.class, () -> medicationService.createMedication(newMedication));
//
//        // Verify
//        verify(medicationRepository).findAll();
//        verify(drugRestrictionRepository, times(0)).findAll();
//        verify(drugRestrictionRepository, times(0)).delete(any());
//        verify(medicationRepository, times(0)).save(any());
//    }

    @Test
    void testDeleteMedication() {
        // Mock data
        Long inventoryItemId = 1L;

        Medication mockMedication = new Medication();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockMedication));

        List<TransactionItem> mockTransactionItems = new ArrayList<>();
        when(transactionItemService.getAllItems()).thenReturn(mockTransactionItems);

        when(drugRestrictionRepository.findAll()).thenReturn(Collections.emptyList());

        // Test

            String result = medicationService.deleteMedication(inventoryItemId);
            assertEquals("Medication with inventoryItemId  1 has been deleted successfully.", result);
        verify(medicationRepository,times(1)).delete(mockMedication);

    }

    @Test
    void testDeleteMedication_IdNotFound() {
        // Mock data
        Long inventoryItemId = 2L;
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(MedicationNotFoundException.class, () -> medicationService.deleteMedication(inventoryItemId));

        // Verify
        verify(medicationRepository, never()).delete(any(Medication.class));
    }
    @Test
    void testDeleteMedication_MedicationInTransactionItem() {
        // Mock data
        Long inventoryItemId = 1L;

        Medication mockMedication = new Medication();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(mockMedication));

//        InventoryItem mockInventoryItem = mockMedication;
        List<TransactionItem> mockTransactionItems = new ArrayList<>();
       TransactionItem mockTransactionItem = new TransactionItem(mockMedication.getInventoryItemName(),mockMedication.getInventoryItemDescription(),1,mockMedication.getRetailPricePerQuantity(),mockMedication);
        mockTransactionItems.add(mockTransactionItem);
        when(transactionItemService.getAllItems()).thenReturn(mockTransactionItems);

        when(drugRestrictionRepository.findAll()).thenReturn(Collections.emptyList());

        // Test

//        String result = medicationService.deleteMedication(inventoryItemId);
        assertEquals(mockTransactionItem.getInventoryItem().getInventoryItemId(), mockMedication.getInventoryItemId());
        assertThrows(Exception.class, () -> {
            medicationService.deleteMedication(inventoryItemId);
        });

        // Verify
        verify(medicationRepository, never()).delete(any(Medication.class));
    }

    @Test
    void testUpdateMedication() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication existingMedication = new Medication();
        existingMedication.setInventoryItemName("Cetirizine 10mg Tablets (12 pieces)");
        existingMedication.setInventoryItemDescription("10mg per piece");
        existingMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        existingMedication.setQuantityInStock(100);
        existingMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        existingMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        existingMedication.setAllergenEnumList(new ArrayList<>());
        existingMedication.setComments("Do not take with alcohol");
        List<DrugRestriction> drugRestrictions = new ArrayList<>();
//        drugRestrictions.add(new DrugRestriction("Drug1"));
        existingMedication.setDrugRestrictions(drugRestrictions);

        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Cetirizine 10mg Tablets (12 pieces)");
        updatedMedication.setInventoryItemDescription("Updated 10mg per piece");
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(100);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());
//        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingMedication));

        // Test
            Medication result = medicationService.updateMedication(inventoryItemId, updatedMedication);
            assertNotNull(result);
            assertEquals("Updated Cetirizine 10mg Tablets (12 pieces)", result.getInventoryItemName());
            assertEquals("Updated 10mg per piece", result.getInventoryItemDescription());
            assertEquals(ItemTypeEnum.MEDICINE, result.getItemTypeEnum());
            assertEquals(100, result.getQuantityInStock());
            assertEquals(BigDecimal.valueOf(5.0), result.getRestockPricePerQuantity());
            assertEquals(BigDecimal.valueOf(10), result.getRetailPricePerQuantity());
//            assertEquals(updatedMedication,result);
        verify(medicationRepository).findById(inventoryItemId);
        verify(medicationRepository,times(1)).save(result);
    }

    @Test
    void testUpdateMedication_IdNotFound() {
        // Mock data
        Long inventoryItemId = 2L;
        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Cetirizine 10mg Tablets (12 pieces)");
        updatedMedication.setInventoryItemDescription("Updated 10mg per piece");
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(100);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());

        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(MedicationNotFoundException.class, () -> medicationService.updateMedication(inventoryItemId, updatedMedication));

        // Verify
        verify(medicationRepository, never()).save(any(Medication.class));

    }
    @Test
    void testUpdateMedication_NullName() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication existingMedication = new Medication();

        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName(null);
        updatedMedication.setInventoryItemDescription("Updated 10mg per piece");
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(100);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());
//        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingMedication));

        // Test
        assertThrows(Exception.class, () -> medicationService.updateMedication(inventoryItemId, updatedMedication));

        // Verify
        verify(medicationRepository, never()).save(any(Medication.class));
    }
    @Test
    void testUpdateMedication_NullDescription() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication existingMedication = new Medication();

        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Cetirizine 10mg Tablets (12 pieces)");
        updatedMedication.setInventoryItemDescription(null);
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(100);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());
//        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingMedication));

        // Test
        assertThrows(Exception.class, () -> medicationService.updateMedication(inventoryItemId, updatedMedication));

        // Verify
        verify(medicationRepository, never()).save(any(Medication.class));
    }
    @Test
    void testUpdateMedication_NullItemTypeEnum() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication existingMedication = new Medication();

        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Cetirizine 10mg Tablets (12 pieces)");
        updatedMedication.setInventoryItemDescription("Updated 10mg per piece");
        updatedMedication.setItemTypeEnum(null);
        updatedMedication.setQuantityInStock(100);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());
//        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingMedication));

        // Test
        assertThrows(Exception.class, () -> medicationService.updateMedication(inventoryItemId, updatedMedication));

        // Verify
        verify(medicationRepository, never()).save(any(Medication.class));
    }
    @Test
    void testUpdateMedication_NullQuantityInStock() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication existingMedication = new Medication();

        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Cetirizine 10mg Tablets (12 pieces)");
        updatedMedication.setInventoryItemDescription("Updated 10mg per piece");
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(null);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());
//        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingMedication));

        // Test
        assertThrows(Exception.class, () -> medicationService.updateMedication(inventoryItemId, updatedMedication));

        // Verify
        verify(medicationRepository, never()).save(any(Medication.class));
    }
    @Test
    void testUpdateMedication_NullRestockPricePerQuantity() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication existingMedication = new Medication();

        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Cetirizine 10mg Tablets (12 pieces)");
        updatedMedication.setInventoryItemDescription("Updated 10mg per piece");
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(100);
        updatedMedication.setRestockPricePerQuantity(null);
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());
//        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingMedication));

        // Test
        assertThrows(Exception.class, () -> medicationService.updateMedication(inventoryItemId, updatedMedication));

        // Verify
        verify(medicationRepository, never()).save(any(Medication.class));
    }
    @Test
    void testUpdateMedication_NullRetailPricePerQuantity() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication existingMedication = new Medication();

        Medication updatedMedication = new Medication();
        updatedMedication.setInventoryItemName("Updated Cetirizine 10mg Tablets (12 pieces)");
        updatedMedication.setInventoryItemDescription("Updated 10mg per piece");
        updatedMedication.setItemTypeEnum(ItemTypeEnum.MEDICINE);
        updatedMedication.setQuantityInStock(100);
        updatedMedication.setRestockPricePerQuantity(BigDecimal.valueOf(5.0));
        updatedMedication.setRetailPricePerQuantity(BigDecimal.valueOf(10));
        updatedMedication.setAllergenEnumList(new ArrayList<>());
        updatedMedication.setComments("Do not take with alcohol");
        updatedMedication.setDrugRestrictions(new ArrayList<>());
//        List<DrugRestriction> drugRestrictions = new ArrayList<>();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(existingMedication));

        // Test
        assertThrows(Exception.class, () -> medicationService.updateMedication(inventoryItemId, updatedMedication));

        // Verify
        verify(medicationRepository, never()).save(any(Medication.class));
    }
    @Test
    void testGetAllMedicationByName() {
        // Mock data
        Medication medication = new Medication();
        when(medicationRepository.findAll()).thenReturn(Collections.singletonList(medication));

        // Test

        List<Medication> result = medicationService.getAllMedicationByName();
        assertEquals(1, result.size());
        verify(medicationRepository, times(1)).findAll();
    }
    @Test
    void testFindMedicationByInventoryItemId() {
        // Mock data
        Long inventoryItemId = 1L;
        Medication medication = new Medication();
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.of(medication));

        // Test
        Medication result = medicationService.findMedicationByInventoryItemId(inventoryItemId);
        assertNotNull(result);
        assertEquals(medication, result);
        verify(medicationRepository).findById(inventoryItemId);
    }

    @Test
    void testFindMedicationByInventoryItemId_MedicationNotFound() {
        // Mock data
        Long inventoryItemId = 2L;
        when(medicationRepository.findById(inventoryItemId)).thenReturn(Optional.empty());

        // Test
        assertThrows(MedicationNotFoundException.class, () -> medicationService.findMedicationByInventoryItemId(inventoryItemId));

        // Verify
        verify(medicationRepository, times(1)).findById(inventoryItemId);
    }

    @Test
    void testGetAllMedicationsByAllergy() {
        // Mock data
        Long pId = 1L;
        ElectronicHealthRecord ehr = new ElectronicHealthRecord();
        ehr.setElectronicHealthRecordId(pId);
        MedicalHistoryRecord medicalHistoryRecord = new MedicalHistoryRecord("EGG", "Doctor Maria Garcia", LocalDateTime.of(2008, 4, 18, 14, 30, 0), LocalDateTime.of(2009, 9, 2, 9, 45, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);
//        ehr.getListOfMedicalHistoryRecords().add(medicalHistoryRecord);
        when(electronicHealthRecordRepository.findById(pId)).thenReturn(Optional.of(ehr));
        Collection<AllergenEnum> allergenList2 = new ArrayList<>();
        allergenList2.add(AllergenEnum.EGG);
        Medication medication = new Medication("Augmentin 228mg Suspension (1 bottle)", "5ml per bottle", ItemTypeEnum.MEDICINE, 100,
                BigDecimal.valueOf(3), BigDecimal.TEN, allergenList2, "", new ArrayList<>());
        List<Medication> medicationList = new ArrayList<>();
        medicationList.add(medication);
        when(medicationRepository.findAll()).thenReturn(medicationList);
        System.out.println(medicationList.size());

        // Test
        List<Medication> result = medicationService.getAllMedicationsByAllergy(pId);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(electronicHealthRecordRepository).findById(pId);
        verify(medicationRepository).findAll();
    }
    @Test
    void testGetAllInpatientMedicationsByAllergy() {
        // Mock data
        Long pId = 1L;
        ElectronicHealthRecord ehr = new ElectronicHealthRecord();
        ehr.setElectronicHealthRecordId(pId);
        MedicalHistoryRecord medicalHistoryRecord = new MedicalHistoryRecord("EGG", "Doctor Maria Garcia", LocalDateTime.of(2008, 4, 18, 14, 30, 0), LocalDateTime.of(2009, 9, 2, 9, 45, 0), PriorityEnum.MEDIUM, ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);
//        ehr.getListOfMedicalHistoryRecords().add(medicalHistoryRecord);
        when(electronicHealthRecordRepository.findById(pId)).thenReturn(Optional.of(ehr));
        Collection<AllergenEnum> allergenList2 = new ArrayList<>();
        allergenList2.add(AllergenEnum.EGG);
        Medication medication = new Medication("Augmentin 228mg Suspension (1 bottle)", "5ml per bottle", ItemTypeEnum.MEDICINE_INPATIENT, 100,
                BigDecimal.valueOf(3), BigDecimal.TEN, allergenList2, "", new ArrayList<>());
        List<Medication> medicationList = new ArrayList<>();
        medicationList.add(medication);
        when(medicationRepository.findAll()).thenReturn(medicationList);
        System.out.println(medicationList.size());

        // Test
        List<Medication> result = medicationService.getAllMedicationsByAllergy(pId);
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(electronicHealthRecordRepository).findById(pId);
        verify(medicationRepository).findAll();
    }
    @Test
    void testGetAllInpatientMedication() {
        // Mock data
        List<Medication> medicationList = new ArrayList<>();
        medicationList.add(new Medication());
        when(medicationRepository.findByItemTypeEnum(ItemTypeEnum.MEDICINE_INPATIENT)).thenReturn(medicationList);

        // Test

        List<Medication> result = medicationService.getAllInpatientMedication();
        assertEquals(1, result.size());
        verify(medicationRepository, times(1)).findByItemTypeEnum(ItemTypeEnum.MEDICINE_INPATIENT);
    }
//    @Test
//    void testGetAllMedicationsByAllergy_EHRNotFound() {
//        // Mock data
//        Long pId = 1L;
//        when(electronicHealthRecordRepository.findById(pId)).thenReturn(Optional.empty());
//
//        // Test
//        assertThrows(MedicationNotFoundException.class, () -> medicationService.getAllMedicationsByAllergy(pId));
//
//        // Verify
//        verify(electronicHealthRecordRepository).findById(pId);
//        verify(medicationRepository, times(0)).findAll();
//    }
}
