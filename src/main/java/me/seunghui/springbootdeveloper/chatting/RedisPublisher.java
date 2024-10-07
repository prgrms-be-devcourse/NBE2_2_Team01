package me.seunghui.springbootdeveloper.chatting;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class RedisPublisher {

    @Autowired
    @Qualifier("VideoRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    public void publish(String roomId, String message) {
        ChannelTopic channelTopic = new ChannelTopic(roomId);
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}

