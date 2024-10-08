package me.seunghui.springbootdeveloper.config.jwt;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Data
@RequiredArgsConstructor
public class JwtPrincipal implements Principal {

    private final String username;


    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return "JwtPrincipal{" +
                "username='" + username + '\'' +
                '}';
    }

}
// 테스트