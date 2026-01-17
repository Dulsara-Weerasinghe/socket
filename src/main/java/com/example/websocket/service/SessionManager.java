package com.example.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SessionManager {

    // Map username to sessionId
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    
    // Map sessionId to username
    private final Map<String, String> sessionUsers = new ConcurrentHashMap<>();

    /**
     * Check if username already has an active session
     */
    public boolean isUserAlreadyConnected(String username) {
        return userSessions.containsKey(username);
    }

    /**
     * Get the existing session ID for a username
     */
    public String getExistingSessionId(String username) {
        return userSessions.get(username);
    }

    /**
     * Register a new user session
     */
    public void registerSession(String username, String sessionId) {
        log.info("Registering session {} for user {}", sessionId, username);
        
        // Remove old session if exists
        String oldSessionId = userSessions.get(username);
        if (oldSessionId != null) {
            sessionUsers.remove(oldSessionId);
        }
        
        userSessions.put(username, sessionId);
        sessionUsers.put(sessionId, username);
    }

    /**
     * Remove session when user disconnects
     */
    public String removeSession(String sessionId) {
        log.info("Removing session {}", sessionId);
        String username = sessionUsers.remove(sessionId);
        if (username != null) {
            userSessions.remove(username);
        }
        return username;
    }

    /**
     * Get username by session ID
     */
    public String getUserBySessionId(String sessionId) {
        return sessionUsers.get(sessionId);
    }

    /**
     * Remove session by username
     */
    public void removeSessionByUsername(String username) {
        String sessionId = userSessions.remove(username);
        if (sessionId != null) {
            sessionUsers.remove(sessionId);
            log.info("Removed session {} for user {}", sessionId, username);
        }
    }
}
