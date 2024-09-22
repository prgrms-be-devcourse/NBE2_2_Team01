package me.seunghui.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.Repository.RefreshTokenRepository;
import me.seunghui.springbootdeveloper.config.jwt.TokenProvider;
import me.seunghui.springbootdeveloper.config.oauth.CustomLogoutHandler;
import me.seunghui.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.seunghui.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import me.seunghui.springbootdeveloper.config.oauth.Oauth2UserCustomService;
import me.seunghui.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfig {
    private final Oauth2UserCustomService oauth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    //스프링 시큐리티의 기본 보안 설정을 비활성화하는 부분
    // 이미지, CSS, JS 파일과 H2 데이터베이스 콘솔에 대한 요청은 보안 필터 체인을 통과하지 않고 바로 접근할 수 있도록 설정한다.
    @Bean
    public WebSecurityCustomizer configure() { //스프링 시큐리티 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console()) //H2 콘솔에 대한 요청 경로
                .requestMatchers("/img/**", "/css/**", "/js/**"); //정적 자원에 대한 경로를 보안 검사에서 제외
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //토큰 방식으로 인증을 하기 떄문에 기준에 사용하던 폼로그인, 세션 비활성화
        http.csrf(csrf -> csrf.disable()) //CSRF 보호 기능을 비활성화한다. 이 애플리케이션은 주로 API와 토큰 방식의 인증을 사용하므로 필요 없다.
                .httpBasic(httpBasic -> httpBasic.disable()) //기본 HTTP 인증을 비활성화한다. 토큰 인증 방식을 사용하기 때문에 HTTP Basic 인증은 필요 없다.
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable()); //기본적인 로그인 폼과 로그아웃 기능을 비활성화한다. 자체적으로 OAuth2 및 JWT 인증을 사용한다.

        // 세션 관리 비활성화
        //세션을 사용하지 않는 무상태 인증 방식을 설정한다. API 인증에서는 서버에 세션을 저장하지 않고 클라이언트에서 JWT 토큰을 통해 인증을 관리한다.
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //헤더를 확인할 커스텀 필터 추가
        //TokenAuthenticationFilter를 Spring Security의 기본 UsernamePasswordAuthenticationFilter 앞에 추가하여,
        // JWT 토큰을 헤더에서 읽고 인증하는 커스텀 필터를 사용한다.
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        //토큰 재발급 URL은 인증 없이 접근 가능하도록 설정. 나머지 API URL은 인증 필요
        http.authorizeRequests()
                .requestMatchers("/api/token").permitAll() // /api/token 경로는 인증 없이 접근할 수 있도록 설정
                .requestMatchers("/api/**").authenticated() // /api/** 경로는 인증이 필요하도록 설정
                .anyRequest().permitAll(); //그 외의 경로는 모두 접근 가능

        // OAuth2 로그인 설정
        //OAuth2 로그인을 지원하며, 성공 시 커스텀 성공 핸들러(oAuth2SuccessHandler())가 실행된다.
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oauth2UserCustomService)));

        // 로그아웃 설정
        //로그아웃 후 /login으로 리다이렉트된다.
//        http.logout(logout -> logout
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
//                .logoutSuccessUrl("/login"));
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .addLogoutHandler(new CustomLogoutHandler(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .logoutSuccessUrl("/login"));


        // 예외 처리: /api/** 경로에 대해 401 상태 코드 반환
        ///api/** 경로로 접근할 때 인증이 되지 않으면 401 UNAUTHORIZED 상태 코드를 반환한다.
        http.exceptionHandling(exceptions -> exceptions
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")));
        return http.build();
    }
    // OAuth2 인증 성공 후 토큰을 발급하는 커스텀 핸들러
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(){
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
    }

    //JWT 토큰을 확인하고 인증하는 커스텀 필터
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    //
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository(){
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    //사용자 비밀번호를 암호화하기 위해 BCryptPasswordEncoder를 사용
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

//요약:
//이 코드는 Spring Security 6.1을 기반으로 OAuth2 로그인과 JWT 토큰 인증을 지원하는 보안 설정을 구현합니다.
// 세션을 사용하지 않고 무상태(stateless) 방식으로 동작하며, 특정 API 엔드포인트는 인증 없이 접근할 수 있도록 설정하고,
// OAuth2 로그인 성공 시 사용자 정보를 저장하거나 업데이트하는 처리를 담당합니다.