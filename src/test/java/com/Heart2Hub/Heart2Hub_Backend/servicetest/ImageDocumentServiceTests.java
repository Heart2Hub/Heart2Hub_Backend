package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateImageDocumentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ImageDocumentRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.ImageDocumentService;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
class ImageDocumentServiceTests {

    @InjectMocks
    private ImageDocumentService imageDocumentService;
    @Mock
    private ImageDocumentRepository imageDocumentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

    @Test
    void testCreateImageDocument() throws UnableToCreateImageDocumentException {
        ImageDocument imageDocument = new ImageDocument("Id1.png", LocalDateTime.of(2023, 11, 11, 12, 0, 0));

        // Mocking the repository to return the same image document
        when(imageDocumentRepository.save(imageDocument)).thenReturn(imageDocument);

        // Performing the test
        ImageDocument result = imageDocumentService.createImageDocument(imageDocument);

        assertNotNull(result);
        assertEquals(imageDocument, result);
    }

    @Test
    void testCreateImageDocument_NullImageLinkFailure() {
        ImageDocument imageDocument = new ImageDocument("null", LocalDateTime.of(2023, 11, 11, 12, 0, 0));

        // Mocking the repository to throw an exception
        when(imageDocumentRepository.save(imageDocument)).thenThrow(new RuntimeException("Database error"));

        // Performing the test
        assertThrows(UnableToCreateImageDocumentException.class, () -> imageDocumentService.createImageDocument(imageDocument));
    }
}
