package com.example.websocket.controller;

import com.example.websocket.dto.ChallengeDecisionRequest;
import com.example.websocket.dto.ChallengeStatusResponse;
import com.example.websocket.model.LoginChallengeStatus;
import com.example.websocket.service.JwtService;
import com.example.websocket.service.LoginChallengeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login-challenges")
public class ChallengeController {

    private final LoginChallengeService service;
    private final JwtService jwt;
    public ChallengeController(LoginChallengeService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    // Device B polls this
    @GetMapping("/{id}/status")
    public ChallengeStatusResponse status(@PathVariable String id) {
        var ch = service.getAndUpdateExpiry(id).orElseThrow();
        return new ChallengeStatusResponse(ch.getId(), ch.getStatus().name());
    }

    // Device A approves/denies this (must be authenticated)
    @PostMapping("/{id}/respond")
    public ChallengeStatusResponse respond(
            @PathVariable String id,
            @RequestBody ChallengeDecisionRequest req,
            HttpServletRequest request
    ) {
        String userId = extractUserId(request); // approver

        boolean approve = "APPROVE".equalsIgnoreCase(req.getDecision());
        boolean deny = "DENY".equalsIgnoreCase(req.getDecision());
        if (!approve && !deny) throw new IllegalArgumentException("decision must be APPROVE or DENY");

        var ch = service.decide(id, userId, approve);
        return new ChallengeStatusResponse(ch.getId(), ch.getStatus().name());
    }

    private String extractUserId(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) throw new SecurityException("Missing token");
        String token = header.substring("Bearer ".length());
        return jwt.verifyAndGetUserId(token);
    }

}
