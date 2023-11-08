package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.dto.StaffChatDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
import com.Heart2Hub.Heart2Hub_Backend.entity.Conversation;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.exception.UnableToCreateConversationException;
import com.Heart2Hub.Heart2Hub_Backend.mapper.StaffChatMapper;
import com.Heart2Hub.Heart2Hub_Backend.repository.ConversationRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConversationService {

  private final ChatMessageService chatMessageService;
  private final ConversationRepository conversationRepository;
  private final StaffService staffService;
  private final StaffChatMapper staffChatMapper;

  public ConversationService(ChatMessageService chatMessageService,
      ConversationRepository conversationRepository,
      StaffService staffService, StaffChatMapper staffChatMapper) {
    this.chatMessageService = chatMessageService;
    this.conversationRepository = conversationRepository;
    this.staffChatMapper = staffChatMapper;
    this.staffService = staffService;
  }

  public Conversation createNewStaffConversation(Long staffId1, Long staffId2) {

    if (staffId1.equals(staffId2)) {
      throw new UnableToCreateConversationException("Cannot create a conversation with self");
    }

    List<Conversation> convo1 = conversationRepository.findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(
        staffId1, staffId2);
    List<Conversation> convo2 = conversationRepository.findFirstByFirstStaff_StaffIdAndSecondStaff_StaffId(
        staffId2, staffId1);

    if (convo1.size() > 0 || convo2.size() > 0) {
      throw new UnableToCreateConversationException("Conversation already exist");
    }

    Staff staff1 = staffService.getStaffById(staffId1);
    Staff staff2 = staffService.getStaffById(staffId2);

    Conversation conversation = new Conversation(null, staff1, staff2);

    return conversationRepository.save(conversation);
  }

  public ChatMessage addChatMessage(Long conversationId, ChatMessage chatMessage) {
    Optional<Conversation> convo = conversationRepository.findById(conversationId);

    if (convo.isPresent()) {
      Conversation conversation = convo.get();
      chatMessageService.saveChatMessage(chatMessage);
      conversation.getListOfChatMessages().add(chatMessage);

      return chatMessage;
    } else {
      throw new UnableToCreateConversationException("Conversation does not exist");
    }
  }

  public HashMap<Long,Conversation> getStaffConversations(Long staffId) {

    List<Conversation> listOfAllConvos = new ArrayList<>(
        conversationRepository.findAllByFirstStaff_StaffId(staffId));
    listOfAllConvos.addAll(conversationRepository.findAllBySecondStaff_StaffId(staffId));


//    store a map of other staffId, in a map of staffDTO,conversation
//    HashMap<Long, HashMap<StaffChatDTO,Conversation>> result = new HashMap<>();
//
//    for (Conversation convo : listOfAllConvos) {
//      Long otherStaffId = convo.getFirstStaff().getStaffId().equals(staffId) ? convo.getSecondStaff().getStaffId() : convo.getFirstStaff().getStaffId();
//      Staff staff = staffService.getStaffById(otherStaffId);
//      StaffChatDTO dto = staffChatMapper.toDTO(staff);
//      HashMap<StaffChatDTO, Conversation> map = new HashMap<>();
//      map.put(dto, convo);
//      result.put(otherStaffId,map);
//    }

    HashMap<Long,Conversation> result = new HashMap<>();
    for (Conversation convo : listOfAllConvos) {
      Long otherStaffId = convo.getFirstStaff().getStaffId().equals(staffId) ? convo.getSecondStaff().getStaffId() : convo.getFirstStaff().getStaffId();
      result.put(otherStaffId,convo);
    }
    return result;
  }

  public StaffChatDTO getStaffChatDTO(Long staffId) {
    return staffChatMapper.toDTO(staffService.getStaffById(staffId));
  }
}
