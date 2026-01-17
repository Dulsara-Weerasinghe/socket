package com.example.websocket.controller;

import com.example.websocket.dto.LoginAttemptRequest;
import com.example.websocket.dto.LoginAttemptResponse;
import com.example.websocket.model.PresenceTracker;
import com.example.websocket.service.JwtService;
import com.example.websocket.service.LoginChallengeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    private final JwtService jwt;
    private final LoginChallengeService challengeService;
    private final SimpMessagingTemplate messagingTemplate;

    // simulate "user already logged in"
    private static boolean alreadyLoggedIn = false;



    private final PresenceTracker presence;

    public TestController(JwtService jwt, LoginChallengeService challengeService, SimpMessagingTemplate messagingTemplate, PresenceTracker presence) {
        this.jwt = jwt;
        this.challengeService = challengeService;
        this.messagingTemplate = messagingTemplate;

        this.presence = presence;
    }



    @PostMapping("/login")
    public LoginAttemptResponse login(@RequestBody LoginAttemptRequest req, HttpServletRequest servletReq) {
        // DEMO: authenticate username/password.
        // Replace with your real auth (DB, LDAP, etc).
        String userId = req.getUsername(); // treat username as userId
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("Invalid credentials");
        }

        String ip = servletReq.getRemoteAddr();
        String ua = servletReq.getHeader("User-Agent");

        // If user is online on Device A (web app), require approval
        if (presence.isOnline(userId)) {
            var ch = challengeService.createAndNotify(userId, ip, ua);
            return new LoginAttemptResponse(
                    "PENDING_APPROVAL",
                    null,
                    ch.getId(),
                    java.time.Duration.between(java.time.Instant.now(), ch.getExpiresAt()).getSeconds()
            );
        }

        // else normal login
        String token = jwt.mintAccessToken(userId);
        return new LoginAttemptResponse("OK", token, null, 0);
    }


    // Simulate concurrent login warning
    @PostMapping("/warn")
    public void sendWarning(@RequestParam String username) {

        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/security-warning",
                "You just logged in through Chrome Windows. Is this you?"
        );
    }
}
