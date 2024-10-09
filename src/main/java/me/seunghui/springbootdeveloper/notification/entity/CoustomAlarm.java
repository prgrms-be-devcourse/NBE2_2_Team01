package me.seunghui.springbootdeveloper.notification.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Date;
import java.util.Set;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Transactional
@EntityListeners(AuditingEntityListener.class)
public class CoustomAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String  userId; // 사용자 ID

    private String message;

    @Column(nullable = false)
    private Boolean isRead = false; // 기본값 false로 초기화

    private Boolean status;
    private LocalTime reserveAt;

    @ElementCollection
    @CollectionTable(name = "notification_days", joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "day")
    private Set<String> notificationDays; // Set<String>으로 변경

    @CreatedDate
    private Date createdAt;
}
