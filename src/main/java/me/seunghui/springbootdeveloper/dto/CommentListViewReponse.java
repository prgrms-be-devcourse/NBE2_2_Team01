package me.seunghui.springbootdeveloper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import me.seunghui.springbootdeveloper.domain.Comment;

import java.time.LocalDateTime;

@Getter
public class CommentListViewReponse {
    private final Long commentId;
    private final String commentAuthor;
    private final String commentContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentCreatedAt;
    private final Long id; //게시글 아이디

    public CommentListViewReponse(Comment comment) {
        this.commentId=comment.getCommentId();
        this.commentAuthor=comment.getCommentAuthor();
        this.commentContent=comment.getCommentContent();
        this.commentCreatedAt=comment.getCommentCreatedAt();
        this.id=comment.getArticle().getId();
    }


}
