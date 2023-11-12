package com.Heart2Hub.Heart2Hub_Backend.servicetest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.Heart2Hub.Heart2Hub_Backend.dto.StaffChatDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
import com.Heart2Hub.Heart2Hub_Backend.entity.Conversation;
import com.Heart2Hub.Heart2Hub_Backend.entity.Patient;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.exception.PatientNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.StaffNotFoundException;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateConversationException;
import com.Heart2Hub.Heart2Hub_Backend.mapper.StaffChatMapper;
import com.Heart2Hub.Heart2Hub_Backend.repository.ConversationRepository;
import com.Heart2Hub.Heart2Hub_Backend.service.ChatMessageService;
import com.Heart2Hub.Heart2Hub_Backend.service.ConversationService;
import com.Heart2Hub.Heart2Hub_Backend.service.PatientService;
import com.Heart2Hub.Heart2Hub_Backend.service.StaffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ConversationServiceTests {

    @Mock
    private ChatMessageService chatMessageService;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private StaffService staffService;

    @Mock
    private StaffChatMapper staffChatMapper;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private ConversationService conversationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateNewStaffConversation() {
        Long staffId1 = 1L;
        Long staffId2 = 2L;

        Staff staff1 = new Staff();
        Staff staff2 = new Staff();

        when(staffService.getStaffById(staffId1)).thenReturn(staff1);
        when(staffService.getStaffById(staffId2)).thenReturn(staff2);

        when(conversationRepository.findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(
                staffId1, staffId2)).thenReturn(new ArrayList<>());

        when(conversationRepository.findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(
                staffId2, staffId1)).thenReturn(new ArrayList<>());

        when(conversationRepository.save(any())).thenReturn(new Conversation());

        Conversation result = conversationService.createNewStaffConversation(staffId1, staffId2);

        assertNotNull(result);
    }

    @Test
    void testCreateNewStaffConversation_StaffNotFound() {
        Long staffId1 = 1L;
        Long staffId2 = 2L;

        when(staffService.getStaffById(staffId1)).thenReturn(null);
        when(staffService.getStaffById(staffId2)).thenReturn(null);

        assertThrows(StaffNotFoundException.class,
                () -> conversationService.createNewStaffConversation(staffId1, staffId2));

        // Verify
        verify(staffService).getStaffById(staffId1);
        verify(staffService).getStaffById(staffId2);
        verifyNoMoreInteractions(staffService, conversationRepository);
    }


    @Test
    void testCreateNewStaffConversationSelf() {
        Long staffId1 = 1L;

        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.createNewStaffConversation(staffId1, staffId1));
    }

    @Test
    void testCreateNewStaffConversationSelf_UnableToCreateConversationException() {
        Long staffId1 = 1L;

        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.createNewStaffConversation(staffId1, staffId1));

        // Verify
        verifyNoInteractions(staffService, conversationRepository);
    }

    @Test
    void testCreateNewStaffConversationExisting() {
        Long staffId1 = 1L;
        Long staffId2 = 2L;

        when(conversationRepository.findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(
                staffId1, staffId2)).thenReturn(List.of(new Conversation()));

        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.createNewStaffConversation(staffId1, staffId2));
    }

    @Test
    void testCreateNewStaffConversationExisting_UnableToCreateConversationException() {
        Long staffId1 = 1L;
        Long staffId2 = 2L;

        when(conversationRepository.findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(
                staffId1, staffId2)).thenReturn(List.of(new Conversation()));

        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.createNewStaffConversation(staffId1, staffId2));

        // Verify
        verify(conversationRepository).findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(staffId1, staffId2);
        verifyNoMoreInteractions(staffService, conversationRepository);
    }

    @Test
    void testAddChatMessage() {
        Long conversationId = 1L;
        ChatMessage chatMessage = new ChatMessage();

        Conversation conversation = new Conversation();
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));

        when(chatMessageService.saveChatMessage(chatMessage)).thenReturn(chatMessage);

        ChatMessage result = conversationService.addChatMessage(conversationId, chatMessage);

        assertNotNull(result);
        assertTrue(conversation.getListOfChatMessages().contains(chatMessage));
    }

    @Test
    void testAddChatMessage_ConversationNotFound() {
        Long conversationId = 1L;
        ChatMessage chatMessage = new ChatMessage();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.addChatMessage(conversationId, chatMessage));
    }

    @Test
    void testGetStaffConversations() {
        Long staffId = 1L;

        List<Conversation> listOfAllConvos = new ArrayList<>();
        when(conversationRepository.findAllByFirstStaff_StaffId(staffId)).thenReturn(listOfAllConvos);
        when(conversationRepository.findAllBySecondStaff_StaffId(staffId)).thenReturn(listOfAllConvos);

        HashMap<Long, Conversation> result = conversationService.getStaffConversations(staffId);

        assertNotNull(result);
    }

    @Test
    void testGetStaffConversations_NoConversationsFound() {
        Long staffId = 1L;

        when(conversationRepository.findAllByFirstStaff_StaffId(staffId)).thenReturn(new ArrayList<>());
        when(conversationRepository.findAllBySecondStaff_StaffId(staffId)).thenReturn(new ArrayList<>());

        assertThrows(Exception.class,
                () -> conversationService.getStaffConversations(staffId));

        // Verify
        verify(conversationRepository).findAllByFirstStaff_StaffId(staffId);
        verify(conversationRepository).findAllBySecondStaff_StaffId(staffId);
        verifyNoMoreInteractions(conversationRepository);
    }


    @Test
    void testGetStaffChatDTO() {
        Long staffId = 1L;

        Staff staff = new Staff();
        when(staffService.getStaffById(staffId)).thenReturn(staff);

        when(staffChatMapper.toDTO(staff)).thenReturn(new StaffChatDTO());

        StaffChatDTO result = conversationService.getStaffChatDTO(staffId);

        assertNotNull(result);
    }

    @Test
    void testGetStaffChatDTO_StaffNotFoundException() {
        Long staffId = 1L;

        when(staffService.getStaffById(staffId)).thenThrow(new StaffNotFoundException("Staff not found."));

        assertThrows(StaffNotFoundException.class,
                () -> conversationService.getStaffChatDTO(staffId));

        // Verify
        verify(staffService).getStaffById(staffId);
        verifyNoMoreInteractions(staffService, staffChatMapper);
    }


    @Test
    void testCreateNewPatientConversation() {
        Long patientId = 1L;
        Long staffId = 2L;

        Patient patient = new Patient();
        Staff staff = new Staff();

        when(patientService.getPatientById(patientId)).thenReturn(patient);
        when(staffService.getStaffById(staffId)).thenReturn(staff);

        when(conversationRepository.findFirstByPatient_PatientIdAndFirstStaff_StaffId(
                patientId, staffId)).thenReturn(new ArrayList<>());

        when(conversationRepository.findFirstByFirstStaff_StaffIdAndPatient_PatientId(
                staffId, patientId)).thenReturn(new ArrayList<>());

        when(conversationRepository.save(any())).thenReturn(new Conversation());

        Conversation result = conversationService.createNewPatientConversation(patientId, staffId);

        assertNotNull(result);
    }

    @Test
    void testCreateNewPatientConversation_PatientNotFoundException() {
        Long patientId = 1L;
        Long staffId = 2L;

        when(patientService.getPatientById(patientId)).thenThrow(new PatientNotFoundException("Patient not found."));

        assertThrows(PatientNotFoundException.class,
                () -> conversationService.createNewPatientConversation(patientId, staffId));

        // Verify
        verify(patientService).getPatientById(patientId);
        verifyNoMoreInteractions(patientService, staffService, conversationRepository);
    }


    @Test
    void testCreateNewPatientConversationExisting() {
        Long patientId = 1L;
        Long staffId = 2L;

        when(conversationRepository.findFirstByPatient_PatientIdAndFirstStaff_StaffId(
                patientId, staffId)).thenReturn(List.of(new Conversation()));

        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.createNewPatientConversation(patientId, staffId));
    }

    @Test
    void testCreateNewPatientConversationExisting_UnableToCreateConversationException() {
        Long patientId = 1L;
        Long staffId = 2L;

        when(conversationRepository.findFirstByPatient_PatientIdAndFirstStaff_StaffId(
                patientId, staffId)).thenReturn(List.of(new Conversation()));

        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.createNewPatientConversation(patientId, staffId));

        // Verify
        verify(conversationRepository).findFirstByPatient_PatientIdAndFirstStaff_StaffId(patientId, staffId);
        verifyNoMoreInteractions(patientService, staffService, conversationRepository);
    }

}
