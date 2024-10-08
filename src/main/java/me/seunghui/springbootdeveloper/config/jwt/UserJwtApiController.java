package me.seunghui.springbootdeveloper.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.RefreshTokenRepository;
import me.seunghui.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.seunghui.springbootdeveloper.domain.RefreshToken;
import me.seunghui.springbootdeveloper.domain.Role;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.UserDetailService;
import me.seunghui.springbootdeveloper.service.UserService;
import me.seunghui.springbootdeveloper.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserJwtApiController extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;  // 사용자 서비스 클래스 (회원가입, 로그아웃 처리)
    private final UserDetailService userDetailService;

    // -> OAuth2 Success Handler 를 통해, 구글&카카오 로그인시 사용되던 토큰 발급 프로세스 가지고 옴
//  + 추가적으로 인증 필터에서 인증 객체를 진행했던 작업을 가지고 와서 수동으로 진행하도록
//      * 되도록이면, 인증필터 하나에서 전부 처리하거나, 핸들러 별도로 구현하기

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token"; //리프레시 토큰이 저장될 쿠키의 이름으로 "refresh_token"이라는 이름을 사용한다.
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14); //리프레시 토큰의 유효 기간을 14일로 설정한 상수
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1); //액세스 토큰의 유효 기간을 1일로 설정한 상수
    public static final String REDIRECT_PATH = "/articles"; //인증 성공 후 사용자가 리다이렉트될 경로로 "/articles"로 설정되어 있다.

    //인증 처리에 필요한 의존성
    private final TokenProvider tokenProvider; // TokenProvider는 JWT 토큰을 생성하고,
    private final RefreshTokenRepository refreshTokenRepository; // RefreshTokenRepository는 리프레시 토큰을 저장하는 레포지토리이며,
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;


    @PostMapping("/api/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // dto 로 넘어온 값으로, 디비에서 회원 정보 불러옴 (밑에서 비교하기 위해서)
        User user = (User) userDetailService.loadUserByUsername(loginRequest.getUsername());

        // 로그인 실패시 (이메일 틀린 경우)
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        if (new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword())) {
            // 로그인 성공시

            // 1. 토큰 생성 + 쿠키 저장
            //리프레시 토큰 생성 ->저장->쿠키에 저장
            String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
            saveRefreshToken(user.getId(), refreshToken, user.getEmail());
            addRefreshTokenToCookie(request, response, refreshToken);

            //액세스 토큰 생성 -> 패스에 엑세스 토큰 추가 (이유 : 프론트 코드가 그렇게 되어야 읽어옴 )
            String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
            String targetUrl = getTargetUrl(accessToken);

            clearAuthenticationAttributes(request, response); //OAuth2 인증과 관련된 설정값과 쿠키를 제거
            // getRedirectStrategy().sendRedirect(request, response, targetUrl);



            // [ 추가 ] : (Http 응답 안에) Authorization 헤더에 AccessToken 추가
//            log.info("Authorization 헤더에 AccessToken이 추가되었습니다: {}", accessToken);
            // => 무조건 로컬에 저장해야 하며, 다른데 저장 할꺼면 if 헤더 / 프론트 측 코드 수정해야함 => 헤더에서 파싱해 보내도록

            //  중간정리, 현재 상황 = 리프레쉬 토큰은 쿠키에 저장되고, 엑세스 토큰은 헤더에 저장된 상태
            // String targetUrl = getTargetUrl(accessToken);
            // clearAuthenticationAttributes(request, response); //OAuth2 인증과 관련된 설정값과 쿠키를 제거

            // 2. 생성된 토큰 기반으로, 유저 정보를 담은 객체 저장
            // String username = userDetailsService.loadUserByUsername(token).getUsername();
            //  String username = userDetailService.loadUserByUsername(loginRequest.getUsername()).getUsername();
            User user2 = (User) userDetailService.loadUserByUsername(loginRequest.getUsername());
            Role role = Role.ROLE_USER;
            // 인증 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            new JwtPrincipal(user2.getUsername()),
                            null,
                            Arrays.asList(new SimpleGrantedAuthority(role.getAuthority()))
                    );
            // 시쿠리티 컨텍스트 홀더에에 인증(토큰)객체 저장
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authenticationToken);
            log.info(" 일반 로그인시 생성 & 저장된 : 인증 정보 (유저 객체) ");
            log.info("내용 : " + SecurityContextHolder.getContext().getAuthentication());

            // 한번찍히고, 뷰단에서 안찍히고 날아간 이유가 혹시
            // 초록책 326쪽
            // 별도의 인증성공 핸들러를 지정하지 않으면, 로그이니 성공 이휴

            //리다이렉트 안함 (로그인 성공시 이동시킬 페이지를 추가적으로 만들기)
            log.info("잘 날가야 하는 엑세스 토큰 : " + accessToken);
            response.setHeader("Authorization", "Bearer " + accessToken);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
        // 로그인 실패시(비번 틀린 경우)

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    // 로그인 성공시, 리프레쉬 토큰 발급
    private void saveRefreshToken(Long userId, String newRefreshToken, String email) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken, email));

        refreshTokenRepository.save(refreshToken);
    }

    // 로그인 성공시 발급한 리프레쉬 토큰, => 쿠키에 저장 (기존 쿠키는 삭제 후)
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }



    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}// end
