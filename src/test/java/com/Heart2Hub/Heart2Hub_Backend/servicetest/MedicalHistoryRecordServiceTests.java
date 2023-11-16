package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.Heart2Hub.Heart2Hub_Backend.enumeration.PriorityEnum;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.ProblemTypeEnum;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.MedicalHistoryRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.MedicalHistoryRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicalHistoryRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicalHistoryRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicalHistoryRecordService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
class MedicalHistoryRecordServiceTests {

    @InjectMocks
    private MedicalHistoryRecordService medicalHistoryRecordService;
    @Mock
    private MedicalHistoryRecordRepository medicalHistoryRecordRepository;
    @Mock
    private ElectronicHealthRecordRepository electronicHealthRecordRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

    @Test
    void testCreateMedicalHistoryRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;

        String dateTimeString1 = "2023-11-11 12:00:00";
        String dateTimeString2 = "2024-11-11 12:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the strings to LocalDateTime
        LocalDateTime localDateTime1 = LocalDateTime.parse(dateTimeString1, formatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse(dateTimeString2, formatter);
        MedicalHistoryRecord medicalHistory = new MedicalHistoryRecord("Heart Pain", "Doctor X", localDateTime1, localDateTime2, PriorityEnum.HIGH, ProblemTypeEnum.CARDIOVASCULAR);

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            MedicalHistoryRecord result = medicalHistoryRecordService.createMedicalHistoryRecord(electronicHealthRecordId, medicalHistory);
            assertNotNull(result);

            // Add this assertion to verify that the createdMedicalHistoryRecord is equal to newMedicalHistoryRecord
            assertEquals(medicalHistory, result);

            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(medicalHistoryRecordRepository).save(medicalHistory);
            verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);
        } catch (UnableToCreateMedicalHistoryRecordException e) {
            fail("Exception not expected");
        }
    }


    @Test
    void testCreateMedicalHistoryRecord_IdNotFoundFailure() {
        // Mocking
        Long electronicHealthRecordId = 2L;
        String dateTimeString1 = "2023-11-11 12:00:00";
        String dateTimeString2 = "2024-11-11 12:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the strings to LocalDateTime
        LocalDateTime localDateTime1 = LocalDateTime.parse(dateTimeString1, formatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse(dateTimeString2, formatter);
        MedicalHistoryRecord newMedicalHistoryRecord = new MedicalHistoryRecord("Heart Pain", "Doctor X", localDateTime1, localDateTime2, PriorityEnum.HIGH, ProblemTypeEnum.CARDIOVASCULAR);
//        MedicalHistoryRecord newMedicalHistoryRecord = new MedicalHistoryRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(UnableToCreateMedicalHistoryRecordException.class, () -> medicalHistoryRecordService.createMedicalHistoryRecord(electronicHealthRecordId, newMedicalHistoryRecord));

        // Verify
        verify(electronicHealthRecordRepository, times(1)).findById(electronicHealthRecordId);
    }


    @Test
    void testDeleteMedicalHistoryRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long medicalHistoryRecordId = 1L;

        MedicalHistoryRecord mockMedicalHistoryRecord = new MedicalHistoryRecord();
        when(medicalHistoryRecordRepository.findById(medicalHistoryRecordId)).thenReturn(Optional.of(mockMedicalHistoryRecord));

        // Use the same ID for findById in the test as used in the service
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(new ElectronicHealthRecord()));

        // Test
        try {
            String result = medicalHistoryRecordService.deleteMedicalHistoryRecord(electronicHealthRecordId, medicalHistoryRecordId);

            assertNotNull(result);
            assertEquals("MedicalHistoryRecord with MedicalHistoryRecordId " + medicalHistoryRecordId + " has been deleted successfully.", result);

            verify(medicalHistoryRecordRepository).findById(medicalHistoryRecordId);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(medicalHistoryRecordRepository).delete(mockMedicalHistoryRecord);
            // Make sure that you don't need to save the electronicHealthRecord when deleting a medical history record.
        } catch (MedicalHistoryRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }



    @Test
    void testDeleteMedicalHistoryRecord_IdNotFoundFailure() {
        // Mocking
        Long electronicHealthRecordId = 1L;
        Long medicalHistoryRecordId = 2L;
        when(medicalHistoryRecordRepository.findById(medicalHistoryRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(MedicalHistoryRecordNotFoundException.class, () -> medicalHistoryRecordService.deleteMedicalHistoryRecord(electronicHealthRecordId, medicalHistoryRecordId));

        // Verify
        verify(medicalHistoryRecordRepository, times(1)).findById(medicalHistoryRecordId);
    }

    @Test
    void testUpdateMedicalHistoryRecord() {
        // Mock data
        Long medicalHistoryRecordId = 1L;
        MedicalHistoryRecord updatedMedicalHistoryRecord = new MedicalHistoryRecord();
        updatedMedicalHistoryRecord.setDescription("Updated Heart Pain");
        updatedMedicalHistoryRecord.setCreatedBy("Updated Doctor X");
        updatedMedicalHistoryRecord.setPriorityEnum(PriorityEnum.LOW);
        updatedMedicalHistoryRecord.setProblemTypeEnum(ProblemTypeEnum.CARDIOVASCULAR);
        String dateTimeString1 = "2023-11-11 12:00:00";
        String dateTimeString2 = "2024-11-11 12:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the strings to LocalDateTime
        LocalDateTime localDateTime1 = LocalDateTime.parse(dateTimeString1, formatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse(dateTimeString2, formatter);
        updatedMedicalHistoryRecord.setCreatedDate(localDateTime1);
        updatedMedicalHistoryRecord.setResolvedDate(localDateTime2);

        MedicalHistoryRecord mockMedicalHistoryRecord = new MedicalHistoryRecord();
        when(medicalHistoryRecordRepository.findById(medicalHistoryRecordId)).thenReturn(Optional.of(mockMedicalHistoryRecord));

        // Test
        try {
            MedicalHistoryRecord result = medicalHistoryRecordService.updateMedicalHistoryRecord(medicalHistoryRecordId, updatedMedicalHistoryRecord);
            assertNotNull(result);
            assertEquals("Updated Heart Pain", result.getDescription());
            assertEquals(PriorityEnum.LOW, result.getPriorityEnum());
            assertEquals(ProblemTypeEnum.CARDIOVASCULAR, result.getProblemTypeEnum());

            verify(medicalHistoryRecordRepository).findById(medicalHistoryRecordId);
            verify(medicalHistoryRecordRepository).save(mockMedicalHistoryRecord);
        } catch (MedicalHistoryRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testUpdateMedicalHistoryRecord_MedicalHistoryRecordNotFound() {
        // Mocking
        Long medicalHistoryRecordId = 2L;
        MedicalHistoryRecord updatedMedicalHistoryRecord = new MedicalHistoryRecord();
        updatedMedicalHistoryRecord.setDescription("Updated Heart Pain");
        updatedMedicalHistoryRecord.setCreatedBy("Updated Doctor X");
        updatedMedicalHistoryRecord.setPriorityEnum(PriorityEnum.LOW);
        updatedMedicalHistoryRecord.setProblemTypeEnum(ProblemTypeEnum.CARDIOVASCULAR);
        String dateTimeString1 = "2023-11-11 12:00:00";
        String dateTimeString2 = "2024-11-11 12:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parsing the strings to LocalDateTime
        LocalDateTime localDateTime1 = LocalDateTime.parse(dateTimeString1, formatter);
        LocalDateTime localDateTime2 = LocalDateTime.parse(dateTimeString2, formatter);
        updatedMedicalHistoryRecord.setCreatedDate(localDateTime1);
        updatedMedicalHistoryRecord.setResolvedDate(localDateTime2);
//        MedicalHistoryRecord updatedMedicalHistoryRecord = new MedicalHistoryRecord();
        when(medicalHistoryRecordRepository.findById(medicalHistoryRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(MedicalHistoryRecordNotFoundException.class, () -> medicalHistoryRecordService.updateMedicalHistoryRecord(medicalHistoryRecordId, updatedMedicalHistoryRecord));

        // Verify
        verify(medicalHistoryRecordRepository, times(1)).findById(medicalHistoryRecordId);
    }

    @Test
    void testGetAllMedicalHistoryRecordsByElectronicHealthRecordId() {
        // Mock data
        Long electronicHealthRecordId = 1L;

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        mockElectronicHealthRecord.setListOfMedicalHistoryRecords(Arrays.asList(new MedicalHistoryRecord()));

        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            List<MedicalHistoryRecord> result = medicalHistoryRecordService.getAllMedicalHistoryRecordsByElectronicHealthRecordId(electronicHealthRecordId);
            assertNotNull(result);

            System.out.println("Actual Size of Result: " + result.size());

            assertEquals(1, result.size());
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        } catch (MedicalHistoryRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testGetAllMedicalHistoryRecordsByElectronicHealthRecordId_EHRNotFound() {
        // Mocking
        Long electronicHealthRecordId = 1L;
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ElectronicHealthRecordNotFoundException.class, () -> medicalHistoryRecordService.getAllMedicalHistoryRecordsByElectronicHealthRecordId(electronicHealthRecordId));

        // Verify
        verify(electronicHealthRecordRepository, times(1)).findById(electronicHealthRecordId);
    }


}
