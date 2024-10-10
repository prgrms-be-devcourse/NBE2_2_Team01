package me.seunghui.springbootdeveloper.chatting;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.Repository.chatting.MessageRepository;
import me.seunghui.springbootdeveloper.chat.ChatService;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.UserService;
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
    private final MessageRepository messageRepository;

    public ChatHandlerImpl(ChatService chatService, MessageBrokerService messageBrokerService, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions, MessageRepository messageRepository) {
        this.chatService = chatService;
        this.messageBrokerService = messageBrokerService;
        this.roomSessions = roomSessions;
        this.messageRepository = messageRepository;
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 방 Id 가져오기
        String roomId = session.getUri().toString().split("/ws/chat/")[1];
        String payload = message.getPayload();

        // JSON 파싱 및 재구성
        Map<String, Object> messageData = new Gson().fromJson(payload, Map.class);
        String chatMessage = (String) messageData.get("chatMessage");

        // WebSocketSession에서 사용자 정보 가져오기 (예: Security 사용 시)
        String sender = session.getPrincipal().getName(); // 사용자 이름 또는 이메일 가져오기

        // 메시지에 sender 정보 추가
        messageData.put("sender", sender);

        // 메시지 재구성 (sender 포함)
        String formattedMessage = new Gson().toJson(messageData);

        // 메세지를 레디스로 발행하기 (레디스에 sender 정보 포함)
        messageBrokerService.publishToChannel(roomId, formattedMessage);
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
        String accountId = session.getAttributes().get("accountId").toString();
        Map<String, WebSocketSession> sessionsInRoom = roomSessions.get(roomId);

        if (roomSessions.containsKey(roomId) && roomSessions.get(roomId).containsKey(session.getId())) {
            // 중복 처리 방지
            roomSessions.get(roomId).remove(session.getId());
            chatService.handleUserDisconnection(session,roomId,accountId);
        }
        /*
        if (sessionsInRoom != null) {
            sessionsInRoom.remove(sessionId);
            // 세션 제거
            if (sessionsInRoom.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }*/

        log.info("chatImpl roomSessions after disconnection: {}", roomSessions.keySet());
    }

    // URI에서 roomId 추출
    private String extractRoomId(WebSocketSession session) {
        return session.getUri().toString().split("/ws/chat/")[1];
    }

}
