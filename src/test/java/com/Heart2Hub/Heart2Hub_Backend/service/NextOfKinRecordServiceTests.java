package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.NextOfKinRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateNextOfKinRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.NextOfKinRecordRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class NextOfKinRecordServiceTests {

    @Mock
    private NextOfKinRecordRepository nextOfKinRecordRepository;

    @Mock
    private ElectronicHealthRecordRepository electronicHealthRecordRepository;

    @InjectMocks
    private NextOfKinRecordService nextOfKinRecordService;

    @Test
    void testCreateNextOfKinRecord() throws UnableToCreateNextOfKinRecordException {
        // Arrange
        Long electronicHealthRecordId = 1L;
        NextOfKinRecord newNextOfKinRecord = new NextOfKinRecord();
        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Act
        NextOfKinRecord result = nextOfKinRecordService.createNextOfKinRecord(electronicHealthRecordId, newNextOfKinRecord);

        // Assert
        assertEquals(newNextOfKinRecord, result);
        verify(nextOfKinRecordRepository).save(newNextOfKinRecord);
        verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);
    }

    @Test
    void testGetNextOfKinRecordsByEHRId() throws ElectronicHealthRecordNotFoundException {
        // Arrange
        Long electronicHealthRecordId = 1L;
        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Act
        nextOfKinRecordService.getNextOfKinRecordsByEHRId(electronicHealthRecordId);

        // Assert
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
    }

    @Test
    void testDeleteNextOfKinRecord() throws NextOfKinRecordNotFoundException {
        // Arrange
        Long nextOfKinRecordId = 1L;
        when(nextOfKinRecordRepository.findById(nextOfKinRecordId)).thenReturn(Optional.of(new NextOfKinRecord()));

        // Act
        String result = nextOfKinRecordService.deleteNextOfKinRecord(nextOfKinRecordId);

        // Assert
        assertEquals("Next of Kin Record 1 has been successfully deleted.", result);
        verify(nextOfKinRecordRepository).deleteById(nextOfKinRecordId);
    }

    @Test
    void testDeleteAllNextOfKinRecordsFromElectronicHealthRecord() throws ElectronicHealthRecordNotFoundException {
        // Arrange
        Long electronicHealthRecordId = 1L;
        ElectronicHealthRecord mockElectronicHealthRecord = new ElectronicHealthRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.of(mockElectronicHealthRecord));

        // Act
        ElectronicHealthRecord result = nextOfKinRecordService.deleteAllNextOfKinRecordsFromElectronicHealthRecord(electronicHealthRecordId);

        // Assert
        assertTrue(result.getListOfNextOfKinRecords().isEmpty());
        verify(electronicHealthRecordRepository).findById(electronicHealthRecordId);
        verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);
    }
}