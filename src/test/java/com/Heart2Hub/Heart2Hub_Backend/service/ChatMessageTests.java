package com.Heart2Hub.Heart2Hub_Backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
import com.Heart2Hub.Heart2Hub_Backend.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatMessageTests {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveChatMessage() {
        ChatMessage chatMessage = new ChatMessage();

        // Mocking the repository to return the same chat message
        when(chatMessageRepository.save(chatMessage)).thenReturn(chatMessage);

        // Performing the test
        ChatMessage result = chatMessageService.saveChatMessage(chatMessage);

        assertNotNull(result);
        assertEquals(chatMessage, result);
    }

}
