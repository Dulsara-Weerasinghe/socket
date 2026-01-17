package com.example.websocket.dto;

import lombok.Data;

@Data
public class LoginAttemptResponse {

    private String status;       // "OK" or "PENDING_APPROVAL"
    private String accessToken;    // present if OK
    private String challengeId;     // present if PENDING_APPROVAL
    private long expiresInSeconds;

    public LoginAttemptResponse(String status, String accessToken, String challengeId, long expiresInSeconds) {
        this.status = status;
        this.accessToken = accessToken;
        this.challengeId = challengeId;
        this.expiresInSeconds = expiresInSeconds;
    }
}
