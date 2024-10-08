package me.seunghui.springbootdeveloper.Repository.chatting;


import me.seunghui.springbootdeveloper.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
