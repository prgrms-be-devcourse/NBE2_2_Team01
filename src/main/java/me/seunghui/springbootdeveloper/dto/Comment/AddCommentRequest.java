package me.seunghui.springbootdeveloper.dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.Comment;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentRequest {
    private String commentContent;
    private Long parentCommentId; // 대댓글일 경우 부모 댓글의 ID
    //private boolean commentIsDeleted;

    // Article과 Comment 객체를 이용해 Comment 엔티티 생성
    public Comment toEntity(String commentAuthor, Article article, Comment parentComment) {
        return Comment.builder()
                .commentAuthor(commentAuthor)
                .commentContent(commentContent)
                .article(article)
                .parentComment(parentComment) // parentComment는 대댓글일 경우에 사용됨
                .build();
    }
}