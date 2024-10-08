package me.seunghui.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.Role;
import me.seunghui.springbootdeveloper.domain.Comment;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.dto.User.AddUserRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {
    private final UserRepository userRepository; // 사용자 정보를 처리하는 레포지토리

    // 사용자 저장 메서드 (회원가입)
    public Long save(AddUserRequest dto) {
        // 비밀번호를 안전하게 저장하기 위해 BCrypt 해싱 알고리즘 사용
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // DTO에서 이메일과 비밀번호를 추출하여, 비밀번호를 암호화 후 User 객체 생성
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword())) // 비밀번호를 해시 처리
                        .role(Role.ROLE_USER) // 자동 유저 부여
                        .nickname(dto.getNickname())

                .build()).getId(); // 저장된 사용자 레코드의 ID 반환
    }

    // ID로 사용자 조회
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    // 이메일로 사용자 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No user found with email: " + email));
    }

    // 특정 사용자의 닉네임을 null로 설정하는 메서드 (이메일로 사용자 조회)
    @Transactional // 데이터 변경 시 트랜잭션을 보장함
    public void setNicknameNullByEmail(String email) {
        log.info("닉네임을 null로 변경할 이메일: " + email);
        userRepository.findByEmail(email).ifPresent(user -> {
            // 닉네임을 null로 변경 후 저장
            user.update(null);
            userRepository.save(user);
            log.info("닉네임이 null로 변경됨: " + user);
        });
    }

    public String currentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 로그인된 사용자 확인
//        if (!user.getEmail().equals(userName)) {
//            throw new IllegalArgumentException("not authorized");
//        }
        log.info("userName: {}" , userName);
        return userName;
    }
    //사용자가 좋아요 누른 게시글 조회
    //사용자가 쓴 게시글 조회
    //시용자가 쓴 댓글 조회

    public User findByUsername(String username) {
        Optional<User> user = userRepository.findByEmail(username);
        return user.orElseThrow(() -> new IllegalArgumentException("No user found with email: " + username));
    }
}
//전체 동작 흐름
//사용자 가입 요청이 들어오면, AddUserRequest DTO를 통해 사용자 정보가 전달됩니다.
//save 메서드가 호출되면, 비밀번호는 암호화되고, 이메일과 암호화된 비밀번호를 가진 User 객체가 생성됩니다.
//이 User 객체는 UserRepository를 통해 데이터베이스에 저장됩니다.
//저장된 User 객체의 ID가 반환되어, 사용자 가입 작업이 완료됩니다.