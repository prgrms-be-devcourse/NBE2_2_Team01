package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.Repository.search.ArticleSearch;
import me.seunghui.springbootdeveloper.domain.Article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleSearch {

    // 특정 ID에 해당하는 Article을 조회하는 JPQL 쿼리
    // 해당 Article을 DTO인 ArticleListViewResponse로 변환하여 반환
//    @Query("SELECT a FROM Article a WHERE a.id=:id")
//    Page<ArticleListViewResponse> list(@Param("id") long id,Pageable pageable);

    //사용자가 작성한 게시글 목록 조회
    @Query("SELECT a FROM Article a WHERE a.author = :email ORDER BY a.id DESC ")
    List<Article> findUserArticles(@Param("email") String email);


}
