package me.seunghui.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long id;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "email",nullable = false)
    private String email;  // 채팅방 생성자 닉네임

    @Column(name = "description")
    private String description;

    public ChatRoom(Long id, String roomName, String email, String description, LocalDateTime createdDate) {
        this.id = id;
        this.roomName = roomName;
        this.email = email;
        this.description = description;
        this.createdDate = createdDate;
    }

    @Column(nullable = false)
    private LocalDateTime createdDate;  // 채팅방 생성일

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

}
