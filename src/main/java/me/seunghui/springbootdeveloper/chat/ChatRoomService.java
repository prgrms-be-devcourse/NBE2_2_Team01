package me.seunghui.springbootdeveloper.chat;

import me.seunghui.springbootdeveloper.Repository.chatting.ChatRoomRepository;
import me.seunghui.springbootdeveloper.Repository.chatting.MessageRepository;
import me.seunghui.springbootdeveloper.domain.ChatRoom;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatRoomService implements ChatRoomServiceInterface{

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository, MessageRepository messageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
    }


    @Override
    public void save(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    @Override
    public List<ChatRoom> list() {
        return chatRoomRepository.findAll();
    }

    @Override
    public List<ChatRoom> findByUsername(String username) {
        List<ChatRoom> list = chatRoomRepository.findAll();
        //받아온 리스트를 스트림 -> 필터 (같은 username) -> 다시 담아서 반환하기
        return  list.stream()
                .filter(chatRoom -> chatRoom.getEmail().equals(username))
                .toList();
    }

    @Override
    public void delete(Long id) {
        //외래 키로 설정되어있기 때문에 해당 룸에 있는 메세지 또한 완전 삭제가 이루어져야한다.
        messageRepository.deleteByChatRoom_Id(id);
        //만약 채팅방 이름이 들어온거라면
        chatRoomRepository.deleteById(id);
    }


}
