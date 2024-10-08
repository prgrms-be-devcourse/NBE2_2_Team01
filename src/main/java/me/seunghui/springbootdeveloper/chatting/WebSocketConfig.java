package me.seunghui.springbootdeveloper.chatting;
import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.notification.config.component.SessionHandshakeInterceptor;
import me.seunghui.springbootdeveloper.notification.service.handler.NotificationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//1
@Configuration
@EnableWebSocket // 웹소켓의 기능을 활성화하는 어노테이션
                // -> WebSocketConfigurer 인터페이스를 구현하여서 원하는 경로에 웹소켓 연결을 처리할 수 있다.(경로 지정)

public class WebSocketConfig implements WebSocketConfigurer {


    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    @Lazy
    private final ChatHandler chatHandler;

    private final RandomVideoChatHandler randomVideoChatHandler;
    private final NotificationHandler notificationHandler;
    private final SessionHandshakeInterceptor sessionHandshakeInterceptor;

    public WebSocketConfig(WebSocketHandshakeInterceptor webSocketHandshakeInterceptor, @Lazy ChatHandler chatHandler,
                           RandomVideoChatHandler randomVideoChatHandler
                           ,NotificationHandler notificationHandler
                            ,SessionHandshakeInterceptor sessionHandshakeInterceptor) {
        this.webSocketHandshakeInterceptor = webSocketHandshakeInterceptor;
        this.chatHandler = chatHandler;
        this.randomVideoChatHandler = randomVideoChatHandler;
        this.notificationHandler = notificationHandler;
        this.sessionHandshakeInterceptor = sessionHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler((WebSocketHandler) chatHandler, "/ws/chat/*")
                .addInterceptors(webSocketHandshakeInterceptor)
                .setAllowedOrigins("*");

        registry.addHandler(new p2pVideoChatHandler(), "/ws/p2p-video-chat")
                .setAllowedOrigins("*");

        // 랜덤 화상채팅 WebSocket 핸들러 추가
        registry.addHandler(randomVideoChatHandler, "/ws/random-video-chat")
                .setAllowedOrigins("*");

        registry.addHandler(notificationHandler, "/ws/notifications")
                .addInterceptors(sessionHandshakeInterceptor)
                .setAllowedOrigins("*"); // CORS 설정 필요 시 조정
    }

    @Bean
    public WebSocketHandler socketTextHandler() {
        return (WebSocketHandler) chatHandler;
    }

     @Bean
    public ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions (){
         return new ConcurrentHashMap<>();
        }
    }