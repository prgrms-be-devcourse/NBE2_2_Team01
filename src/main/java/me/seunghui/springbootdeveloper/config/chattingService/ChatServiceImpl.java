package me.seunghui.springbootdeveloper.config.chattingService;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.config.chatting.MessageBrokerService;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final MessageBrokerService messageBrokerService;
    private final UserService userService;
    private final ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions;

    public ChatServiceImpl(MessageBrokerService messageBrokerService, UserService userService, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions) {
        this.messageBrokerService = messageBrokerService;
        this.userService = userService;
        this.roomSessions = roomSessions;
    }


    //연결 처리
    @Override
    public void handleUserConnection(WebSocketSession session, String roomId, String principal, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions) {
        log.info("연결 처리 실행");

        // Redis 채널 구독 설정
        messageBrokerService.subscribeToChannel(roomId);
        log.info("{} 채팅방에 입장하여 구독을 시작합니다.", roomId);

        User user = userService.findByEmail(principal);
        String nickname = user.getUsername();
        String sessionId = session.getId();

        // 세션 정보 저장
        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(sessionId, session);
        log.info("chatImpl roomSessions : {}", roomSessions.keySet());
        //채널 구독
        messageBrokerService.publishToChannel(roomId, principal);

        String welcomeMessage = String.format("{\"message\": \"%s님이 입장했습니다. 모두 환영해주세요\", \"sender\": \"%s\",  \"nickname\": \"%s님\"}",nickname, principal, nickname);
        messageBrokerService.publishToChannel(roomId,welcomeMessage);

    }

    //퇴장 처리
    @Override
    public void handleUserDisconnection(WebSocketSession session, String roomId, String email) {
        User user = userService.findByEmail(email);
        String nickname = user.getUsername();

        String bye = String.format("\"message\" : \"%s 님이 퇴장하셨습니다\"",nickname);
        messageBrokerService.publishToChannel(roomId,bye);

        roomSessions.get(roomId).remove(session.getId());

    }

    @Override
    public void memberListUpdated(String roomId, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions) {
        log.info("리스트 시작");
// 해당 방의 세션 목록을 가져옵니다.
        if (roomSessions.containsKey(roomId)) {
            Map<String, WebSocketSession> sessionsInRoom = roomSessions.get(roomId);

            // 현재 방의 모든 사용자 이름을 수집합니다.
            List<String> userList = new ArrayList<>();
            for (String sessionId : sessionsInRoom.keySet()) {
                WebSocketSession session = sessionsInRoom.get(sessionId);
                String username = (String) session.getAttributes().get("email"); // 키 이름 확인
                log.info("email:{}", username);
                if (username != null) {
                    userList.add(username);
                }
            }

            // 멤버 목록을 JSON 형식으로 변환
            String userListJson = new Gson().toJson(userList);

            // 각 세션에 멤버 목록을 전송합니다.
            for (WebSocketSession session : sessionsInRoom.values()) {
                try {
                    session.sendMessage(new TextMessage(userListJson));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            log.info("접속자 정보 룸/ 닉네임{}: {}", roomId, userList);
        }
    }

}
