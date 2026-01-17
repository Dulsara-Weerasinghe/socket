package com.example.websocket.controller;

import com.example.websocket.model.LoginChallenge;
import com.example.websocket.service.JwtService;
import com.example.websocket.service.LoginChallengeService;
import com.example.websocket.service.WsNotifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    private final JwtService jwt;
    private final LoginChallengeService challengeService;
    private final SimpMessagingTemplate messagingTemplate;
private final WsNotifier wsNotifier;

    // simulate "user already logged in"
    private static boolean alreadyLoggedIn = false;

    public TestController(JwtService jwt, LoginChallengeService challengeService, SimpMessagingTemplate messagingTemplate, WsNotifier wsNotifier) {
        this.jwt = jwt;
        this.challengeService = challengeService;
        this.messagingTemplate = messagingTemplate;
        this.wsNotifier = wsNotifier;
    }


    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req,
                                     @RequestHeader(value = "User-Agent", required = false) String ua) {
        System.out.println("WS CONNECT token = " + req);
        String username = req.get("username");

        System.out.println("usernnnnnn"+ username);
        if (!alreadyLoggedIn) {
            alreadyLoggedIn = true;
            return Map.of(
                    "status", "OK",
                    "accessToken", jwt.generate(username)
            );

        }
            // 🚨 CONCURRENT LOGIN FOUND
           var  challenge = challengeService.createChallenge(
                    username,
                    "127.0.0.1",
                    ua
            );

        System.out.println("challenge passing to notifier---> " + challenge.getChallengeId() + " " + challenge.getIp() + challenge.getStatus() + challenge.getUsername());
            // 🔔 THIS IS WHERE WE INFORM DEVICE A
            wsNotifier.notifyUser(
                    username,
                    challenge
            );


        System.out.println("Challenge----> " + challenge);
        return Map.of(
                "status", "PENDING_APPROVAL",
                "challengeId",challenge.getChallengeId()
        );
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
