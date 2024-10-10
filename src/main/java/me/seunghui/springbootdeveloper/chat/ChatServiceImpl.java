package me.seunghui.springbootdeveloper.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.chatting.MessageBrokerService;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
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
    public void handleUserConnection(WebSocketSession session, String roomId, String accountId, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions) {
        log.info("연결 처리 실행");

        // Redis 채널 구독 설정
        messageBrokerService.subscribeToChannel(roomId);
        log.info("{} 채팅방에 입장하여 구독을 시작합니다.", roomId);

        User user = userService.findByEmail(accountId);
        String nickname = user.getNickname();
        String sessionId = session.getId();

        // 세션 정보 저장
        roomSessions.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(sessionId, session);
        log.info("roomSessions : {}", roomSessions.keySet());
        //채널 구독
      /*  messageBrokerService.publishToChannel(roomId, accountId);*/
        // 간단한 환영 메시지: sender와 message만 포함

        String welcomeMessage = String.format("%s님이 입장했습니다. 모두 환영해주세요",nickname);
       messageBrokerService.publishToChannel(roomId,welcomeMessage);

    }

    //퇴장 처리
    @Override
    public void handleUserDisconnection(WebSocketSession session, String roomId, String email) {
        log.info("퇴장 메서드 실행");
        User user = userService.findByEmail(email);
        String nickname = user.getNickname();
        // 간단한 퇴장 메시지: sender와 message만 포함
        Map<String, String> message = new HashMap<>();
        message.put("sender", nickname);
        message.put("chatMessage", String.format("%s 님이 퇴장하셨습니다.", nickname));

        String messageJson = new Gson().toJson(message); // Gson으로 메시지를 JSON으로 변환


        messageBrokerService.publishToChannel(roomId, messageJson);
       /* String bye = String.format("\"message\" : \"%s 님이 퇴장하셨습니다\"",nickname);
        messageBrokerService.publishToChannel(roomId,bye);*/
        log.info("퇴장 메시지 발행: {}", messageJson);

        roomSessions.get(roomId).remove(session.getId());

    }

    @Override
    public void memberListUpdated(String roomId, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions) {
        log.info("리스트 시작");
// 해당 방의 세션 목록을 가져옵니다.
        if (roomSessions.containsKey(roomId)) {
            Map<String, WebSocketSession> sessionsInRoom = roomSessions.get(roomId);

            // 현재 방의 모든 사용자 이름
            List<String> memberList = new ArrayList<>();
            for (String sessionId : sessionsInRoom.keySet()) {
                WebSocketSession session = sessionsInRoom.get(sessionId);
                String username = (String) session.getAttributes().get("accountId"); // 키 이름 확인
                String nickname = userService.findByEmail(username).getNickname();
                log.info("nickname:{}", nickname);
                if (nickname != null) {
                    memberList.add(nickname);
                }
            }

            // 멤버 목록을 JSON 형식으로 변환
            String memberListJson = new Gson().toJson(memberList);

            // 각 세션에 멤버 목록을 전송
            for (WebSocketSession session : sessionsInRoom.values()) {
                try {
                    session.sendMessage(new TextMessage(memberListJson));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            log.info("접속자 정보 룸/ 닉네임{}: {}", roomId, memberList);
        }
    }

    //chat/list 화면에서 해당 채팅방의 접속자 수를 반환
    @Override
    public int getUserCount(String roomId) {
        log.info("roomId: {}, roomSessions: {}", roomId, roomSessions);
        return roomSessions.containsKey(roomId) ? roomSessions.get(roomId).size() : 0;
    }

}
