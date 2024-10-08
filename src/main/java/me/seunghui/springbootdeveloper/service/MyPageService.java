package me.seunghui.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

        private final UserRepository userRepository;

        public User getUserByEmail(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }

