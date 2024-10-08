package me.seunghui.springbootdeveloper.dto.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//사용자가 작성한 댓글과 해당 게시물 목록 조회DTO
@Getter
@NoArgsConstructor
public class UserCommentsList {
    private Long commentId;
    private String commentContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentCreatedAt;
    private String articleTitle;

    public UserCommentsList(Long commentId,String commentContent, LocalDateTime commentCreatedAt, String articleTitle) {
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.commentCreatedAt = commentCreatedAt;
        this.articleTitle = articleTitle;
    }

}
