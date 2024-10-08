package me.seunghui.springbootdeveloper.notification.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import me.seunghui.springbootdeveloper.domain.Article;
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
public class LikeAlarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;
    @ManyToOne // 여러 개의 InsertedFile이 하나의 Article에 속함
    @JoinColumn(name = "article_id", nullable = false) // 외래키 설정
    @JsonIgnore //순환 참조를 방지
    private Article article;
    private String username;
    @CreatedDate
    private Date createAt;
    public LikeAlarm(Article article, String username) {
        this.article = article;
        this.username = username;
        this.alarmType = AlarmType.LIKE;
    }
}
