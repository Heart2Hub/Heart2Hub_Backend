package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import com.Heart2Hub.Heart2Hub_Backend.entity.ElectronicHealthRecord;
import com.Heart2Hub.Heart2Hub_Backend.entity.NextOfKinRecord;
import com.Heart2Hub.Heart2Hub_Backend.exception.ElectronicHealthRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.NextOfKinRecordNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateNextOfKinRecordException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ElectronicHealthRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.repository.NextOfKinRecordRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.NextOfKinRecordService;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
class NextOfKinRecordServiceTests {

    @InjectMocks
    private NextOfKinRecordService nextOfKinRecordService;
    @Mock
    private NextOfKinRecordRepository nextOfKinRecordRepository;
    @Mock
    private ElectronicHealthRecordRepository electronicHealthRecordRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

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
    void testCreateNextOfKinRecord_RecordNotFound() throws UnableToCreateNextOfKinRecordException {
        // Arrange
        Long electronicHealthRecordId = 1L;
        NextOfKinRecord newNextOfKinRecord = new NextOfKinRecord();
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UnableToCreateNextOfKinRecordException.class, () -> {
            nextOfKinRecordService.createNextOfKinRecord(electronicHealthRecordId, newNextOfKinRecord);
        });

        // Verify
//        verifyNoMoreInteractions(nextOfKinRecordRepository, electronicHealthRecordRepository);
        verify(electronicHealthRecordRepository).findById(any());
//        verify(electronicHealthRecordRepository).save(mockElectronicHealthRecord);
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
    void testGetNextOfKinRecordsByEHRId_RecordNotFound() throws ElectronicHealthRecordNotFoundException {
        // Arrange
        Long electronicHealthRecordId = 1L;
        when(electronicHealthRecordRepository.findById(electronicHealthRecordId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ElectronicHealthRecordNotFoundException.class, () -> {
            nextOfKinRecordService.getNextOfKinRecordsByEHRId(electronicHealthRecordId);
        });

        // Verify
        verifyNoMoreInteractions(nextOfKinRecordRepository);
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
    void testDeleteNextOfKinRecord_RecordNotFound() throws NextOfKinRecordNotFoundException {
        // Arrange
        Long nextOfKinRecordId = 1L;
        when(nextOfKinRecordRepository.findById(nextOfKinRecordId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NextOfKinRecordNotFoundException.class, () -> {
            nextOfKinRecordService.deleteNextOfKinRecord(nextOfKinRecordId);
        });

        // Verify
        verify(nextOfKinRecordRepository).findById(nextOfKinRecordId);

    }
}
