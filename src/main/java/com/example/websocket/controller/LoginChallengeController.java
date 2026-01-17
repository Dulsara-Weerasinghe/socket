package com.example.websocket.controller;

import com.example.websocket.model.LoginStatus;
import com.example.websocket.service.LoginChallengeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login-challenges")
public class LoginChallengeController {

    private final LoginChallengeService service;

    public LoginChallengeController(LoginChallengeService service) {
        this.service = service;
    }

    @GetMapping("/{id}/status")
    public Map<String, Object> status(@PathVariable String id) {
        System.out.println("check status");
        var c = service.get(id);
        System.out.println("c. stateus"+  c.getStatus());
        return Map.of("status", c.getStatus());
    }


    @PostMapping("/{id}/respond")
    public Map<String, Object> respond(@PathVariable String id,
                                       @RequestBody Map<String, String> req) {

        LoginStatus decision = LoginStatus.valueOf(req.get("decision"));
        var c = service.respond(id, decision);

        return Map.of("status", c.getStatus());
    }

}
