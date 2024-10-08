package me.seunghui.springbootdeveloper.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Transactional
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class) // Date를 등록, 수정 일시 자동 반영 중요!!
@Table(name = "video_chat_log")
public class VideoChatLog {

    @Id
    private String video_chat_id; // 화상채팅 방 id
    private String user_id; // 본인 고유번호
    private String other_user_id; // 상대 고유번호

    @CreatedDate
    private LocalDateTime video_chat_create_at; // 화상채팅 시작 날짜 및 시각
    private LocalDateTime video_chat_end_at; // 화상채팅 종료 날짜 및 시각

}
