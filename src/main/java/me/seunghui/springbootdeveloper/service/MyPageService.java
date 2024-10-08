package me.seunghui.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

        private final UserRepository userRepository;

        public User getUserByEmail(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }


    @Transactional
    public void updateProfileImage(String email, byte[] profileImage) {
        // Optional로 반환된 객체를 처리
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 프로필 이미지 설정
        user.setProfileImage(profileImage);

        // 변경 사항 저장
        userRepository.save(user);
    }


}

