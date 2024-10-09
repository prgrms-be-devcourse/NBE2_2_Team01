package me.seunghui.springbootdeveloper.notification.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CommentAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Long articleId;
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;
    @NotNull
    private Long userId;
    @CreatedDate
    private Date createdAt;



}
