package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.StaffChatDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
import com.Heart2Hub.Heart2Hub_Backend.entity.Conversation;
import com.Heart2Hub.Heart2Hub_Backend.enumeration.MessageTypeEnum;
import com.Heart2Hub.Heart2Hub_Backend.service.ConversationService;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/conversation")
//@CrossOrigin( origins =  "http://localhost:3000" , allowCredentials = "false" ,  maxAge = 3600)
public class ConversationController {

  private final ConversationService conversationService;
  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;

  }

  // Endpoint for creating a new conversation
  @PostMapping("/createStaffConversation")
  public ResponseEntity<Conversation> createConversation(@RequestParam("staffId1") Long staffId1,
      @RequestParam("staffId2") Long staffId2) {
    Conversation createdConversation = conversationService.createNewStaffConversation(staffId1,
        staffId2);
    return ResponseEntity.status(HttpStatus.OK).body(createdConversation);
  }

  // Endpoint for creating a new conversation
  @GetMapping("/getStaffConversations")
  public ResponseEntity<HashMap<Long,Conversation>> getStaffConversations(@RequestParam("staffId") Long staffId) {
    return ResponseEntity.status(HttpStatus.OK).body(conversationService.getStaffConversations(staffId));
  }

  @GetMapping("/getStaffChatDTO")
  public ResponseEntity<StaffChatDTO> getStaffChatDTO(@RequestParam("staffId") Long staffId) {
    return ResponseEntity.status(HttpStatus.OK).body(conversationService.getStaffChatDTO(staffId));
  }

  @PostMapping("/addChatMessage")
  public ResponseEntity<String> addChatMessage(@RequestParam("conversationId") Long conversationId,
                                         @RequestParam("senderId") Long senderId,
                                         @RequestParam("content") String content) {
    ChatMessage chatMessage = new ChatMessage(content, MessageTypeEnum.CHAT, senderId);
    conversationService.addChatMessage(conversationId, chatMessage);
    return ResponseEntity.status(HttpStatus.OK).body("yay");
  }

  @PostMapping("/createPatientConversation")
  public ResponseEntity<Conversation> createPatientConversation(@RequestParam("patientId1") Long patientId1,
                                                         @RequestParam("staffId1") Long staffId1) {
    Conversation createdConversation = conversationService.createNewPatientConversation(patientId1,
            staffId1);
    return ResponseEntity.status(HttpStatus.OK).body(createdConversation);
  }

  @GetMapping("/getPatientConversation")
  public ResponseEntity<HashMap<Long,Conversation>> getPatientConversations(@RequestParam("patientId") Long patientId) {
    return ResponseEntity.status(HttpStatus.OK).body(conversationService.getPatientConversation(patientId));
  }

//  // Endpoint for sending a chat message within a conversation
//  @MessageMapping("/chat.sendPrivateMessage/{conversationId}")
//  public void sendChatMessage(@Payload ChatMessage chatMessage,
//      @DestinationVariable Long conversationId) {
//
//    System.out.println("SEND CHAT MESSAGE CALLED");
//    // Handle message persistence and sending
//    ChatMessage savedChatMessage = conversationService.addChatMessage(conversationId, chatMessage);
//    messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, savedChatMessage);
//  }

  // Other endpoints for retrieving conversation details, message history, etc.
}