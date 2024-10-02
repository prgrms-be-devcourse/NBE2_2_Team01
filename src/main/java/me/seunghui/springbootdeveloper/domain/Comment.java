package me.seunghui.springbootdeveloper.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(value={AuditingEntityListener.class})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long commentId;

    @Column
    private String commentAuthor;

    @Column
    private String commentContent;

    @CreatedDate
    @Column
    private LocalDateTime commentCreatedAt;

    @LastModifiedDate
    @Column
    private LocalDateTime commentUpdatedAt;


    @Column
    private boolean commentIsHidden;


    @ManyToOne // 여러 개의 InsertedFile이 하나의 Article에 속함
    @JoinColumn(name = "article_id", nullable = false) // 외래키 설정
    @JsonIgnore //순환 참조를 방지
    private Article article;

    @ManyToOne // 자기 참조, 대댓글을 위한 상위 댓글
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment; // 부모 댓글을 참조

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<>(); // 자식 댓글들 (대댓글 리스트)

    public void addChildComment(Comment childComment) {
        this.childComments.add(childComment);
        childComment.changeParentComment(this);
    }


    public void changeParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    public void update(String commentContent) {
        this.commentContent = commentContent;
    }

    public void blind(boolean commentIsHidden) {
        this.commentIsHidden = commentIsHidden;
    }


}
