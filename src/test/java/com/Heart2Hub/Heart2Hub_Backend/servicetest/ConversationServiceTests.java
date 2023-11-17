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
import junit.runner.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
class ConversationServiceTests {

    @InjectMocks
    private ConversationService conversationService;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        System.out.println("JUnit version is: " + Version.id());
    }

    @Test
    void testCreateNewStaffConversation() {
        //Arrange
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

        //Act
        Conversation result = conversationService.createNewStaffConversation(staffId1, staffId2);

        //Assert
        assertNotNull(result);
    }

    @Test
    void testCreateNewStaffConversation_ConversationExistsFailure() {
        //Arrange
        Long staffId1 = 1L;
        Long staffId2 = 2L;

        when(conversationRepository.findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(
            staffId1, staffId2)).thenReturn(List.of(new Conversation()));

        //Act
        //Assert
        assertThrows(UnableToCreateConversationException.class,
            () -> conversationService.createNewStaffConversation(staffId1, staffId2));

        verify(conversationRepository,times(2)).findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(anyLong(), anyLong());
        verifyNoMoreInteractions(staffService, conversationRepository);
    }

    @Test
    void testCreateNewStaffConversationSelf_UnableToCreateConversationException() {
        //Arrange
        Long staffId1 = 1L;

        //Act
        //Assert
        assertThrows(UnableToCreateConversationException.class,
            () -> conversationService.createNewStaffConversation(staffId1, staffId1));
        // Verify
        verifyNoInteractions(staffService, conversationRepository);
    }


    @Test
    void testCreateNewStaffConversation_StaffNotFound() {
        // Arrange
        Long staffId1 = 1L;
        Long staffId2 = 2L;

        when(staffService.getStaffById(anyLong())).thenAnswer(invocation -> {
            Long staffId = invocation.getArgument(0);
            if (staffId.equals(staffId1)) {
                throw new StaffNotFoundException("Username Does Not Exist");
            } else {
                return null;
            }
        });

        // Act and Assert
        assertThrows(StaffNotFoundException.class,
            () -> conversationService.createNewStaffConversation(staffId1, staffId2));

        // Verify
        verify(conversationRepository, times(2)).findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(anyLong(),anyLong());
        verify(staffService, times(1)).getStaffById(staffId1);
        verifyNoMoreInteractions(staffService, conversationRepository);
    }



    @Test
    void testAddChatMessage() {
        //Arrange
        Long conversationId = 1L;
        ChatMessage chatMessage = new ChatMessage();
        Conversation conversation = new Conversation();
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(chatMessageService.saveChatMessage(chatMessage)).thenReturn(chatMessage);

        //Act
        ChatMessage result = conversationService.addChatMessage(conversationId, chatMessage);

        //Assert
        assertNotNull(result);
        assertTrue(conversation.getListOfChatMessages().contains(chatMessage));
    }

    @Test
    void testAddChatMessage_IdNotFoundFailure() {
        //Arrange
        Long conversationId = 1L;
        ChatMessage chatMessage = new ChatMessage();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        //Act
        //Assert
        assertThrows(UnableToCreateConversationException.class,
                () -> conversationService.addChatMessage(conversationId, chatMessage));
    }

    @Test
    void testGetStaffConversations() {
        //Arrange
        Long staffId = 1L;
        Staff staff1 = new Staff();
        staff1.setStaffId(1L);
        Staff staff2 = new Staff();
        staff2.setStaffId(2L);
        Conversation convo = new Conversation();
        convo.setFirstStaff(staff1);
        convo.setSecondStaff(staff2);
        convo.setPatient(null);

        List<Conversation> listOfAllConvos = new ArrayList<>();
        List<Conversation> listOfAllConvos2 = new ArrayList<>();
        listOfAllConvos.add(convo);
        when(conversationRepository.findAllByFirstStaff_StaffId(staffId)).thenReturn(listOfAllConvos);
        when(conversationRepository.findAllBySecondStaff_StaffId(staffId)).thenReturn(listOfAllConvos2);

        //Act
        HashMap<Long, Conversation> result = conversationService.getStaffConversations(staffId);

        //Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetStaffChatDTO() {
        //Arrange
        Long staffId = 1L;
        Staff staff = new Staff();
        when(staffService.getStaffById(staffId)).thenReturn(staff);
        when(staffChatMapper.toDTO(staff)).thenReturn(new StaffChatDTO());

        //Act
        StaffChatDTO result = conversationService.getStaffChatDTO(staffId);

        //Assert
        assertNotNull(result);
    }

    @Test
    void testGetStaffChatDTO_StaffNotFoundException() {
        //Arrange
        Long staffId = 1L;
        when(staffService.getStaffById(staffId)).thenThrow(new StaffNotFoundException("Staff not found."));

        //Act
        //Assert
        assertThrows(StaffNotFoundException.class,
                () -> conversationService.getStaffChatDTO(staffId));

        verify(staffService).getStaffById(staffId);
        verifyNoMoreInteractions(staffService, staffChatMapper);
    }


    @Test
    void testCreateNewPatientConversation() {
        //Arrange
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

        //Act
        Conversation result = conversationService.createNewPatientConversation(patientId, staffId);

        //Assert
        assertNotNull(result);
    }

    @Test
    void testCreateNewPatientConversation_ConversationExistFailure() {
        //Arrange
        Long patientId = 1L;
        Long staffId = 2L;

        when(conversationRepository.findFirstByPatient_PatientIdAndFirstStaff_StaffId(
            patientId, staffId)).thenReturn(List.of(new Conversation()));

        //Act
        //Assert
        assertThrows(UnableToCreateConversationException.class,
            () -> conversationService.createNewPatientConversation(patientId, staffId));
    }

    @Test
    void testCreateNewPatientConversation_StaffNotFoundException() {
        Long patientId = 1L;
        Long staffId = 2L;
        Staff staff = new Staff();
        staff.setStaffId(staffId);

        when(conversationRepository.findFirstByPatient_PatientIdAndFirstStaff_StaffId(
            patientId, staffId)).thenReturn(new ArrayList<>());
        when(conversationRepository.findFirstByFirstStaff_StaffIdAndPatient_PatientId(
            staffId, patientId)).thenReturn(new ArrayList<>());
        when(staffService.getStaffById(staffId)).thenThrow(new StaffNotFoundException("Username Does Not Exist."));
//        when(patientService.getPatientById(patientId)).thenThrow(new PatientNotFoundException("Patient not found."));

        assertThrows(StaffNotFoundException.class,
            () -> conversationService.createNewPatientConversation(patientId, staffId));

        // Verify
        verify(conversationRepository,times(1)).findFirstByPatient_PatientIdAndFirstStaff_StaffId(anyLong(),anyLong());
        verify(conversationRepository,times(1)).findFirstByFirstStaff_StaffIdAndPatient_PatientId(anyLong(),anyLong());
//        verify(patientService).getPatientById(anyLong());
        verify(staffService).getStaffById(anyLong());
        verifyNoMoreInteractions(patientService, staffService, conversationRepository);
    }

    @Test
    void testCreateNewPatientConversation_PatientNotFoundException() {
        Long patientId = 1L;
        Long staffId = 2L;
        Staff staff = new Staff();
        staff.setStaffId(staffId);

        when(conversationRepository.findFirstByPatient_PatientIdAndFirstStaff_StaffId(
            patientId, staffId)).thenReturn(new ArrayList<>());
        when(conversationRepository.findFirstByFirstStaff_StaffIdAndPatient_PatientId(
            staffId, patientId)).thenReturn(new ArrayList<>());
        when(staffService.getStaffById(staffId)).thenReturn(staff);
        when(patientService.getPatientById(patientId)).thenThrow(new PatientNotFoundException("Patient not found."));

        assertThrows(PatientNotFoundException.class,
                () -> conversationService.createNewPatientConversation(patientId, staffId));

        // Verify
        verify(conversationRepository,times(1)).findFirstByPatient_PatientIdAndFirstStaff_StaffId(anyLong(),anyLong());
        verify(conversationRepository,times(1)).findFirstByFirstStaff_StaffIdAndPatient_PatientId(anyLong(),anyLong());
        verify(patientService).getPatientById(anyLong());
        verify(staffService).getStaffById(anyLong());
        verifyNoMoreInteractions(patientService, staffService, conversationRepository);
    }

    @Test
    void testGetPatientConversation() {
        //Arrange
        Long patientId = 1L;
        Long staffId = 2L;
        Patient patient = new Patient();
        patient.setPatientId(patientId);
        Staff staff = new Staff();
        staff.setStaffId(staffId);
        Conversation convo = new Conversation();
        convo.setPatient(patient);
        convo.setFirstStaff(staff);
        ArrayList<Conversation> list = new ArrayList<>();
        list.add(convo);
        when(conversationRepository.findAllByPatient_PatientId(anyLong())).thenReturn(list);

        //Act
        HashMap<Long,Conversation> result = conversationService.getPatientConversation(patientId);

        //Assert
        assertNotNull(result);
        assertEquals(result.get(staffId), convo);
    }
}
