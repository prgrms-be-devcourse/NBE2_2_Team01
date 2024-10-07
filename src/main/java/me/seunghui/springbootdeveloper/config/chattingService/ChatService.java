package me.seunghui.springbootdeveloper.config.chattingService;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ChatService {
    void handleUserConnection(WebSocketSession session, String roomId, String email, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions);
    void handleUserDisconnection(WebSocketSession session, String roomId, String principal);
    void memberListUpdated(String roomId, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions);
}
