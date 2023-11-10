package com.Heart2Hub.Heart2Hub_Backend.configuration;

//import com.Heart2Hub.Heart2Hub_Backend.interceptor.CustomHandshakeInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//@EnableWebSocket
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
////  @Override
////  public void registerStompEndpoints(StompEndpointRegistry registry) {
//////    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
////    registry.addEndpoint("/ws").setAllowedOriginPatterns("http://localhost:3000").withSockJS()
////        .setSuppressCors(true);
////  }
//
////  @Override
////  public void configureMessageBroker(MessageBrokerRegistry registry) {
////    registry.setApplicationDestinationPrefixes("/app");
////    registry.setUserDestinationPrefix("/user"); // Set the staff destination prefix
////  }
//
//  @Override
//  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000","localhost:3000")
//        .addInterceptors(new CustomHandshakeInterceptor())
//        .withSockJS().setSuppressCors(true);
//  }
//
//  @Override
//  public void configureMessageBroker(MessageBrokerRegistry config) {
//    config.setApplicationDestinationPrefixes("/app");
//    config.enableSimpleBroker("/topic", "/user", "/private");
//  }
//
////  @MessageMapping("/ws")
////  public void handleIncomingMessage(String message) {
////    // Log the received message or perform processing
////    System.out.println("Received message: " + message);
////  }
//
////  @Bean
////  WebMvcConfigurer corsConfig() {
////    return new WebMvcConfigurer() {
////
////      @Override
////      public void addCorsMappings(CorsRegistry registry) {
////        registry.addMapping("/ws/**")
////            .allowedMethods("GET", "POST", "PUT", "DELETE")
////            .allowedHeaders("*")
////            .allowedOrigins("http://localhost:3000");
////      }
////    };
////  }
//
//  //for presence checking
////  @Override
////  public void configureClientInboundChannel(ChannelRegistration registration) {
////    registration.interceptors(staffInterceptor());
////  }
//
////  @Bean
////  public StaffPresenceEventListener staffInterceptor() {
////    return new StaffPresenceEventListener();
////  }
//}

//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//  @Override
//  public void configureMessageBroker(MessageBrokerRegistry config) {
//    config.setApplicationDestinationPrefixes("/app");
//    config.enableSimpleBroker("/chatroom", "/user");
//    config.setUserDestinationPrefix("/user");
//  }
//
//  @Override
//  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
//  }
//}