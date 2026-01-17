package com.example.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginAlertPayload {

    
    private String ip;
    private String userAgent;



    private  String challengeId;

    private Instant createdAt;
    private Instant expiresAt;



}
