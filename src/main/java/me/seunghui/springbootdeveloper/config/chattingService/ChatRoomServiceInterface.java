package me.seunghui.springbootdeveloper.config.chattingService;


import me.seunghui.springbootdeveloper.domain.ChatRoom;

import java.util.List;

public interface ChatRoomServiceInterface {
    void save(ChatRoom chatRoom);
    List<ChatRoom> list();
}
