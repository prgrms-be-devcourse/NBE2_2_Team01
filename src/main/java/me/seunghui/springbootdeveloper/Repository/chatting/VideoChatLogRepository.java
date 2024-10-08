package me.seunghui.springbootdeveloper.Repository.chatting;


import me.seunghui.springbootdeveloper.domain.VideoChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoChatLogRepository extends JpaRepository<VideoChatLog, String> {


}
