//package com.Heart2Hub.Heart2Hub_Backend.controller;
//
//import com.Heart2Hub.Heart2Hub_Backend.dto.StaffChatDTO;
//import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
//import com.Heart2Hub.Heart2Hub_Backend.enumeration.MessageTypeEnum;
//import com.Heart2Hub.Heart2Hub_Backend.service.ChatMessageService;
//import java.util.ArrayList;
//import java.util.HashMap;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/chatMessage")
//@Component
//@RequiredArgsConstructor
//public class ChatMessageController {
//  private final SimpMessageSendingOperations messagingTemplate;
//  private final ChatMessageService chatMessageService;
//
////  @MessageMapping("/chat.sendMessage")
////  @SendTo("/topic/public")
////  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
////    return chatMessage;
////  }
////
////  @MessageMapping("chat.addUser")
////  @SendTo("topic/public")
////  public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
////    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
////    return chatMessage;
////  }
////
////  @MessageMapping("/chat.sendPrivateMessage")
////  public void sendPrivateMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
////    String recipient = chatMessage.getRecipient();
////    messagingTemplate.convertAndSendToUser(recipient, "/private", chatMessage);
////  }
////
////  @MessageMapping("/chat.sendPatientMessage")
////  public void sendPatientMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
////    Long patientId = chatMessage.getPatientId();
////    // Send the message to the patient's unique destination
////    messagingTemplate.convertAndSend("/patient/" + patientId, chatMessage);
////  }
//
////  @MessageMapping("/chat.sendMessage")
////  @SendTo("/topic/public")
////  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
////    chatMessage.setMessageTypeEnum(MessageTypeEnum.CHAT);
////    // Handle saving and sending the chat message
////    return chatMessage;
////  }
////
////  @MessageMapping("/chat.enter")
////  @SendTo("/topic/public")
////  public ChatMessage enterChat(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
////    chatMessage.setMessageTypeEnum(MessageTypeEnum.ENTER);
////    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
////    // Handle saving and sending the enter event
////    return chatMessage;
////  }
////
////  @MessageMapping("/chat.leave")
////  @SendTo("/topic/public")
////  public ChatMessage leaveChat(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
////    chatMessage.setMessageTypeEnum(MessageTypeEnum.LEAVE);
////    // Handle saving and sending the leave event
////    return chatMessage;
////  }
//
//  @MessageMapping("/chat.sendPrivateMessage")
//  @SendTo("topic/messages")
//  public void sendPrivateMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//    String recipient = headerAccessor.getDestination().substring("/user/".length());
//    chatMessage.setMessageTypeEnum(MessageTypeEnum.CHAT);
//    // Handle saving and sending the private chat message
//    messagingTemplate.convertAndSendToUser(recipient, "/private", chatMessage);
//  }
//
//  @MessageMapping("/chat.sendPatientMessage")
//  public void sendPatientMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
//
//    Long patientId = (Long) headerAccessor.getSessionAttributes().get("patientId");
//    Long senderStaffId = (Long) headerAccessor.getSessionAttributes().get("senderStaffId");
//
//    chatMessage.setMessageTypeEnum(MessageTypeEnum.CHAT);
//    //let service handle
////    chatMessage.setPatientId(patientId);
////    chatMessage.setSenderStaff(senderStaff);    // Handle saving and sending the patient-staff chat message
//    messagingTemplate.convertAndSend("/patient/" + patientId, chatMessage);
//  }
//
//  @GetMapping("/getStaffConversations")
//  public ResponseEntity<HashMap<StaffChatDTO, ArrayList<ChatMessage>>> getStaffConversations(
//      @RequestParam("loggedInStaffId") Long loggedInStaffId) {
//
//    return ResponseEntity.ok(chatMessageService.getStaffConversations(loggedInStaffId));
//  }
//}
