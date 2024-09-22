package me.seunghui.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.dto.AddUserRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {
    private final UserRepository userRepository;
    // 사용자 비밀번호를 안전하게 저장하기 위해 BCrypt 해싱 알고리즘을 사용함

    public Long save(AddUserRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .build()).getId(); //dto.getPassword()로부터 비밀번호를 가져와 bCryptPasswordEncoder로 암호화하여 User 객체에 설정
        //저장된 User 객체에서 ID를 추출하여 반환합니다. 이 ID는 새로 생성된 사용자 레코드의 고유 식별자임
    }
    //AddUserRequest는 사용자가 가입할 때 필요한 정보를 담고 있는 DTO(Data Transfer Object)
    // DTO에서 이메일과 비밀번호를 추출하여 사용자 객체를 생성함

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));
    }


    @Transactional
    public void setNicknameNullByEmail(String email) {
        log.info("닉네임을 null로 변경할 이메일: " + email);
        userRepository.findByEmail(email).ifPresent(user -> {
            user.update(null);
            userRepository.save(user);
            log.info("닉네임이 null로 변경됨: " + user);
        });
    }

}
//전체 동작 흐름
//사용자 가입 요청이 들어오면, AddUserRequest DTO를 통해 사용자 정보가 전달됩니다.
//save 메서드가 호출되면, 비밀번호는 암호화되고, 이메일과 암호화된 비밀번호를 가진 User 객체가 생성됩니다.
//이 User 객체는 UserRepository를 통해 데이터베이스에 저장됩니다.
//저장된 User 객체의 ID가 반환되어, 사용자 가입 작업이 완료됩니다.