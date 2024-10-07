package me.seunghui.springbootdeveloper.config.chatting;


import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.Repository.chatting.ChatRoomRepository;
import me.seunghui.springbootdeveloper.Repository.chatting.MessageRepository;
import me.seunghui.springbootdeveloper.domain.ChatMessage;
import me.seunghui.springbootdeveloper.domain.ChatRoom;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// MessageListener : Redis Pub/Sub 시스템에서 메시지를 구독하고, 수신된 메시지를 처리하기 위한 인터페이스
@Slf4j
@Service
public class RedisSubscriber implements MessageListener {

    private final ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public RedisSubscriber(ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions, MessageRepository messageRepository, ChatRoomRepository chatRoomRepository) {
        this.roomSessions = roomSessions;
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(pattern);
        log.info("channel :{}",channel);
        String content =new String(message.getBody());
        log.info("content :{}",content);

        if (roomSessions.containsKey(channel)) {
            for (WebSocketSession session : roomSessions.get(channel).values()) {
                try {
                    session.sendMessage(new TextMessage(content));
                    log.info("메세지가 보내졌습니다.");
                    String email = (String) session.getAttributes().get("email");
                    saveMessage(channel,email,content);

                    log.info("메세지가 저장되었습니다");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Redis에서 수신한 메시지: " + content);
    }

    public void saveMessage(String channel,String principal,String content) {
        Long roomId  = Long.parseLong(channel);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        ChatMessage chatMessage = new ChatMessage(chatRoom,principal,content,LocalTime.now());
        messageRepository.save(chatMessage);
    }
}
