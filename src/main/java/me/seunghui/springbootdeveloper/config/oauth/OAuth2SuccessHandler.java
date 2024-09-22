package me.seunghui.springbootdeveloper.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.RefreshTokenRepository;
import me.seunghui.springbootdeveloper.config.jwt.TokenProvider;
import me.seunghui.springbootdeveloper.domain.RefreshToken;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.UserService;
import me.seunghui.springbootdeveloper.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

// Spring Security의 SimpleUrlAuthenticationSuccessHandler를 확장한 OAuth2SuccessHandler 클래스
// 이 클래스는 OAuth2 인증 성공 후 처리 작업을 담당하며,
// 주로 액세스 토큰과 리프레시 토큰을 생성하고 쿠키에 저장한 뒤, 사용자를 특정 URL로 리다이렉트하는 역할을 한다.
@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token"; //리프레시 토큰이 저장될 쿠키의 이름으로 "refresh_token"이라는 이름을 사용한다.
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14); //리프레시 토큰의 유효 기간을 14일로 설정한 상수
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1); //액세스 토큰의 유효 기간을 1일로 설정한 상수
    public static final String REDIRECT_PATH = "/articles"; //인증 성공 후 사용자가 리다이렉트될 경로로 "/articles"로 설정되어 있다.

    //인증 처리에 필요한 의존성
    private final TokenProvider tokenProvider; // TokenProvider는 JWT 토큰을 생성하고,
    private final RefreshTokenRepository refreshTokenRepository; // RefreshTokenRepository는 리프레시 토큰을 저장하는 레포지토리이며,
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;  // UserService는 사용자 정보를 관리하는 서비스

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal(); //인증된 사용자의 정보를 OAuth2User 객체로부터 가져온다.
        log.info("OAuth2User 정보: {}", oAuth2User.getAttributes());
        log.info("여기까지감1");
        // Refresh token 생성 및 쿠키에 저장
       // User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email")); //인증된 사용자의 이메일을 통해 User 객체를 조회

        String email = null;
        // 구글 OAuth일 경우
        if (oAuth2User.getAttributes().containsKey("email")) {
            email = (String) oAuth2User.getAttributes().get("email");
        }
        // 카카오톡 OAuth일 경우
        else if (oAuth2User.getAttributes().containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        // 이메일을 찾지 못한 경우 예외 처리
        if (email == null) {
            throw new IllegalArgumentException("이메일을 찾을 수 없습니다.");
        }

        // 사용자 정보 조회
        User user = userService.findByEmail(email);
        log.info("여기까지감2");

        //리프레시 토큰 생성 ->저장->쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        //액세스 토큰 생성-> 패스에 액세스 토큰을 추가
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        //인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response); //OAuth2 인증과 관련된 설정값과 쿠키를 제거

        //리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //생성된 리프레시 토큰을 전달받아 데이터베이스에 저장
    //사용자의 userId로 기존 리프레시 토큰이 있는지 확인하고, 있으면 업데이트하고, 없으면 새로 생성하여 저장
    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
    }

    //생성된 리프레시 토큰을 쿠키에 저장
    //기존에 동일한 이름의 쿠키가 존재하면 먼저 삭제한 후, 새로운 리프레시 토큰을 쿠키에 추가
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    //인증 관련 설정값, 쿠키 제거
    //기본적으로 부모 클래스인 SimpleUrlAuthenticationSuccessHandler의 clearAuthenticationAttributes 메서드를 호출하여 인증 관련 설정을 삭제하고,
    // 이후 쿠키에 저장된 OAuth2 인증 요청도 제거한다.
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    //액세스 토큰을 패스에 추가
    //REDIRECT_PATH는 리다이렉트될 경로("/articles")이고, 그 경로에 액세스 토큰을 쿼리 파라미터로 추가하여 최종 URL을 생성한다.
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }


}
