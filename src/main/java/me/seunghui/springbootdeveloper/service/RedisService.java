package me.seunghui.springbootdeveloper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
@Log4j2
public class RedisService {

    // RedisTemplate 주입 시 @Qualifier 사용
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(@Qualifier("VideoRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveVideoChatMessageLog(
            String videoChatId, Long userId, Long otherUserId, String message) throws JsonProcessingException {
        String key = "RedisVideoChatMessageLog : " + videoChatId;

        // 대화 내역을 객체로 생성
        Map<String, Object> chatLog = new HashMap<>();
        chatLog.put("video_chat_id", videoChatId);
        chatLog.put("user_id", userId);
        chatLog.put("other_user_id", otherUserId);
        chatLog.put("video_chat_message", message);
        chatLog.put("video_chat_message_sent_at", LocalDateTime.now().toString());

        // HashMap을 JSON으로 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        String serializedChatLog = objectMapper.writeValueAsString(chatLog);

        // Redis List에 대화 내역 저장
        redisTemplate.opsForList().rightPush(key, serializedChatLog);

        // TTL 설정: 한달 (30일)
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public List<Object> getVideoChatMessageLog(String videoChatId) throws JsonProcessingException {
        String key = "RedisVideoChatMessageLog : " + videoChatId;

        // Redis에서 저장된 대화 내역을 리스트로 가져옴
        List<Object> chatLogs = redisTemplate.opsForList().range(key, 0, -1); // 모든 리스트 항목을 가져옴

        if (chatLogs == null || chatLogs.isEmpty()) {
            return new ArrayList<>(); // 대화 내역이 없을 경우 빈 리스트 반환
        }

        return chatLogs; // 대화 내역 반환
    }

}
