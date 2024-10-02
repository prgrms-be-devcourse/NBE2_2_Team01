package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //
    @Query("SELECT c FROM Comment c WHERE c.article.id=:articleId")
    List<Comment> findByArticleId(@Param("articleId") Long articleId);

    //특정 게시글의 특정 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND c.commentId = :commentId")
    List<Comment> findByCommentId(@Param("articleId") Long articleId, @Param("commentId") Long commentId);

    // 특정 부모 댓글을 포함하여 그 자식 댓글을 모두 조회 (부모 댓글도 포함)
    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId " +
            "AND (c.commentId = :commentId OR c.parentComment.commentId = :commentId " +
            "OR c.parentComment.commentId IN (SELECT sc.commentId FROM Comment sc WHERE sc.parentComment.commentId = :commentId))")
    List<Comment> findParentAndChildCommentsByArticleId(
            @Param("articleId") Long articleId,
            @Param("commentId") Long commentId
    );
}

//c.commentId = :commentId: 이 조건은 최상위 부모 댓글을 조회한다.
//c.parentComment.commentId = :commentId:해당 부모 댓글의 첫 번째 자식 댓글들을 조회한다.
//c.parentComment.commentId IN (SELECT sc.commentId FROM Comment sc WHERE sc.parentComment.commentId = :commentId): 이 조건은 부모 댓글의 자식 댓글의 자식 댓글(즉, 대댓글)을 조회합니다.