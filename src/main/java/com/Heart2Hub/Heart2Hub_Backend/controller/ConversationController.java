package com.Heart2Hub.Heart2Hub_Backend.controller;

import com.Heart2Hub.Heart2Hub_Backend.dto.StaffChatDTO;
import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
import com.Heart2Hub.Heart2Hub_Backend.entity.Conversation;
import com.Heart2Hub.Heart2Hub_Backend.service.ConversationService;
import java.util.HashMap;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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