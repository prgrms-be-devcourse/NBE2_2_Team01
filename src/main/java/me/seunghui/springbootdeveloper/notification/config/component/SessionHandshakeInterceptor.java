package me.seunghui.springbootdeveloper.notification.config.component;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Log4j2
public class SessionHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * WebSocket 핸드셰이크 전 실행
     *
     * @param request    HTTP 요청
     * @param response   HTTP 응답
     * @param wsHandler  WebSocket 핸들러
     * @param attributes WebSocket 세션 속성
     * @return 핸드셰이크 여부
     * @throws Exception 예외 발생 시
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Spring Security 세션을 통해 사용자 이름(author)을 가져옵니다.
        if (request.getPrincipal() != null) {
            String author = request.getPrincipal().getName(); // 보통 이메일로 설정
            attributes.put("author", author);
            log.info("HandshakeInterceptor: author={}", author);
        } else {
            log.warn("HandshakeInterceptor: Principal is null");
        }
        return true;
    }

    /**
     * WebSocket 핸드셰이크 후 실행
     *
     * @param request    HTTP 요청
     * @param response   HTTP 응답
     * @param wsHandler  WebSocket 핸들러
     * @param exception  예외 정보
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 추가적인 로직 필요 시 구현
    }
}
