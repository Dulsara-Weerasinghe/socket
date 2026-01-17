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

        String token = null;

        // Authorization: Bearer <token>
        String auth = request.getHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring(7);
        }

        // Query param: ?token=<token>
        if (token == null && request instanceof ServletServerHttpRequest ssr) {
            token = ssr.getServletRequest().getParameter("token");
        }

        if (token == null) {
            System.out.println("WS handshake without token: " + request.getURI());
            return false; // for POC: require auth to use /user queue
        }

        try {
            String userId = jwtService.verifyAndGetUserId(token);
            attributes.put("userId", userId);
            System.out.println("WS handshake OK. userId=" + userId + " uri=" + request.getURI());
            return true;
        } catch (Exception e) {
            System.out.println("WS JWT verify failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
