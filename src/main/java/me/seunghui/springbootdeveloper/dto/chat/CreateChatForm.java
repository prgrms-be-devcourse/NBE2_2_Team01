package me.seunghui.springbootdeveloper.dto.chat;


import lombok.*;
import me.seunghui.springbootdeveloper.domain.ChatRoom;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CreateChatForm {
    private String roomName;
    private String creator;
    private String description;

    public ChatRoom toEntity(String creator) {
        return new ChatRoom(null,roomName,creator,description, LocalDateTime.now());
    }

}
