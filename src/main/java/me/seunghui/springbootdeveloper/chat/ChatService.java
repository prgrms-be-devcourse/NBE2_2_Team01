package me.seunghui.springbootdeveloper.chat;

import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface ChatService {
    void handleUserConnection(WebSocketSession session, String roomId, String principal, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions);
    void handleUserDisconnection(WebSocketSession session, String roomId, String principal);
    void memberListUpdated(String roomId, ConcurrentHashMap<String, Map<String, WebSocketSession>> roomSessions);
}
