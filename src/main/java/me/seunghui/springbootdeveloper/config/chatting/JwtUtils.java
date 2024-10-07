package me.seunghui.springbootdeveloper.config.chatting;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.config.jwt.JwtProperties;
import me.seunghui.springbootdeveloper.config.jwt.TokenProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final TokenProvider tokenProvider;  // 직접적으로 TokenProvider 사용
    private final JwtProperties jwtProperties;

    public String getEmailFromToken(String token) {
        Claims claims = tokenProvider.getClaims(token);  // TokenProvider의 메서드를 사용
        return claims.get("email", String.class);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = tokenProvider.getClaims(token);
        return claims.get("id", Long.class);
    }

    public boolean validateToken(String token) {
        return tokenProvider.validToken(token);  // TokenProvider의 메서드를 사용
    }
}
