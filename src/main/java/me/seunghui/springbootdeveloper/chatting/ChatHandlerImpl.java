package me.seunghui.springbootdeveloper.chatting;

import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.chat.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//2
//TextWebSocketHandler = 텍스트 기반 메시지를 처리하는 웹소켓 핸들러
//메세지 처리 로직 구현 클래스
@Slf4j
@Component
public class ChatHandlerImpl extends TextWebSocketHandler implements ChatHandler {

    private  final ChatService chatService;
    private final MessageBrokerService messageBrokerService;
    private final ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions;

    public ChatHandlerImpl(ChatService chatService, MessageBrokerService messageBrokerService, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions) {
        this.chatService = chatService;
        this.messageBrokerService = messageBrokerService;
        this.roomSessions = roomSessions;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //방 Id 가져오기
        String roomId = session.getUri().toString().split("/ws/chat/")[1];
        String payload = message.getPayload();
        //메세지를 레디스로 발행하기 // 분산서버의 환경 / 레디스가 메세지를 모아 한 채널을 구독한 사용자들에게 전달
        messageBrokerService.publishToChannel(roomId, payload);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("accountId = {}",session.getAttributes().get("accountId"));
        String roomId = session.getUri().toString().split("/ws/chat/")[1];
        String accountId = session.getAttributes().get("accountId").toString();

        chatService.handleUserConnection(session,roomId,accountId,roomSessions);
        chatService.memberListUpdated(roomId,roomSessions);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = extractRoomId(session);
        String sessionId = session.getId();
        Map<String, WebSocketSession> sessionsInRoom = roomSessions.get(roomId);

        if (sessionsInRoom != null) {
            sessionsInRoom.remove(sessionId);  // 세션 제거
            if (sessionsInRoom.isEmpty()) {
                roomSessions.remove(roomId);  // 방에 세션이 없다면 방 제거
            }
        }

        log.info("chatImpl roomSessions after disconnection: {}", roomSessions.keySet());
    }

    // URI에서 roomId 추출
    private String extractRoomId(WebSocketSession session) {
        return session.getUri().toString().split("/ws/chat/")[1];
    }

}
