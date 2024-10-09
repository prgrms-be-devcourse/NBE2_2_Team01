package me.seunghui.springbootdeveloper.chat;


import me.seunghui.springbootdeveloper.domain.ChatRoom;

import java.util.List;

public interface ChatRoomServiceInterface {
    void save(ChatRoom chatRoom);
    List<ChatRoom> list();
    List<ChatRoom> findByUsername(String username);
    void delete(Long id);

}
