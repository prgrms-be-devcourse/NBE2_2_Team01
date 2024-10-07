package me.seunghui.springbootdeveloper.config.chatting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

//구조 : 중간 레이어를 도입하는 방식 : 메세지 브로터 역할을 담당하는 서비스 클래스 -> 레디스 관련 모든 로직을 처리할 예정
//메세지 발행, 구독 설정
@Slf4j
@Service
public class MessageBrokerService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisMessageListenerContainer redisMessageListenerContainer;

   @Lazy
    private final RedisSubscriber redisSubscriber;
    @Autowired
    public MessageBrokerService(RedisTemplate<String, String> redisTemplate, RedisMessageListenerContainer redisMessageListenerContainer, @Lazy RedisSubscriber redisSubscriber) {
        this.redisTemplate = redisTemplate;
        this.redisMessageListenerContainer = redisMessageListenerContainer;

        this.redisSubscriber = redisSubscriber;
    }

    // 동적으로 Redis 채널 구독 설정
    public void subscribeToChannel(String roomId) {
        ChannelTopic topic = new ChannelTopic(roomId);  // roomId를 기준으로 채널을 동적으로 생성
        MessageListenerAdapter adapter = new MessageListenerAdapter(redisSubscriber, "onMessage");
        redisMessageListenerContainer.addMessageListener(adapter, topic);
        log.info("Redis 채널 {}을 구독 중입니다.", roomId);
    }

    public void publishToChannel(String roomId, String message) {
        ChannelTopic channelTopic = new ChannelTopic(roomId);
        log.info("Publishing to channel: {}", roomId);
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        log.info("Message published to channel: {}", roomId);
    }
}
