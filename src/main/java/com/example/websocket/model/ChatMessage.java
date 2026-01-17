package com.example.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    
    private String sender;
    private String content;
    private MessageType type;
    private String sessionId;  // To identify specific sessions
    
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        DUPLICATE_LOGIN,      // Notify existing session of duplicate login
        FORCE_LOGOUT          // Force a session to logout
    }
}
