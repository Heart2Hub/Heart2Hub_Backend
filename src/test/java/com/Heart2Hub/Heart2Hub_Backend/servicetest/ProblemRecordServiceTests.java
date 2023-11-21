package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ProblemTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicalHistoryRecordException;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class ProblemRecordServiceTests {

    @InjectMocks
    private ProblemRecordService problemRecordService;
    @Mock
    private ProblemRecordRepository problemRecordRepository;
    @Mock
    private ElectronicHealthRecordRepository electronicHealthRecordRepository;
    @Mock
    private MedicalHistoryRecordService medicalHistoryRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

    @Test
    void testCreateProblemRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        ProblemRecord newProblemRecord = new ProblemRecord();
        newProblemRecord.setDescription("Heart Pain");
        newProblemRecord.setCreatedBy("Doctor X");
        newProblemRecord.setPriorityEnum(PriorityEnum.HIGH);
        newProblemRecord.setProblemTypeEnum(ProblemTypeEnum.CARDIOVASCULAR);

        String dateTimeString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        newProblemRecord.setCreatedDate(localDateTime);

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));
        when(problemRecordRepository.save(newProblemRecord)).thenReturn(newProblemRecord);

        // Test
        try {
            ProblemRecord result = problemRecordService.createProblemRecord(electronicHealthRecordId, newProblemRecord);
            assertNotNull(result);
            assertEquals("Heart Pain", result.getDescription());
            assertEquals("Doctor X", result.getCreatedBy());
            assertEquals(PriorityEnum.HIGH, result.getPriorityEnum());
            assertEquals(ProblemTypeEnum.CARDIOVASCULAR, result.getProblemTypeEnum());
            assertEquals(localDateTime, result.getCreatedDate());

            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(problemRecordRepository).save(newProblemRecord);
            verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);

        } catch (UnableToCreateProblemRecordException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testCreateProblemRecord_IdNotFoundFailure() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        ProblemRecord newProblemRecord = new ProblemRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateProblemRecordException.class, () -> problemRecordService.createProblemRecord(electronicHealthRecordId, newProblemRecord));

        // Verify
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        verify(problemRecordRepository, times(0)).save(newProblemRecord);
    }

    @Test
    void testCreateAllergyRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        ProblemRecord newProblemRecord = new ProblemRecord();
        newProblemRecord.setDescription("Egg");
        newProblemRecord.setCreatedBy("Doctor X");
        newProblemRecord.setPriorityEnum(PriorityEnum.HIGH);
        newProblemRecord.setProblemTypeEnum(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);
        String dateTimeString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        newProblemRecord.setCreatedDate(localDateTime);


        MedicalHistoryRecord newMedicalHistoryRecord = new MedicalHistoryRecord();
        newMedicalHistoryRecord.setDescription("Egg");
        newMedicalHistoryRecord.setCreatedBy("Doctor X");
        newMedicalHistoryRecord.setCreatedDate(LocalDateTime.now());
        newMedicalHistoryRecord.setPriorityEnum(PriorityEnum.HIGH);
        newMedicalHistoryRecord.setProblemTypeEnum(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);

        when(medicalHistoryRecordService.createMedicalHistoryRecord(eq(electronicHealthRecordId), any()))
                .thenReturn(newMedicalHistoryRecord);

        // Test
        try {
            MedicalHistoryRecord result = problemRecordService.createAllergyRecord(electronicHealthRecordId, newProblemRecord);
            assertNotNull(result);
            assertEquals("Egg", result.getDescription());
            assertEquals("Doctor X", result.getCreatedBy());
            assertEquals(PriorityEnum.HIGH, result.getPriorityEnum());
            assertEquals(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC, result.getProblemTypeEnum());

            verify(medicalHistoryRecordService).createMedicalHistoryRecord(eq(electronicHealthRecordId), any());

        } catch (UnableToCreateProblemRecordException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testCreateAllergyRecord_IdNotFoundFailure() {
        Long electronicHealthRecordId = 2L;
        ProblemRecord problemRecord = new ProblemRecord(/* initialize with appropriate values */);

        // Mocking behavior to throw an exception when createMedicalHistoryRecord is called
        when(medicalHistoryRecordService.createMedicalHistoryRecord(anyLong(), any()))
                .thenThrow(new UnableToCreateMedicalHistoryRecordException("Simulated failure"));

        try {
            // Act
            problemRecordService.createAllergyRecord(electronicHealthRecordId, problemRecord);
        } catch (UnableToCreateMedicalHistoryRecordException e) {
            // Assert
            assert(e.getMessage().equals("Simulated failure"));
        }

        verify(medicalHistoryRecordService, times(1)).createMedicalHistoryRecord(
                eq(electronicHealthRecordId), any(MedicalHistoryRecord.class));
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
            assertNotNull(result);
            assertEquals("ProblemRecord with ProblemRecordId 2 has been deleted successfully.", result);

            verify(problemRecordRepository).findById(problemRecordId);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(problemRecordRepository).delete(mockProblemRecord);


        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }


    @Test
    void testDeleteProblemRecord_IdNotFoundFailure() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long problemRecordId = 2L;
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(new ProblemRecord()));
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ProblemRecordNotFoundException.class, () -> problemRecordService.deleteProblemRecord(electronicHealthRecordId, problemRecordId));

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
        updatedProblemRecord.setDescription("Updated Heart Pain");
        updatedProblemRecord.setCreatedBy("Updated Doctor X");
        updatedProblemRecord.setPriorityEnum(PriorityEnum.HIGH);
        updatedProblemRecord.setProblemTypeEnum(ProblemTypeEnum.CARDIOVASCULAR);
        String dateTimeString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        updatedProblemRecord.setCreatedDate(localDateTime);

        ProblemRecord mockProblemRecord = new ProblemRecord();
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(mockProblemRecord));

        // Test
        try {
            ProblemRecord result = problemRecordService.updateProblemRecord(problemRecordId, updatedProblemRecord);
            assertNotNull(result);
            assertEquals("Updated Heart Pain", result.getDescription());
            assertEquals(PriorityEnum.HIGH, result.getPriorityEnum());
            assertEquals(ProblemTypeEnum.CARDIOVASCULAR, result.getProblemTypeEnum());
            verify(problemRecordRepository).findById(problemRecordId);
            verify(problemRecordRepository).save(mockProblemRecord);

        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testUpdateProblemRecord_IdNotFoundFailure() {
        // Mock data
        Long problemRecordId = 2L;
        ProblemRecord updatedProblemRecord = new ProblemRecord();
        updatedProblemRecord.setDescription("Updated Heart Pain");
        updatedProblemRecord.setCreatedBy("Updated Doctor X");
        updatedProblemRecord.setPriorityEnum(PriorityEnum.HIGH);
        updatedProblemRecord.setProblemTypeEnum(ProblemTypeEnum.CARDIOVASCULAR);
        String dateTimeString = "2023-11-11 12:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, formatter);
        updatedProblemRecord.setCreatedDate(localDateTime);

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
        // Assuming getProblemRecords() is the method in ElectronicHealthRecord that returns the list of problem records
        mockElectronicHealthRecord.setListOfProblemRecords(Collections.singletonList(new ProblemRecord()));

        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            List<ProblemRecord> result = problemRecordService.getAllProblemRecordsByElectronicHealthRecordId(electronicHealthRecordId);
            assertNotNull(result);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            assertEquals(1, result.size()); // Add this line

        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }


    @Test
    void testGetAllProblemRecordsByElectronicHealthRecordId_ProblemRecordNotFound() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ProblemRecordNotFoundException.class, () -> problemRecordService.getAllProblemRecordsByElectronicHealthRecordId(electronicHealthRecordId));

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
            assertEquals(mockMedicalHistoryRecord.getDescription(), result.getDescription());
            assertEquals(mockMedicalHistoryRecord.getCreatedBy(), result.getCreatedBy());
            assertEquals(mockMedicalHistoryRecord.getProblemTypeEnum(), result.getProblemTypeEnum());
            assertEquals(mockMedicalHistoryRecord.getPriorityEnum(), result.getPriorityEnum());

            verify(problemRecordRepository).findById(problemRecordId);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(problemRecordRepository).delete(mockProblemRecord);
            verify(medicalHistoryRecordService).createMedicalHistoryRecord(eq(electronicHealthRecordId), any());

        } catch (ProblemRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testResolveProblemRecord_ProblemRecordNotFound() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long problemRecordId = 2L;
        when(problemRecordRepository.findById(problemRecordId)).thenReturn(Optional.of(new ProblemRecord()));
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ProblemRecordNotFoundException.class, () -> problemRecordService.resolveProblemRecord(electronicHealthRecordId, problemRecordId));

        // Verify
        verify(problemRecordRepository).findById(problemRecordId);
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        verify(problemRecordRepository, times(0)).delete(any());
        verify(medicalHistoryRecordService, times(0)).createMedicalHistoryRecord(eq(electronicHealthRecordId), any());
    }
}
