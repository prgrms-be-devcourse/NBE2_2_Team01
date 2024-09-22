package me.seunghui.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.Repository.RefreshTokenRepository;
import me.seunghui.springbootdeveloper.domain.RefreshToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalArgumentException("Unexpected token"));
    }
}
