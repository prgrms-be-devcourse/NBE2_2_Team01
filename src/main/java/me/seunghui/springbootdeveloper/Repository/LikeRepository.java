package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.Comment;
import me.seunghui.springbootdeveloper.domain.Like;
import me.seunghui.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l FROM Like l WHERE l.article.id = :articleId AND l.user.id = :userId")
    Optional<Like> findByArticleAndUser(@Param("articleId") Long articleId, @Param("userId") Long userId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.article.id = :articleId AND l.likedStatus=true")
    long countLikesByArticleId(@Param("articleId") Long articleId);
}
