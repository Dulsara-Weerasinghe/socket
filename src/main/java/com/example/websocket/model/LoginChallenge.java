package com.example.websocket.model;

import java.time.Instant;
import java.util.UUID;

public class LoginChallenge {

    private final String challengeId = UUID.randomUUID().toString();
    private final String username;
    private LoginStatus status = LoginStatus.PENDING;

    private final Instant createdAt = Instant.now();
    private final Instant expiresAt = createdAt.plusSeconds(120);

    private final String ip;
    private final String userAgent;

    public LoginChallenge(String username, String ip, String userAgent) {
        this.username = username;
        this.ip = ip;
        this.userAgent = userAgent;
    }

    // getters + setters
    public String getChallengeId() { return challengeId; }
    public String getUsername() { return username; }
    public LoginStatus getStatus() { return status; }
    public void setStatus(LoginStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }
}
