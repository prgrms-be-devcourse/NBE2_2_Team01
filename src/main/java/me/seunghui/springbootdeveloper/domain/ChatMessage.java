package me.seunghui.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id",updatable = false)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Column(name = "sender",nullable = false)
    private String sender;  // 메시지를 보낸 사용자 닉네임

    @Column(name = "content",nullable = false)
    private String content;  // 메시지 내용

    @Column(name = "create_at", nullable = false)
    private LocalTime createAt;  // 메시지 보낸 시간

    public ChatMessage(ChatRoom chatRoom, String sender, String content, LocalTime createAt) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.content = content;
        this.createAt = createAt;
    }
}
