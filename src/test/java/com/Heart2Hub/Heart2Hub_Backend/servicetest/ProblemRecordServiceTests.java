package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ProblemTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.ProblemRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.ProblemRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateProblemRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.ProblemRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicalHistoryRecordService;
import com.Heart2Hub.Heart2Hub_Backend.service.ProblemRecordService;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProblemRecordServiceTests {

    @Mock
    private ProblemRecordRepository problemRecordRepository;

    @Mock
    private ElectronicHealthRecordRepository electronicHealthRecordRepository;

    @Mock
    private MedicalHistoryRecordService medicalHistoryRecordService;

    @InjectMocks
    private ProblemRecordService problemRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProblemRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        ProblemRecord newProblemRecord = new ProblemRecord();
        newProblemRecord.setDescription("New Problem");
        newProblemRecord.setCreatedBy("User123");
        newProblemRecord.setPriorityEnum(PriorityEnum.HIGH);
        newProblemRecord.setProblemTypeEnum(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));
        when(problemRecordRepository.save(newProblemRecord)).thenReturn(newProblemRecord);

        // Test
        try {
            ProblemRecord createdProblemRecord = problemRecordService.createProblemRecord(electronicHealthRecordId, newProblemRecord);
            assertNotNull(createdProblemRecord);
            assertEquals("New Problem", createdProblemRecord.getDescription());
            assertEquals("User123", createdProblemRecord.getCreatedBy());
            assertEquals(PriorityEnum.HIGH, createdProblemRecord.getPriorityEnum());
            assertEquals(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC, createdProblemRecord.getProblemTypeEnum());

            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(problemRecordRepository).save(newProblemRecord);
            verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);

        } catch (UnableToCreateProblemRecordException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testCreateProblemRecord_ElectronicHealthRecordNotFound() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        ProblemRecord newProblemRecord = new ProblemRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ElectronicHealthRecordNotFoundException.class, () -> problemRecordService.createProblemRecord(electronicHealthRecordId, newProblemRecord));

        // Verify
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        verify(problemRecordRepository, times(0)).save(newProblemRecord);
    }

    @Test
    void testCreateAllergyRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        ProblemRecord newProblemRecord = new ProblemRecord();
        newProblemRecord.setDescription("New Allergy");
        newProblemRecord.setCreatedBy("User456");
        newProblemRecord.setPriorityEnum(PriorityEnum.LOW);
        newProblemRecord.setProblemTypeEnum(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);

        MedicalHistoryRecord newMedicalHistoryRecord = new MedicalHistoryRecord();
        newMedicalHistoryRecord.setDescription("New Allergy");
        newMedicalHistoryRecord.setCreatedBy("User456");
        newMedicalHistoryRecord.setCreatedDate(LocalDateTime.now());
        newMedicalHistoryRecord.setPriorityEnum(PriorityEnum.LOW);
        newMedicalHistoryRecord.setProblemTypeEnum(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);

        when(medicalHistoryRecordService.createMedicalHistoryRecord(eq(electronicHealthRecordId), any()))
                .thenReturn(newMedicalHistoryRecord);

        // Test
        try {
            MedicalHistoryRecord createdMedicalHistoryRecord = problemRecordService.createAllergyRecord(electronicHealthRecordId, newProblemRecord);
            assertNotNull(createdMedicalHistoryRecord);
            assertEquals("New Allergy", createdMedicalHistoryRecord.getDescription());
            assertEquals("User456", createdMedicalHistoryRecord.getCreatedBy());
            assertEquals(PriorityEnum.LOW, createdMedicalHistoryRecord.getPriorityEnum());
            assertEquals(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC, createdMedicalHistoryRecord.getProblemTypeEnum());

            verify(medicalHistoryRecordService).createMedicalHistoryRecord(electronicHealthRecordId, newMedicalHistoryRecord);

        } catch (UnableToCreateProblemRecordException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testCreateAllergyRecord_MedicalHistoryRecordCreationFailure() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        ProblemRecord newProblemRecord = new ProblemRecord();
        newProblemRecord.setDescription("New Allergy");
        newProblemRecord.setCreatedBy("User456");
        newProblemRecord.setPriorityEnum(PriorityEnum.LOW);
        newProblemRecord.setProblemTypeEnum(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);

        when(medicalHistoryRecordService.createMedicalHistoryRecord(eq(electronicHealthRecordId), any()))
                .thenThrow(UnableToCreateProblemRecordException.class);

        // Test
        assertThrows(UnableToCreateProblemRecordException.class, () ->
                problemRecordService.createAllergyRecord(electronicHealthRecordId, newProblemRecord));

        // Verify
        verify(medicalHistoryRecordService).createMedicalHistoryRecord(electronicHealthRecordId, new MedicalHistoryRecord());
        verifyNoMoreInteractions(medicalHistoryRecordService);
    }


    @Test
    void testDeleteProblemRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long problemRecordId = 2L;

        ProblemRecord mockProblemRecord = new ProblemRecord();
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(mockProblemRecord));

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            String result = problemRecordService.deleteProblemRecord(electronicHealthRecordId, problemRecordId);
            assertEquals("ProblemRecord with ProblemRecordId 2 has been deleted successfully.", result);
            verify(problemRecordRepository).findById(problemRecordId);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(problemRecordRepository).delete(mockProblemRecord);
            verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);

        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testDeleteProblemRecord_ElectronicHealthRecordNotFound() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long problemRecordId = 2L;
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(new ProblemRecord()));
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ElectronicHealthRecordNotFoundException.class, () -> problemRecordService.deleteProblemRecord(electronicHealthRecordId, problemRecordId));

        // Verify
        verify(problemRecordRepository).findById(problemRecordId);
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        verify(problemRecordRepository, times(0)).delete(any());
        verify(electronicHealthRecordRepository, times(0)).save(any());
    }

    @Test
    void testUpdateProblemRecord() {
        // Mock data
        Long problemRecordId = 1L;
        ProblemRecord updatedProblemRecord = new ProblemRecord();
        updatedProblemRecord.setDescription("Updated Problem");
        updatedProblemRecord.setPriorityEnum(PriorityEnum.LOW);
        updatedProblemRecord.setProblemTypeEnum(ProblemTypeEnum.OPTHALMOLOGIC);

        ProblemRecord mockProblemRecord = new ProblemRecord();
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(mockProblemRecord));

        // Test
        try {
            ProblemRecord result = problemRecordService.updateProblemRecord(problemRecordId, updatedProblemRecord);
            assertNotNull(result);
            assertEquals("Updated Problem", result.getDescription());
            assertEquals(PriorityEnum.LOW, result.getPriorityEnum());
            assertEquals(ProblemTypeEnum.OPTHALMOLOGIC, result.getProblemTypeEnum());
            verify(problemRecordRepository).findById(problemRecordId);
            verify(problemRecordRepository).save(mockProblemRecord);

        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testUpdateProblemRecord_ProblemRecordNotFound() {
        // Mock data
        Long problemRecordId = 1L;
        ProblemRecord updatedProblemRecord = new ProblemRecord();
        updatedProblemRecord.setDescription("Updated Problem");
        updatedProblemRecord.setPriorityEnum(PriorityEnum.LOW);
        updatedProblemRecord.setProblemTypeEnum(ProblemTypeEnum.OPTHALMOLOGIC);

        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ProblemRecordNotFoundException.class, () -> problemRecordService.updateProblemRecord(problemRecordId, updatedProblemRecord));

        // Verify
        verify(problemRecordRepository).findById(problemRecordId);
        verifyNoMoreInteractions(problemRecordRepository);
    }


    @Test
    void testGetAllProblemRecordsByElectronicHealthRecordId() {
        // Mock data
        Long electronicHealthRecordId = 1L;

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            List<ProblemRecord> result = problemRecordService.getAllProblemRecordsByElectronicHealthRecordId(electronicHealthRecordId);
            assertNotNull(result);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);

        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testGetAllProblemRecordsByElectronicHealthRecordId_ElectronicHealthRecordNotFound() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ElectronicHealthRecordNotFoundException.class, () -> problemRecordService.getAllProblemRecordsByElectronicHealthRecordId(electronicHealthRecordId));

        // Verify
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
    }


    @Test
    void testResolveProblemRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long problemRecordId = 2L;

        ProblemRecord mockProblemRecord = new ProblemRecord();
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(mockProblemRecord));

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        MedicalHistoryRecord mockMedicalHistoryRecord = new MedicalHistoryRecord();
        when(medicalHistoryRecordService.createMedicalHistoryRecord(eq(electronicHealthRecordId), any()))
                .thenReturn(mockMedicalHistoryRecord);

        // Test
        try {
            MedicalHistoryRecord result = problemRecordService.resolveProblemRecord(electronicHealthRecordId, problemRecordId);
            assertNotNull(result);
            verify(problemRecordRepository).findById(problemRecordId);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(problemRecordRepository).delete(mockProblemRecord);
            verify(medicalHistoryRecordService).createMedicalHistoryRecord(eq(electronicHealthRecordId), any());

        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testResolveProblemRecord_ElectronicHealthRecordNotFound() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long problemRecordId = 2L;
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(new ProblemRecord()));
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ElectronicHealthRecordNotFoundException.class, () -> problemRecordService.resolveProblemRecord(electronicHealthRecordId, problemRecordId));

        // Verify
        verify(problemRecordRepository).findById(problemRecordId);
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        verify(problemRecordRepository, times(0)).delete(any());
        verify(medicalHistoryRecordService, times(0)).createMedicalHistoryRecord(eq(electronicHealthRecordId), any());
    }
}
