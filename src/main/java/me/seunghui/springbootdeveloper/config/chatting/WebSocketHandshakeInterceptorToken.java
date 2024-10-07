package me.seunghui.springbootdeveloper.config.chatting;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import me.seunghui.springbootdeveloper.config.jwt.TokenProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

@Component
public class WebSocketHandshakeInterceptorToken implements HandshakeInterceptor {


    private final JwtUtils JwtUtils;

    public WebSocketHandshakeInterceptorToken(me.seunghui.springbootdeveloper.config.chatting.JwtUtils jwtUtils) {
        JwtUtils = jwtUtils;
    }


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String token = servletRequest.getHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);  // "Bearer " 부분 제거
                if (JwtUtils.validateToken(token)) {
                    String email = JwtUtils.getEmailFromToken(token);
                    Long userId = JwtUtils.getUserIdFromToken(token);

                    attributes.put("email", email);
                    attributes.put("userId", userId);
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // After handshake logic (if needed)
    }
}
