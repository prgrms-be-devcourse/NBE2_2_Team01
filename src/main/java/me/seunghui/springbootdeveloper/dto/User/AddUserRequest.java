package me.seunghui.springbootdeveloper.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//회원가입
public class AddUserRequest {
    private String email;
    private String password;
}
