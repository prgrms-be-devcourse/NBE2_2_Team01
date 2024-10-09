package me.seunghui.springbootdeveloper.notification.entity;


import jakarta.persistence.*;
import lombok.*;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @CreatedDate
    private Date createdAt;

    private Boolean isRead;

    private String message;

    private String recipient;

    private Long targetId;
    private String makeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 알림을 받을 사용자
    public void changeisRead(Boolean isRead) {
        this.isRead = isRead;
    }
    // 기타 필요한 필드 및 메소드
}
