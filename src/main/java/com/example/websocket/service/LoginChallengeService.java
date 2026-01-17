package com.example.websocket.service;


import com.example.websocket.dto.LoginAlertMessage;
import com.example.websocket.entity.LoginChallenge;
import com.example.websocket.model.LoginChallengeStatus;
import com.example.websocket.repo.LoginChallengeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class LoginChallengeService {

    private final com.example.websocket.repo.LoginChallengeRepository repo;
    private final SimpMessagingTemplate messaging;
    private final long ttlSeconds;

    public LoginChallengeService(
            LoginChallengeRepository repo,
            SimpMessagingTemplate messaging,
            @Value("${app.challenge.ttlSeconds}") long ttlSeconds
    ) {
        this.repo = repo;
        this.messaging = messaging;
        this.ttlSeconds = ttlSeconds;
    }

    @Transactional
    public LoginChallenge createAndNotify(String userId, String ip, String userAgent) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        LoginChallenge ch = repo.save(new LoginChallenge(userId, now, exp, ip, userAgent));

        // Push to Device A: /user/{userId}/queue/login-alerts
        messaging.convertAndSendToUser(
                userId,
                "/queue/login-alerts",
                new LoginAlertMessage(ch.getId(), ip, userAgent, now, exp)
        );

        return ch;
    }

    @Transactional
    public Optional<LoginChallenge> getAndUpdateExpiry(String challengeId) {
        return repo.findById(challengeId).map(ch -> {
            ch.expireIfNeeded(Instant.now());
            return ch;
        });
    }

    @Transactional
    public LoginChallenge decide(String challengeId, String approverUserId, boolean approve) {
        LoginChallenge ch = repo.findById(challengeId).orElseThrow();
        ch.expireIfNeeded(Instant.now());

        if (!ch.getUserId().equals(approverUserId)) {
            throw new SecurityException("Not owner of challenge");
        }

        if (ch.getStatus() != LoginChallengeStatus.PENDING) {
            return ch; // already decided/expired
        }

        if (approve) ch.approve(approverUserId, Instant.now());
        else ch.deny(approverUserId, Instant.now());

        return ch;
    }
}
