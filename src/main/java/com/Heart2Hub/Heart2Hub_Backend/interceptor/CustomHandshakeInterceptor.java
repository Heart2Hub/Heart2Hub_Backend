//package com.Heart2Hub.Heart2Hub_Backend.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.http.server.ServletServerHttpResponse;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
//import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
//
//import java.util.Map;
//
//@Slf4j
//public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
//
//  @Override
//  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//    log.info("interceptor is working");
//    log.info(String.valueOf(request));
//    System.out.println(request);
//    if (request instanceof ServletServerHttpRequest) {
//      HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
//      HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
//
//      // Print the origin for debugging
//      log.info("WebSocket handshake intercepted.");
//      String origin = servletRequest.getHeader("Origin");
//      log.info("WebSocket connection from origin: " + origin);
//
//      // Allow requests from your frontend origin
//      servletResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//
//      // Additional CORS headers as needed
//      servletResponse.setHeader("Access-Control-Allow-Credentials", "true");
//
//      // You may configure other headers as needed
//
//      if ("OPTIONS".equalsIgnoreCase(servletRequest.getMethod())) {
//        servletResponse.setStatus(HttpServletResponse.SC_OK);
//        return false;
//      }
//    }
//    return super.beforeHandshake(request, response, wsHandler, attributes);
//  }
//}
