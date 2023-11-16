package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.MessageTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateChatMessageException;
import com.Heart2Hub.Heart2Hub_Backend.repository.ChatMessageRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.ChatMessageService;
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
//@SpringBootTest
class ChatMessageServiceTests {

    @InjectMocks
    private ChatMessageService chatMessageService;
    @Mock
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

    @Test
    void testSaveChatMessage() {
        // Mocking
        ChatMessage chatMessage = new ChatMessage("Test Content", MessageTypeEnum.CHAT, 1L);
        when(chatMessageRepository.save(chatMessage)).thenReturn(chatMessage);

        // Test
        ChatMessage result = chatMessageService.saveChatMessage(chatMessage);
        System.out.println(result);
        assertNotNull(result);
        assertEquals(chatMessage, result);

        // Verify
        verify(chatMessageRepository, times(1)).save(chatMessage);
        verifyNoMoreInteractions(chatMessageRepository);
    }

    @Test
    void testSaveChatMessage_Failure() {
        ChatMessage chatMessage = new ChatMessage();
        doThrow(new DataIntegrityViolationException("Simulating ConstraintViolationException")).when(chatMessageRepository).save(chatMessage);

        // Test and verify exception
        assertThrows(UnableToCreateChatMessageException.class, () -> {
            chatMessageService.saveChatMessage(chatMessage);
        });

        // Verify
        verify(chatMessageRepository, times(1)).save(chatMessage);
        verifyNoMoreInteractions(chatMessageRepository);
    }

}
