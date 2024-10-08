package me.seunghui.springbootdeveloper.chatting;

import com.nimbusds.jose.shaded.gson.annotations.SerializedName;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
public class RedisConfig {


    //RedisTemplate 설정
    //레디스에 정보를 보대고 읽기 위해 사용하는 기본 도구
    //key-value 형태의 데이터를 처리하며 레디스에 메시지를 주거나 데이터를 저장할 때 사용
    //Redis 연결 팩토리를 사용 레디스 서버와의 연결을 설정
    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String,String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // JSON 직렬화 설정
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setDefaultSerializer(serializer);
        return  redisTemplate;
    }

    @Bean
    public RedisTemplate<String, String> VideoRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

    //Redis Pub/Sub 메시지를 수신하기 위한 리스너 컨테이너
    //RonnectionFactory는 Redis와의 연결을 설정
    //addMessageListener() 메서드는 특정 토픽에 대해 메시지 리스너를 등록
    //listenerAdapter는 메시지를 실제로 처리하는 리스너 객체입니다.
    //topic()은 메시지를 수신할 Redis 채널(토픽)을 지정합니다. 이 코드에서는 chat이라는 채널을 구독하고 있습니다.
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory
  , MessageListenerAdapter messageListenerAdapter
) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
  container.addMessageListener(messageListenerAdapter, new ChannelTopic("chatRoom")); // 토픽 설정

        return container;
    }

    //메시지가 수신될 때마다 onMessage() 메서드가 호출
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber redisSubscriber) {
        return  new MessageListenerAdapter(redisSubscriber, "onMessage");
    }

    //고정된 하나의 토픽을 사용할 때만 적용됩으로 동적 토픽을 사용할 땐 불필요..?
    @Bean
    public ChannelTopic topic(){
        return new ChannelTopic("topic");
    }

}

