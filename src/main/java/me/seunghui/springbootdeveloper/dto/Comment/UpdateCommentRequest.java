package me.seunghui.springbootdeveloper.dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seunghui.springbootdeveloper.domain.Comment;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequest {
    //private Long commentId;
    private String commentContent;
    private boolean commentIsHidden;
    private boolean commentIsDeleted;

    public UpdateCommentRequest(Comment comment) {
        this.commentContent = comment.getCommentContent();
        this.commentIsDeleted = comment.isCommentIsDeleted();
        this.commentIsHidden = comment.isCommentIsHidden();

    }

}
