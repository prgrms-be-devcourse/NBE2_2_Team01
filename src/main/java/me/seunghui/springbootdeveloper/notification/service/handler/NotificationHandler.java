package me.seunghui.springbootdeveloper.notification.service.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.notification.entity.CoustomAlarm;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import me.seunghui.springbootdeveloper.notification.event.CustomAlarmReceivedEvent;
import me.seunghui.springbootdeveloper.notification.event.NotificationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher; // ApplicationEventPublisher 주입

    // 작성자 이름(String)과 WebSocketSession을 매핑
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String author = (String) session.getAttributes().get("author");
        if (author != null) {
            sessions.put(author, session);
            log.info("WebSocket 연결 수립: {}", author);
        } else {
            log.warn("연결된 세션에 author 정보가 없습니다.");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("author 정보 없음"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String author = (String) session.getAttributes().get("author");
        if (author != null) {
            sessions.remove(author);
            log.info("WebSocket 연결 종료: {}", author);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String author = (String) session.getAttributes().get("author"); // 세션에서 사용자 식별

        log.info("Received message: {}", payload);

        try {
            CoustomAlarm customAlarmNotification = objectMapper.readValue(payload, CoustomAlarm.class);
            log.info("Parsed Notification: {}", customAlarmNotification);

            // 사용자 정보 설정 (클라이언트에서 보내지 않은 userId를 세션에서 가져와 설정)
            customAlarmNotification.setUserId(author); // CustomAlarm 엔티티에 setUserId 메소드가 있어야 함

            // 알람 데이터 처리 로직 (이벤트 발행)
            eventPublisher.publishEvent(new CustomAlarmReceivedEvent(this, customAlarmNotification));

            // 클라이언트에게 응답 전송
            String response = "Alarm received for user: " + customAlarmNotification.getUserId();
            session.sendMessage(new TextMessage(response));
        } catch (Exception e) {
            log.error("Error handling message: {}", e.getMessage(), e);
            session.sendMessage(new TextMessage("Error processing your alarm."));
        }
    }
    /**
     * 특정 작성자에게 실시간 알림 메시지 전송
     *
     * @param recipient 작성자 이름 (이메일)
     * @param message   전송할 메시지
     */
    public void sendNotification(String recipient, String message, AlarmType alarmType) {
        WebSocketSession session = sessions.get(recipient);
        if (session != null && session.isOpen()) {
            try {
                // 알림 메시지를 JSON 형식으로 만들기
                ObjectMapper objectMapper = new ObjectMapper();
                String payload = objectMapper.writeValueAsString(Map.of(
                        "message", message,
                        "alarmType", alarmType.toString()
                ));

                session.sendMessage(new TextMessage(payload));
                log.info("알림 전송: {} -> {}", recipient, payload);
            } catch (IOException e) {
                log.error("알림 전송 실패: {}", e.getMessage());
            }
        } else {
            log.warn("작성자 [{}]의 WebSocket 세션이 열려 있지 않거나 존재하지 않습니다.", recipient);
        }
    }
    /**
     * NotificationEvent를 수신하여 알림을 전송
     *
     * @param event NotificationEvent
     */
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("NotificationEvent 수신: recipient={}, message={}", event.getRecipient(), event.getMessage());
        sendNotification(event.getRecipient(), event.getMessage(),event.getAlarmType());
    }
}
