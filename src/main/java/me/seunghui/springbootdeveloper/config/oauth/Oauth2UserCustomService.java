package me.seunghui.springbootdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.Role;
import me.seunghui.springbootdeveloper.domain.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
//Oauth2UserCustomService는 DefaultOAuth2UserService를 상속하여 OAuth2 사용자 정보 로딩 메커니즘을 재정의한다.
public class Oauth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //요청을 바탕으로 유저 정보를 담은 객체 반환
        // OAuth2 인증 요청이 들어왔을 때 호출되며, userRequest에 따라 OAuth2 사용자 정보를 로딩한다.
        OAuth2User user = super.loadUser(userRequest); //부모 클래스의 loadUser 메서드를 호출하여 사용자의 OAuth2 정보를 불러온다.

        // 로그를 통해 OAuth2User의 모든 속성 출력
        System.out.println("OAuth2User attributes: " + user.getAttributes());

        savedOrUpdate(user); //사용자 정보가 데이터베이스에 있는지 확인하고, 있으면 업데이트, 없으면 새로 저장하는 메서드를 호출한다.
        return user;

    }

    //유저가 있으면 업데이트, 없으면 유저 생성
    private User savedOrUpdate(OAuth2User oAuth2User) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Map<String, Object> attributes = oAuth2User.getAttributes(); //OAuth2 사용자 정보(email, name 등)를 속성 맵으로 가져온다.

        // OAuth2User의 속성 출력
        String email;
        String name;

        if (attributes.containsKey("kakao_account")) {
            email = (String) ((Map<String, Object>) attributes.get("kakao_account")).get("email");
            System.out.println("Email from Kakao: " + email);
            name = (String) ((Map<String, Object>) attributes.get("properties")).get("nickname");

        } else {
            // 구글 사용자 정보 처리
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        User user = userRepository.findByEmail(email)
                .map(entity -> {
                    // 로그 추가
                    System.out.println("User found, updating: " + entity);
                    return entity.update(name);
                })
                .orElseGet(() -> {
                    // 로그 추가
                    System.out.println("User not found, creating new user with email: " + email);
                    return User.builder()
                            .email(email)
                            .nickname(name)
                            .role(Role.ROLE_USER)
                            .password(encoder.encode("123456"))
                            .build();
                });
        log.info("여기까지감3");
        return userRepository.save(user); //새로운 사용자 정보를 저장하거나 업데이트된 사용자 정보를 저장한다.
    }
}

//주요 동작:
//OAuth2 인증 과정에서 사용자 정보가 전달되면, loadUser 메서드가 호출된다.
//사용자 정보가 데이터베이스에 있는지 확인하고, 있으면 업데이트, 없으면 새로 저장한다.
//최종적으로 OAuth2 사용자 정보를 반환하여 인증을 마무리한다.