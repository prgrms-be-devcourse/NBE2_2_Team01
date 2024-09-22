package me.seunghui.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService { //스프링 시큐리티에서 사용자의 정보를 가져오는 UserDetailsService인터페이스 구현
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)  {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email)); //메서드나 생성자가 예상하지 못한 인자를 받았을 때 발생하는 예외
    }
}
