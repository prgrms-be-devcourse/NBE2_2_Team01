package me.seunghui.springbootdeveloper.chatting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Log4j2
public class p2pVideoChatHandler extends TextWebSocketHandler {

    private static final Map<String, Map<String, WebSocketSession>> textChatSessions = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, WebSocketSession>> videoChatSessions = new ConcurrentHashMap<>();
    private static final List<WebSocketSession> waitingUsers = new CopyOnWriteArrayList<>();

    // user1과 user2를 특정 사용자로 설정
    private WebSocketSession user1Session = null;
    private WebSocketSession user2Session = null;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("handleTextMessage Received message: " + message.getPayload());

        // Signaling 데이터 처리
        String payload = message.getPayload();
        Map<String, Object> data = new ObjectMapper().readValue(payload, Map.class);

        // 방 ID 추출
        String roomId = (String) session.getAttributes().get("roomId");
        System.out.println("roomId: " + roomId);

        // 방 ID가 null인 경우 메시지 처리 중단
        if (roomId == null) {
            System.out.println("매칭되지 않은 사용자입니다. 매칭이 완료될 때까지 메시지를 처리하지 않습니다.");
            session.sendMessage(new TextMessage("{\"type\": \"waiting\", \"message\": \"매칭 대기 중입니다...\"}"));
            return;
        }

        // 채팅 또는 signaling 처리
        if ("chat".equals(data.get("type"))) {
            sendMessageToTextChatSessions(session, new TextMessage(payload));
        } else {
            sendMessageToVideoChatSessions(roomId, session, new TextMessage(payload));
        }
    }

    // 텍스트 채팅 세션에 메시지 전송
    private void sendMessageToTextChatSessions(WebSocketSession senderSession, TextMessage message) {
        String roomId = (String) senderSession.getAttributes().get("roomId");

        Map<String, WebSocketSession> sessionsInRoom = textChatSessions.get(roomId);
        if (sessionsInRoom != null) {
            for (WebSocketSession webSocketSession : sessionsInRoom.values()) {
                if (!webSocketSession.getId().equals(senderSession.getId())) {
                    sendMessage(webSocketSession, message);
                }
            }
        }
    }

    // 화상 채팅 세션에 메시지 전송
    private void sendMessageToVideoChatSessions(String roomId, WebSocketSession senderSession, TextMessage message) {
        Map<String, WebSocketSession> sessionsInRoom = videoChatSessions.get(roomId);
        if (sessionsInRoom != null) {
            for (WebSocketSession webSocketSession : sessionsInRoom.values()) {
                if (!webSocketSession.getId().equals(senderSession.getId())) {
                    sendMessage(webSocketSession, message);
                }
            }
        }
    }

    private void sendMessage(WebSocketSession session, TextMessage message) {
        synchronized (session) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // user1과 user2를 매칭하는 메서드
    private void matchSpecificUsers() {
        // user1과 user2가 연결되었는지 확인
        if (user1Session != null && user2Session != null) {
            String roomId = UUID.randomUUID().toString();
            System.out.println("매칭된 방 ID: " + roomId);

            notifyUsersOfMatch(user1Session, user2Session, roomId);
            addSessionToRooms(user1Session, user2Session, roomId);
            saveRoomIdToSession(user1Session, roomId);
            saveRoomIdToSession(user2Session, roomId);
        }
    }

    // 매칭된 사용자들에게 매칭 메시지 전송
    private void notifyUsersOfMatch(WebSocketSession user1, WebSocketSession user2, String roomId) {
        sendMessage(user1, new TextMessage("{\"type\": \"match\", \"roomId\": \"" + roomId + "\", \"message\": \"상대방과 연결되었습니다.\"}"));
        sendMessage(user2, new TextMessage("{\"type\": \"match\", \"roomId\": \"" + roomId + "\", \"message\": \"상대방과 연결되었습니다.\"}"));

        System.out.println("매칭된 사용자들에게 입장 메시지를 보냈습니다.");
    }

    // 방에 유저 추가
    private void addSessionToRooms(WebSocketSession user1, WebSocketSession user2, String roomId) {
        videoChatSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(user1.getId(), user1);
        videoChatSessions.get(roomId).put(user2.getId(), user2);

        textChatSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(user1.getId(), user1);
        textChatSessions.get(roomId).put(user2.getId(), user2);
    }

    private void saveRoomIdToSession(WebSocketSession session, String roomId) {
        session.getAttributes().put("roomId", roomId);
    }

    // 사용자가 연결되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("User connected: " + session.getId());

        // 특정 사용자가 연결되면 세션을 저장
        if (user1Session == null) {
            user1Session = session;
            System.out.println("User1 connected: " + session.getId());
        } else if (user2Session == null) {
            user2Session = session;
            System.out.println("User2 connected: " + session.getId());
        }

        // user1과 user2가 연결되면 매칭
        matchSpecificUsers();
    }

    // 사용자가 연결을 종료했을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        WebSocketSession otherSession = null;

        // 연결 종료된 사용자를 확인
        if (session.equals(user1Session)) {
            user1Session = null;
            otherSession = user2Session;
            System.out.println("User1 disconnected: " + session.getId());
        } else if (session.equals(user2Session)) {
            user2Session = null;
            otherSession = user1Session;
            System.out.println("User2 disconnected: " + session.getId());
        }

        // 상대방에게 알림 전송
        if (otherSession != null && otherSession.isOpen()) {
            sendMessage(otherSession, new TextMessage("{\"type\": \"disconnect\", \"message\": \"상대방이 연결을 끊었습니다.\"}"));
            System.out.println("상대방에게 연결 종료 메시지를 보냈습니다.");
        }

        // 방에서 세션 제거
        videoChatSessions.values().forEach(sessions -> sessions.remove(session.getId()));
        textChatSessions.values().forEach(sessions -> sessions.remove(session.getId()));

        System.out.println("세션이 방에서 제거되었습니다: " + session.getId());
    }
}
