package me.seunghui.springbootdeveloper.config.jwt;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
