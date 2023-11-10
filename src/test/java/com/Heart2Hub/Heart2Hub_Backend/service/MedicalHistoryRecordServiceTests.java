package com.Heart2Hub.Heart2Hub_Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.MedicalHistoryRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateMedicalHistoryRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.MedicalHistoryRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.MedicalHistoryRecordService;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MedicalHistoryRecordServiceTests {

    @Mock
    private MedicalHistoryRecordRepository medicalHistoryRecordRepository;

    @Mock
    private ElectronicHealthRecordRepository electronicHealthRecordRepository;

    @InjectMocks
    private MedicalHistoryRecordService medicalHistoryRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMedicalHistoryRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        MedicalHistoryRecord newMedicalHistoryRecord = new MedicalHistoryRecord();

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            MedicalHistoryRecord createdMedicalHistoryRecord = medicalHistoryRecordService.createMedicalHistoryRecord(electronicHealthRecordId, newMedicalHistoryRecord);
            assertNotNull(createdMedicalHistoryRecord);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(medicalHistoryRecordRepository).save(newMedicalHistoryRecord);
            verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);
        } catch (UnableToCreateMedicalHistoryRecordException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testDeleteMedicalHistoryRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;
        Long medicalHistoryRecordId = 2L;

        MedicalHistoryRecord mockMedicalHistoryRecord = new MedicalHistoryRecord();
        when(medicalHistoryRecordRepository.findById(medicalHistoryRecordId)).thenReturn(Optional.of(mockMedicalHistoryRecord));

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            String result = medicalHistoryRecordService.deleteMedicalHistoryRecord(electronicHealthRecordId, medicalHistoryRecordId);
            assertEquals("MedicalHistoryRecord with MedicalHistoryRecordId " + medicalHistoryRecordId + " has been deleted successfully.", result);
            verify(medicalHistoryRecordRepository).findById(medicalHistoryRecordId);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(medicalHistoryRecordRepository).delete(mockMedicalHistoryRecord);
            verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);
        } catch (MedicalHistoryRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testUpdateMedicalHistoryRecord() {
        // Mock data
        Long medicalHistoryRecordId = 1L;
        MedicalHistoryRecord updatedMedicalHistoryRecord = new MedicalHistoryRecord();
        updatedMedicalHistoryRecord.setDescription("Updated Description");
        updatedMedicalHistoryRecord.setPriorityEnum(PriorityEnum.HIGH);
        updatedMedicalHistoryRecord.setProblemTypeEnum(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC);

        MedicalHistoryRecord mockMedicalHistoryRecord = new MedicalHistoryRecord();
        when(medicalHistoryRecordRepository.findById(medicalHistoryRecordId)).thenReturn(Optional.of(mockMedicalHistoryRecord));

        // Test
        try {
            MedicalHistoryRecord result = medicalHistoryRecordService.updateMedicalHistoryRecord(medicalHistoryRecordId, updatedMedicalHistoryRecord);
            assertNotNull(result);
            assertEquals("Updated Description", result.getDescription());
            assertEquals(PriorityEnum.HIGH, result.getPriorityEnum());
            assertEquals(ProblemTypeEnum.ALLERGIES_AND_IMMUNOLOGIC, result.getProblemTypeEnum());
            verify(medicalHistoryRecordRepository).findById(medicalHistoryRecordId);
            verify(medicalHistoryRecordRepository).save(mockMedicalHistoryRecord);
        } catch (MedicalHistoryRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testGetAllMedicalHistoryRecordsByElectronicHealthRecordId() {
        // Mock data
        Long electronicHealthRecordId = 1L;

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            List<MedicalHistoryRecord> result = medicalHistoryRecordService.getAllMedicalHistoryRecordsByElectronicHealthRecordId(electronicHealthRecordId);
            assertNotNull(result);
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        } catch (MedicalHistoryRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

    @Test
    void testDeleteAllMedicalHistoryRecordsFromElectronicHealthRecord() {
        // Mock data
        Long electronicHealthRecordId = 1L;

        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Test
        try {
            ElectronicHealthRecord result = medicalHistoryRecordService.deleteAllMedicalHistoryRecordsFromElectronicHealthRecord(electronicHealthRecordId);
            assertNotNull(result);
            assertTrue(result.getListOfMedicalHistoryRecords().isEmpty());
            verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
            verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);
        } catch (ElectronicHealthRecordNotFoundException e) {
            fail("Exception not expected");
        }
    }

}