package com.Heart2Hub.Heart2Hub_Backend.service;

import com.Heart2Hub.Heart2Hub_Backend.dto.StaffChatDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
import com.Heart2Hub.Heart2Hub_Backend.entity.Staff;
import com.Heart2Hub.Heart2Hub_Backend.mapper.StaffChatMapper;
import com.Heart2Hub.Heart2Hub_Backend.repository.ChatMessageRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatMessageService {

  private final ChatMessageRepository chatMessageRepository;

  private final StaffChatMapper staffChatMapper;

  public ChatMessageService(ChatMessageRepository chatMessageRepository,
      StaffChatMapper staffChatMapper) {
    this.chatMessageRepository = chatMessageRepository;
    this.staffChatMapper = staffChatMapper;
  }

  public ChatMessage saveChatMessage(ChatMessage chatMessage) {
    return chatMessageRepository.save(chatMessage);
  }


  //retrieve conversations for a logged in staff
//  public HashMap<StaffChatDTO, ArrayList<ChatMessage>> getStaffConversations(Long loggedInStaffId) {
//
//    //get chat messages that have this logged in staff either as the sender or receiver, and the patient null
//    ArrayList<ChatMessage> listOfChatMessages = chatMessageRepository.findAllByReceiverStaff_StaffIdOrSenderStaff_StaffIdAndPatientNull(
//        loggedInStaffId, loggedInStaffId);
//
//    HashMap<StaffChatDTO, ArrayList<ChatMessage>> map = new HashMap<>();
//    System.out.println(listOfChatMessages.size());
//    System.out.println(listOfChatMessages.get(0));
//
//    //put into the conversations
//    //shld convert to staffdto
//    for (ChatMessage message : listOfChatMessages) {
//      if (!Objects.equals(message.getReceiverStaff().getStaffId(), loggedInStaffId)) {
//        Staff staff = message.getSenderStaff();
//        StaffChatDTO staffDTO = staffChatMapper.toDTO(staff);
//        if (map.get(staffDTO) == null) {
//          ArrayList<ChatMessage> list = new ArrayList<>();
//          list.add(message);
//          map.put(staffDTO, list);
//        } else {
//          map.get(staffDTO).add(message);
//        }
//
//      } else {
//        Staff staff = message.getReceiverStaff();
//        StaffChatDTO staffDTO = staffChatMapper.toDTO(staff);
//        if (map.get(staffDTO) == null) {
//          ArrayList<ChatMessage> list = new ArrayList<>();
//          list.add(message);
//          map.put(staffDTO, list);
//        } else {
//          map.get(staffDTO).add(message);
//        }
//      }
//    }
//
//    for (StaffChatDTO dto : map.keySet()) {
//      map.get(dto).sort(Comparator.comparing(ChatMessage::getTimestamp).reversed());
//    }
//
//    return map;
//  }
}
