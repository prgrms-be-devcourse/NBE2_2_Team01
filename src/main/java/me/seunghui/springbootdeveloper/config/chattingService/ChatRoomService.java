package me.seunghui.springbootdeveloper.config.chattingService;


import me.seunghui.springbootdeveloper.Repository.chatting.ChatRoomRepository;
import me.seunghui.springbootdeveloper.domain.ChatRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService implements ChatRoomServiceInterface{

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }


    @Override
    public void save(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> list() {
        return chatRoomRepository.findAll();
    }

}
