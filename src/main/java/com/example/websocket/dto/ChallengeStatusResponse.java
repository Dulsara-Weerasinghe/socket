package com.example.websocket.dto;

import lombok.Data;

@Data
public class ChallengeStatusResponse {

    private String challengeId;
    private String status;

    public ChallengeStatusResponse(String challengeId, String status) {
        this.challengeId = challengeId;
        this.status = status;
    }
}
