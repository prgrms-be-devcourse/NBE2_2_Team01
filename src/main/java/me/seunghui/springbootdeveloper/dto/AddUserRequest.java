package me.seunghui.springbootdeveloper.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
//회원가입
public class AddUserRequest {
    private String email;
    private String password;
}
