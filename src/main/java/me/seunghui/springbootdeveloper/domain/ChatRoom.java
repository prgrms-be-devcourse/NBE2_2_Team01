package me.seunghui.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "creator",nullable = false)
    private String creator;  // 채팅방 생성자 닉네임

    @Column(name = "description")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDate;  // 채팅방 생성일

}
