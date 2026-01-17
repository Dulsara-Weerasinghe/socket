package com.example.websocket.entity;

import com.example.websocket.model.LoginChallengeStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "login_challenge")
public class LoginChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginChallengeStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant decidedAt;

    // Attempt metadata (for Device A popup)
    private String ip;
    private String userAgent;

    // Who approved/denied (optional auditing)
    private String decidedByUserId;

    protected LoginChallenge() {}

    public LoginChallenge(String userId, Instant createdAt, Instant expiresAt, String ip, String userAgent) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.ip = ip;
        this.userAgent = userAgent;
        this.status = LoginChallengeStatus.PENDING;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public LoginChallengeStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getDecidedAt() { return decidedAt; }
    public String getIp() { return ip; }
    public String getUserAgent() { return userAgent; }

    public void expireIfNeeded(Instant now) {
        if (status == LoginChallengeStatus.PENDING && now.isAfter(expiresAt)) {
            status = LoginChallengeStatus.EXPIRED;
        }
    }

    public void approve(String decidedByUserId, Instant now) {
        status = LoginChallengeStatus.APPROVED;
        this.decidedByUserId = decidedByUserId;
        this.decidedAt = now;
    }

    public void deny(String decidedByUserId, Instant now) {
        status = LoginChallengeStatus.DENIED;
        this.decidedByUserId = decidedByUserId;
        this.decidedAt = now;
    }
}
