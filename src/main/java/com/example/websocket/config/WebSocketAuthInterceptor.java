package com.example.websocket.config;

import com.example.websocket.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {



    private final JwtService jwtService;

    public WebSocketAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // 1) Authorization header
        var headers = request.getHeaders();
        String auth = headers.getFirst("Authorization");
        String token = null;

        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring("Bearer ".length());
        }

        // 2) Or token query param (ws://host/ws?token=xxx)
        if (token == null && request instanceof ServletServerHttpRequest ssr) {
            HttpServletRequest servletReq = ssr.getServletRequest();
            token = servletReq.getParameter("token");
        }

        if (token == null) return true; // allow anonymous connect, but they won't get /user queue properly

        try {
            String userId = jwtService.verifyAndGetUserId(token);
            attributes.put("userId", userId);
        } catch (Exception ignored) {}

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
