package me.seunghui.springbootdeveloper.dto.chat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.seunghui.springbootdeveloper.domain.VideoChatLog;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoChatLogDTO {


    private String video_chat_id; // 화상채팅 방 id
    private Long user_id; // 본인 고유번호
    private Long other_user_id; // 상대 고유번호
    private LocalDateTime video_chat_create_at; // 화상채팅 시작 날짜 및 시각
    private LocalDateTime video_chat_end_at; // 화상채팅 종료 날짜 및 시각

    public VideoChatLogDTO(VideoChatLog videoChatLog) {
        this.video_chat_id = videoChatLog.getVideo_chat_id();
        this.user_id = videoChatLog.getUser_id();
        this.other_user_id = videoChatLog.getOther_user_id();
        this.video_chat_create_at = videoChatLog.getVideo_chat_create_at();
        this.video_chat_end_at = videoChatLog.getVideo_chat_end_at();
    }

    public VideoChatLog toEntity() {
        return VideoChatLog.builder()
                .video_chat_id(video_chat_id)
                .user_id(user_id)
                .other_user_id(other_user_id)
                .video_chat_create_at(video_chat_create_at)
                .video_chat_end_at(video_chat_end_at)
                .build();
    }

}
