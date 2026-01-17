package com.example.websocket.service;

import com.example.websocket.model.LoginChallenge;
import com.example.websocket.model.LoginStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginChallengeService {
    private final Map<String, LoginChallenge> challenges = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public LoginChallengeService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public LoginChallenge createChallenge(String username, String ip, String ua) {
        LoginChallenge c = new LoginChallenge(username, ip, ua);
        challenges.put(c.getChallengeId(), c);

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/login-alerts",
                c
        );

        return c;
    }

    public LoginChallenge get(String id) {
        return challenges.get(id);
    }

    public LoginChallenge respond(String id, LoginStatus status) {
        LoginChallenge c = challenges.get(id);
        if (c != null) c.setStatus(status);
        return c;
    }
}
