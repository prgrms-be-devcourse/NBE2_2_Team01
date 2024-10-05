package me.seunghui.springbootdeveloper.dto.Article;

import lombok.Getter;
import me.seunghui.springbootdeveloper.domain.Article;

import java.time.LocalDateTime;

@Getter
//게시글 목록데이터를 담고 있는 dto
public class ArticleListViewResponse {
    private final long id;
    private final String title;
    private final String content;
    private String author;
    private LocalDateTime createdAt;
    private Long viewCount;
    private Long likeCount;


    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.author = article.getAuthor();
        this.createdAt = article.getCreatedAt();
        this.viewCount = article.getViewCount();
        this.likeCount = article.getLikeCount();
    }
}
