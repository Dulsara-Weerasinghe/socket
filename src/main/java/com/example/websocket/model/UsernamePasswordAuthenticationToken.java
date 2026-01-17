package com.example.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsernamePasswordAuthenticationToken {
    private String userName;

    public UsernamePasswordAuthenticationToken(String username, Object o) {
    }
}
