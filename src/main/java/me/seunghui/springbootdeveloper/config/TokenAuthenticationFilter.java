package me.seunghui.springbootdeveloper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import me.seunghui.springbootdeveloper.config.jwt.JwtPrincipal;
import me.seunghui.springbootdeveloper.config.jwt.TokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@RequiredArgsConstructor
@Log4j2
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider; //JWT 토큰을 생성하고 검증하는 역할을 담당하는 클래스
    private final UserDetailService userDetailService;

    private final static String HEADER_AUTHORIZATION = "Authorization"; //Authorization 헤더의 키 값, 즉 클라이언트가 인증 토큰을 보낼 때 사용하는 HTTP 헤더 이름.
    private final static String TOKEN_PREFIX = "Bearer "; //JWT 토큰의 접두사, 일반적으로 "Bearer "로 사용



    @Override
    // 필터링 로직을 구현한 메인 메서드로, 요청을 가로채어 JWT를 처리한 후, 남은 필터 체인을 계속 실행하도록 한다.
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader=request.getHeader(HEADER_AUTHORIZATION); // 요청 헤더에서 Authorization 헤더의 값을 가져온다. 여기에는 토큰이 포함됨
        //가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader); //헤더에서 Bearer 접두사를 제거한 실제 JWT 토큰을 추출
        //가져온 토큰이 유효한지 확인하고, 유효한 때는 인증 정보를 설정
        if(tokenProvider.validToken(token)){ //토큰의 유효성을 검사한다. 토큰이 유효하면 true를 반환
            Authentication auth = tokenProvider.getAuthentication(token); // 유효한 토큰인 경우, 토큰을 사용해 인증 정보를 가져온다.
            SecurityContextHolder.getContext().setAuthentication(auth); //인증 정보를 Spring Security의 SecurityContext에 저장한다. 이렇게 설정된 인증 정보는 이후 요청이 처리될 때 참고된다.
        }

        filterChain.doFilter(request, response); //필터 체인 내의 다음 필터로 요청을 전달함
    }
//@Override
//protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//        throws ServletException, IOException {
//    String header = request.getHeader("Authorization");
//    if (header != null && header.startsWith("Bearer ")) {
//        String token = header.substring(7);
//        try {
//            log.info("JWT 토큰 검증 중: {}", token);
//            // JWT 검증 및 사용자 인증 처리
//            UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
//            if (authentication != null) {
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                log.info("JWT 인증 성공: {}", authentication.getName());
//            } else {
//                log.info("JWT 인증 실패: 인증 객체가 null입니다.");
//            }
//        } catch (Exception e) {
//            log.error("JWT 검증 중 예외 발생: {}", e.getMessage());
//        }
//    }
//    filterChain.doFilter(request, response);
//}

    private String getAccessToken(String authorizationHeader) {
        if(authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) { //헤더가 존재하고 Bearer로 시작하는지 확인
            return authorizationHeader.substring(TOKEN_PREFIX.length()); //Bearer 부분을 제거하고 순수한 토큰만 추출하여 반환
        }
        return null;
    }
}
//코드 흐름
//클라이언트가 요청을 보낼 때 Authorization 헤더에 JWT 토큰을 포함하여 전송한다.
//TokenAuthenticationFilter는 요청을 가로채 Authorization 헤더에서 JWT 토큰을 추출한다.
//Bearer 접두사를 제거한 후 토큰이 유효한지 검증한다.
//토큰이 유효하면 해당 토큰을 사용해 인증 정보를 추출하고 Spring Security의 SecurityContext에 설정한다.
//이후 필터 체인의 나머지 부분을 처리한다.
//핵심 포인트
//JWT 토큰 처리: Authorization 헤더에서 토큰을 가져와 검증한 후, 인증 정보를 설정하는 과정
//Spring Security와의 통합: 인증이 완료된 후 SecurityContextHolder에 인증 정보를 설정하여 Spring Security가 이를 바탕으로 인증된 사용자로 간주하게 만든다.

