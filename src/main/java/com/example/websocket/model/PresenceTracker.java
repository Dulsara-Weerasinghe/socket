package com.example.websocket.model;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceTracker {
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

    public boolean isOnline(String userId) {
        return onlineUsers.contains(userId);
    }

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        var user = event.getUser();
        if (user != null) onlineUsers.add(user.getName());
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        var user = event.getUser();
        if (user != null) onlineUsers.remove(user.getName());
    }
}
