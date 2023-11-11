package com.Heart2Hub.Heart2Hub_Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.entity.ImageDocument;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateImageDocumentException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ImageDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImageDocumentServiceTests {

    @Mock
    private ImageDocumentRepository imageDocumentRepository;

    @InjectMocks
    private ImageDocumentService imageDocumentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateImageDocument() throws UnableToCreateImageDocumentException {
        ImageDocument imageDocument = new ImageDocument();

        // Mocking the repository to return the same image document
        when(imageDocumentRepository.save(imageDocument)).thenReturn(imageDocument);

        // Performing the test
        ImageDocument result = imageDocumentService.createImageDocument(imageDocument);

        assertNotNull(result);
        assertEquals(imageDocument, result);
    }

    @Test
    void testCreateImageDocumentException() {
        ImageDocument imageDocument = new ImageDocument();

        // Mocking the repository to throw an exception
        when(imageDocumentRepository.save(imageDocument)).thenThrow(new RuntimeException("Database error"));

        // Performing the test
        assertThrows(UnableToCreateImageDocumentException.class, () -> imageDocumentService.createImageDocument(imageDocument));
    }
}
