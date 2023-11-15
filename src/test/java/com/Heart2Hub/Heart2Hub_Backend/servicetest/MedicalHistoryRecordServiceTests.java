package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
    void testCreateMedicalHistoryRecord_EHRNotFound() {
        // Mocking
        Long electronicHealthRecordId = 1L;
        MedicalHistoryRecord newMedicalHistoryRecord = new MedicalHistoryRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Test
        assertThrows(ElectronicHealthRecordNotFoundException.class, () -> medicalHistoryRecordService.createMedicalHistoryRecord(electronicHealthRecordId, newMedicalHistoryRecord));

        // Verify
        verify(electronicHealthRecordRepository, times(1)).findById(electronicHealthRecordId);
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
    void testDeleteMedicalHistoryRecord_MedicalHistoryRecordNotFound() {
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
    void testUpdateMedicalHistoryRecord_MedicalHistoryRecordNotFound() {
        // Mocking
        Long medicalHistoryRecordId = 1L;
        MedicalHistoryRecord updatedMedicalHistoryRecord = new MedicalHistoryRecord();
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
