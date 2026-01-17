package com.example.websocket.controller;

import com.example.websocket.model.ChatMessage;
import com.example.websocket.service.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SessionManager sessionManager;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, 
                        SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();
        String newSessionId = headerAccessor.getSessionId();
        
        log.info("User {} attempting to join with session {}", username, newSessionId);
        
        // Check if user already has an active session
        if (sessionManager.isUserAlreadyConnected(username)) {
            String existingSessionId = sessionManager.getExistingSessionId(username);
            log.info("User {} already connected with session {}. New session: {}", 
                     username, existingSessionId, newSessionId);
            
            // Send duplicate login notification to the existing session
            ChatMessage duplicateNotification = new ChatMessage();
            duplicateNotification.setType(ChatMessage.MessageType.DUPLICATE_LOGIN);
            duplicateNotification.setSender(username);
            duplicateNotification.setContent("Another session detected");
            duplicateNotification.setSessionId(newSessionId);
            
            // Send to specific session using session ID
            messagingTemplate.convertAndSendToUser(
                existingSessionId, 
                "/queue/notification", 
                duplicateNotification,
                createHeaders(existingSessionId)
            );
            
            log.info("Sent duplicate login notification to session {}", existingSessionId);
        } else {
            // No existing session, proceed with normal join
            sessionManager.registerSession(username, newSessionId);
            
            ChatMessage joinMessage = new ChatMessage();
            joinMessage.setType(ChatMessage.MessageType.JOIN);
            joinMessage.setSender(username);
            
            messagingTemplate.convertAndSend("/topic/public", joinMessage);
        }
    }

    @MessageMapping("/chat.sessionChoice")
    public void handleSessionChoice(@Payload ChatMessage message,
                                    SimpMessageHeaderAccessor headerAccessor) {
        String username = message.getSender();
        String currentSessionId = headerAccessor.getSessionId();
        String newSessionId = message.getSessionId();
        String choice = message.getContent(); // "continue" or "keep"
        
        log.info("User {} made choice: {} (current: {}, new: {})", 
                 username, choice, currentSessionId, newSessionId);
        
        if ("continue".equals(choice)) {
            // User chose to continue with new session - logout current session
            log.info("Logging out current session {}", currentSessionId);
            
            ChatMessage forceLogout = new ChatMessage();
            forceLogout.setType(ChatMessage.MessageType.FORCE_LOGOUT);
            forceLogout.setSender(username);
            forceLogout.setContent("Logged out due to new session");
            
            messagingTemplate.convertAndSendToUser(
                currentSessionId,
                "/queue/notification",
                forceLogout,
                createHeaders(currentSessionId)
            );
            
            // Register the new session
            sessionManager.registerSession(username, newSessionId);
            
            // Send join notification for new session
            ChatMessage joinMessage = new ChatMessage();
            joinMessage.setType(ChatMessage.MessageType.JOIN);
            joinMessage.setSender(username);
            messagingTemplate.convertAndSend("/topic/public", joinMessage);
            
        } else if ("keep".equals(choice)) {
            // User chose to keep current session - logout new session
            log.info("Logging out new session {}", newSessionId);
            
            ChatMessage forceLogout = new ChatMessage();
            forceLogout.setType(ChatMessage.MessageType.FORCE_LOGOUT);
            forceLogout.setSender(username);
            forceLogout.setContent("Current session kept");
            
            messagingTemplate.convertAndSendToUser(
                newSessionId,
                "/queue/notification",
                forceLogout,
                createHeaders(newSessionId)
            );
        }
    }

    private org.springframework.messaging.MessageHeaders createHeaders(String sessionId) {
        org.springframework.messaging.simp.SimpMessageHeaderAccessor headerAccessor = 
            org.springframework.messaging.simp.SimpMessageHeaderAccessor.create(
                org.springframework.messaging.simp.SimpMessageType.MESSAGE
            );
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
