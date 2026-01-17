package com.example.websocket.service;

import com.example.websocket.model.LoginAlertPayload;
import com.example.websocket.model.LoginChallenge;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WsNotifier {
    private final SimpMessagingTemplate messagingTemplate;

    public WsNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUser(String username, LoginChallenge challenge) {
        System.out.println("Notify starting.............." + username);
        System.out.println("Challange ----" + challenge.getChallengeId()+
                challenge.getIp() +  challenge.getUserAgent() +  challenge.getCreatedAt());
        LoginAlertPayload payload = new LoginAlertPayload(
                challenge.getChallengeId(),
                challenge.getIp(),
                challenge.getUserAgent(),
                challenge.getCreatedAt(),
                challenge.getExpiresAt()
        );


        System.out.println("Payload ........" + payload.getChallengeId());
        // 🔥 THIS LINE SENDS MESSAGE TO DEVICE A
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/login-alerts",
                payload
        );
    }
}
