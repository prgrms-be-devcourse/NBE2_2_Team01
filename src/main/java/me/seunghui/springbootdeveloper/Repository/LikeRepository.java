package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.Like;
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


    @Query("SELECT DISTINCT a FROM Like l JOIN l.article a JOIN l.user u WHERE l.likedStatus = true AND u.email = :email ORDER BY a.createdAt DESC")
    List<Article> findUserLikedArticles(@Param("email") String email);
}
