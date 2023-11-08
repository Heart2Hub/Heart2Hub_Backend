package com.Heart2Hub.Heart2Hub_Backend.controller;
import com.Heart2Hub.Heart2Hub_Backend.dto.MessageDTO;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

  private final SimpMessagingTemplate simpMessagingTemplate;

  public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/message") //FE url must send /app/message
  @SendTo("/chatroom/public")
  public MessageDTO chat(@Payload MessageDTO message) {

    System.out.println("WORKING");
    System.out.println(message.getMessage());

    return message;
  }
  @MessageMapping("private-message")
  public MessageDTO receivePrivateMessage(@Payload MessageDTO message) {
    //in messageDTO shld store the receiver's ID or sth in order to receive private message
    //user will have to listen to /user/*the name currently*/private
    System.out.println("WORKING 2");
    System.out.println(message.getSenderStaffId());
    System.out.println(message.getReceiverStaffId());
    System.out.println(message.getStatus());
    System.out.println(message.getTimestamp());
    System.out.println(message.getMessage());

    simpMessagingTemplate.convertAndSendToUser(message.getReceiverStaffId().toString(), "/private", message);
    return message;
  }
}