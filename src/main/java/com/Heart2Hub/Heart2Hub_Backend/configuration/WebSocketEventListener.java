//package com.Heart2Hub.Heart2Hub_Backend.configuration;
//
//import com.Heart2Hub.Heart2Hub_Backend.entity.ChatMessage;
//import com.Heart2Hub.Heart2Hub_Backend.enumeration.MessageTypeEnum;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class WebSocketEventListener {
//
//  private final SimpMessageSendingOperations messageTemplate;
//

//  @EventListener
//  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//    String username = (String) headerAccessor.getSessionAttributes().get("username");
//
//    if (username != null) {
//      log.info("User disconnected: {}", username);
//      //user left the chat
//
//      ChatMessage chatMessage = new ChatMessage();
////      var chatMessage = ChatMessage.builder().messageTypeEnum(MessageTypeEnum.LEAVE).sender(username).build();
//      messageTemplate.convertAndSend("/topic/public", chatMessage);
//    }
//  }
//}
