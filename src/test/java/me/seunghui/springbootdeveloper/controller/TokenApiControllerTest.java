package me.seunghui.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.seunghui.springbootdeveloper.Repository.RefreshTokenRepository;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.config.jwt.JwtFactory;
import me.seunghui.springbootdeveloper.config.jwt.JwtProperties;
import me.seunghui.springbootdeveloper.domain.RefreshToken;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.dto.CreateAccessTokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc; //HTTP 요청 및 응답을 시뮬레이션하여 컨트롤러 테스트를 지원하는 도구
    @Autowired
    protected WebApplicationContext context; //Spring 애플리케이션 컨텍스트를 주입받아 MockMvc 설정에 사용됨
    @Autowired
    protected ObjectMapper objectMapper; //객체를 JSON으로 직렬화하거나 JSON을 객체로 역직렬화할 때 사용하는 Jackson 라이브러리의 매퍼
    @Autowired
    JwtProperties jwtProperties; //JWT 관련 설정 정보를 담고 있는 클래스
    @Autowired
    UserRepository userRepository; //사용자 정보를 관리하는 리포지토리
    @Autowired
    RefreshTokenRepository refreshTokenRepository; //Refresh Token 정보를 관리하는 리포지토리

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc= MockMvcBuilders.webAppContextSetup(context).build(); //MockMvc가 애플리케이션 컨텍스트와 함께 설정됨
        userRepository.deleteAll();
    }

    @DisplayName("creatNewAccessToken: 새로운 액세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        //given
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder() //User 엔터티를 데이터베이스에 저장하여 테스트에 사용할 유저 데이터를 생성
                .email("user@email.com")
                .password("test")
                .build());
        String refreshToken = JwtFactory.builder() //유저의 ID를 포함한 클레임 정보를 담아 Refresh Token을 생성
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties); //jwtProperties를 사용해 JWT 토큰을 생성

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken)); //생성한 Refresh Token을 데이터베이스에 저장

        CreateAccessTokenRequest request=new CreateAccessTokenRequest(); //생성된 Refresh Token을 담은 요청 객체를 생성
        request.setRefreshToken(refreshToken);

        final String requestBody=objectMapper.writeValueAsString(request); //요청 객체를 JSON 형식으로 직렬화

        //when
        ///api/token URL에 POST 요청을 보냄
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
