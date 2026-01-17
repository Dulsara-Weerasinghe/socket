package com.example.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data

@NoArgsConstructor
public class LoginAlertMessage {
    private String challengeId;
    private String ip;
    private String userAgent;
    private Instant createdAt;
    private Instant expiresAt;


    public LoginAlertMessage(String challengeId, String ip, String userAgent, Instant createdAt, Instant expiresAt) {
        this.challengeId = challengeId;
        this.ip = ip;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
