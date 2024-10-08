package me.seunghui.springbootdeveloper.chatting;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import me.seunghui.springbootdeveloper.dto.chat.VideoChatLogDTO;
import me.seunghui.springbootdeveloper.service.RedisService;
import me.seunghui.springbootdeveloper.service.VideoChatLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Log4j2
@RequiredArgsConstructor
public class RandomVideoChatHandler extends TextWebSocketHandler {

    private static final Map<String, Map<String, WebSocketSession>> textChatSessions = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, WebSocketSession>> videoChatSessions = new ConcurrentHashMap<>();
    private static final List<WebSocketSession> waitingUsers = new CopyOnWriteArrayList<>();

    private final RedisService redisService;
    private final VideoChatLogService videoChatLogService;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("handleTextMessage Received message: " + message.getPayload());
        System.out.println("handleTextMessage Received session: " + session);

        // Signaling 데이터 처리
        String payload = message.getPayload();
        Map<String, Object> data = new ObjectMapper().readValue(payload, Map.class);
        // 방 ID 추출
        String roomId = (String) session.getAttributes().get("roomId");
        System.out.println("roomId: " + roomId);

        // message 내용 추출
        String chatMessage = (String) data.get("message");
        System.out.println("chatMessage : " + chatMessage);
        // 방 ID가 null인 경우 메시지 처리 중단
        if (roomId == null) {
            System.out.println("매칭되지 않은 사용자입니다. 매칭이 완료될 때까지 메시지를 처리하지 않습니다.");
            // 매칭 대기 중이라는 메시지를 클라이언트로 보낼 수도 있음
            session.sendMessage(new TextMessage("{\"type\": \"waiting\", \"message\": \"매칭 대기 중입니다...\"}"));
            return;
        }

        // 상대방 세션 아이디 추출
        String otherSessionId = getOtherSessionId(roomId, session.getId());
        System.out.println("상대방 세션 아이디: " + otherSessionId);

        // 채팅 또는 signaling 처리
        if ("chat".equals(data.get("type"))) {
            sendMessageToTextChatSessions(session, new TextMessage(payload));
            //        redisService.saveVideoChatMessageLog(roomId, session.getId(), otherSessionId, chatMessage);
            redisService.saveVideoChatMessageLog(roomId, session.getId(), otherSessionId, chatMessage);
        } else if ("offer".equals(data.get("type")) || "answer".equals(data.get("type")) || "candidate".equals(data.get("type"))){
            sendMessageToVideoChatSessions(roomId, session, new TextMessage(payload));
        } else {
            System.out.println("지원하지 않는 메시지 타입: " + data.get("type"));
        }
    }

    // 텍스트 채팅 세션에 메시지 전송
    private void sendMessageToTextChatSessions(WebSocketSession senderSession, TextMessage message) {
        // 세션에서 방 ID 추출
        String roomId = (String) senderSession.getAttributes().get("roomId");

        // 방 ID에 해당하는 텍스트 채팅 세션에서 메시지 전송
        Map<String, WebSocketSession> sessionsInRoom = textChatSessions.get(roomId);
        if (sessionsInRoom != null) {
            for (WebSocketSession webSocketSession : sessionsInRoom.values()) {
                if (!webSocketSession.getId().equals(senderSession.getId())) {
                    sendMessage(webSocketSession, message);
                }
            }
        } else {
            System.out.println("해당 방 ID에 대한 텍스트 채팅 세션이 존재하지 않습니다: " + roomId);
        }
    }

    // 상대방 세션 아이디를 가져오는 메서드
    private String getOtherSessionId(String roomId, String currentSessionId) {
        Map<String, WebSocketSession> sessionsInRoom = videoChatSessions.get(roomId);
        if (sessionsInRoom != null) {
            for (String sessionId : sessionsInRoom.keySet()) {
                if (!sessionId.equals(currentSessionId)) {
                    return sessionId;  // 현재 세션이 아닌 상대방 세션 아이디 반환
                }
            }
        }
        return null;  // 상대방 세션이 없을 경우 null 반환
    }

    // 화상 채팅 세션에 메시지 전송
    private void sendMessageToVideoChatSessions(String roomId, WebSocketSession senderSession, TextMessage message) {
        System.out.println("sendMessageToVideoChatSessions : " + senderSession.toString());
        System.out.println("sendMessageToVideoChatSessions : " + message.toString());

        // 지정된 방 ID에 해당하는 세션을 가져옴
        Map<String, WebSocketSession> sessionsInRoom = videoChatSessions.get(roomId);
        if (sessionsInRoom != null) {
            for (WebSocketSession webSocketSession : sessionsInRoom.values()) {
                if (!webSocketSession.getId().equals(senderSession.getId())) {
                    sendMessage(webSocketSession, message);
                }
            }
        } else {
            System.out.println("해당 방에 대한 세션이 없습니다: " + roomId);
        }
    }

    // 메시지 전송 메서드
    private void sendMessage(WebSocketSession session, TextMessage message) {
        synchronized (session) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();  // 예외 발생 시 처리
                }
            }
        }
    }

    // 대기 중인 사용자 매칭
    private void matchUsers() {
        // 대기열에 2명 이상이 있는 경우 매칭
        System.out.println("현재 대기 중인 사용자 수: " + waitingUsers.size());
        if (waitingUsers.size() >= 2) {
            Collections.shuffle(waitingUsers); // 대기열 랜덤 셔플
            WebSocketSession user1 = waitingUsers.remove(0);
            WebSocketSession user2 = waitingUsers.remove(0);

            String roomId = UUID.randomUUID().toString(); // 고유한 방 ID 생성
            System.out.println("matchUsers : " + roomId);

            // 매칭된 사용자에게 방 ID와 연결 메시지 전송
            notifyUsersOfMatch(user1, user2, roomId);

            // 화상 및 텍스트 채팅 세션에 추가
            addSessionToRooms(user1, user2, roomId);

            // 각 세션에 방 ID를 저장 (추가)
            saveRoomIdToSession(user1, roomId);
            saveRoomIdToSession(user2, roomId);

            // 디버깅 코드: 유저 둘이 같은 방에 들어가 있는지 확인
            verifyUsersInSameRoom(roomId, user1, user2);

            videoChatLogService.videoChatStartTimeLog(VideoChatLogDTO.builder()
                    .video_chat_id(roomId)
                    .user_id(user1.getId())
                    .other_user_id(user2.getId())
                    .build());

        }
    }

    // 매칭된 사용자들에게 매칭 메시지 전송
    private void notifyUsersOfMatch(WebSocketSession user1, WebSocketSession user2, String roomId) {
        // user1은 offerer, user2는 answerer 역할 할당
        sendMessage(user1, new TextMessage("{\"type\": \"match\", \"roomId\": \"" + roomId + "\", \"role\": \"offerer\", \"message\": \"상대방과 연결되었습니다.\"}"));
        sendMessage(user2, new TextMessage("{\"type\": \"match\", \"roomId\": \"" + roomId + "\", \"role\": \"answerer\", \"message\": \"상대방과 연결되었습니다.\"}"));

        // 역할 정보를 세션에 저장
        user1.getAttributes().put("role", "offerer");
        user2.getAttributes().put("role", "answerer");
    }

    // 방에 유저 추가
    private void addSessionToRooms(WebSocketSession user1, WebSocketSession user2, String roomId) {
        videoChatSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(user1.getId(), user1);
        videoChatSessions.get(roomId).put(user2.getId(), user2);

        textChatSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(user1.getId(), user1);
        textChatSessions.get(roomId).put(user2.getId(), user2);

        // 디버깅: 방에 몇 명이 들어왔는지 출력
        System.out.println("방에 추가된 유저 수 (비디오 세션): " + videoChatSessions.get(roomId).size());
        System.out.println("방에 추가된 유저 수 (텍스트 세션): " + textChatSessions.get(roomId).size());
    }

    // 방 ID를 세션에 저장
    private void saveRoomIdToSession(WebSocketSession session, String roomId) {
        System.out.println("saveRoomIdToSession(roomId) : " + roomId);
        session.getAttributes().put("roomId", roomId);
        System.out.println("session 체크 : " + session);
    }

    // 방에 들어간 유저 확인
    private void verifyUsersInSameRoom(String roomId, WebSocketSession user1, WebSocketSession user2) {
        Map<String, WebSocketSession> videoSessions = videoChatSessions.get(roomId);
        Map<String, WebSocketSession> textSessions = textChatSessions.get(roomId);
        System.out.println("두 유저가 들어간 roomId : " + roomId);
        // 비디오 세션 확인
        if (videoSessions != null && videoSessions.size() == 2) {
            System.out.println("비디오 세션에 두 유저가 모두 들어감: " + user1.getId() + ", " + user2.getId());
        } else {
            System.out.println("비디오 세션에 문제가 있음. 현재 세션 수: " + (videoSessions != null ? videoSessions.size() : 0));
        }

        // 텍스트 세션 확인
        if (textSessions != null && textSessions.size() == 2) {
            System.out.println("텍스트 세션에 두 유저가 모두 들어감: " + user1.getId() + ", " + user2.getId());
        } else {
            System.out.println("텍스트 세션에 문제가 있음. 현재 세션 수: " + (textSessions != null ? textSessions.size() : 0));
        }
    }

    // 사용자가 연결되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("User connected: " + session.getId());

        // 이미 대기열에 있지 않으면 추가
        if (!waitingUsers.contains(session)) {
            waitingUsers.add(session);
            System.out.println("현재 대기 중인 사용자 수: " + waitingUsers.size());

            // 매칭 시도
            matchUsers();
        }
    }

//    // 사용자가 연결을 종료했을 때 호출
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        if (waitingUsers.remove(session)) {
//            System.out.println("사용자" + session.getId() + "가 대기열에서 제거되었습니다.");
//        }
//
//        // 방에서 세션 제거
//        videoChatSessions.values().forEach(sessions -> sessions.remove(session.getId()));
//        textChatSessions.values().forEach(sessions -> sessions.remove(session.getId()));
//
//        System.out.println("세션이 방에서 제거되었습니다: " + session.getId());
//    }

    // 사용자가 연결을 종료했을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        System.out.println("Closed roomId : " + roomId);
        if (roomId != null) {
            // 방에서 나가는 사용자가 있는 경우 상대방에게 "나감" 메시지 전송
            Map<String, WebSocketSession> sessionsInRoom = textChatSessions.get(roomId);
            if (sessionsInRoom != null) {
                for (WebSocketSession webSocketSession : sessionsInRoom.values()) {
                    if (!webSocketSession.getId().equals(session.getId())) {
                        sendMessage(webSocketSession, new TextMessage("{\"type\": \"system\", \"message\": \"상대방이 채팅에서 나갔습니다.\"}"));
                    }
                }
            }
            // 종료시간 업데이트
            videoChatLogService.videoChatEndTimeLog(roomId);

            // 비디오 및 텍스트 채팅 세션에서 해당 사용자를 제거
            videoChatSessions.get(roomId).remove(session.getId());
            textChatSessions.get(roomId).remove(session.getId());

            // 방에 아무도 남아있지 않으면 방 자체를 제거
            if (videoChatSessions.get(roomId).isEmpty()) {
                videoChatSessions.remove(roomId);
            }
            if (textChatSessions.get(roomId).isEmpty()) {
                textChatSessions.remove(roomId);
            }

            System.out.println("세션이 방에서 제거되었습니다: " + session.getId());
        }

        // 대기열에서 세션 제거
        if (waitingUsers.remove(session)) {
            System.out.println("사용자" + session.getId() + "가 대기열에서 제거되었습니다.");
        }
    }

}

