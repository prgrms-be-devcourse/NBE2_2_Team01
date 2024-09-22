package me.seunghui.springbootdeveloper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.seunghui.springbootdeveloper.domain.Article;

@Getter

public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Article article) { //public ArticleResponse(String title,String content)랑 다름
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
