package me.seunghui.springbootdeveloper.config.chatting;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;

public interface ChatHandler extends WebSocketHandler {
    void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception;
    void afterConnectionEstablished(WebSocketSession session) throws Exception;
    void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception;
}
