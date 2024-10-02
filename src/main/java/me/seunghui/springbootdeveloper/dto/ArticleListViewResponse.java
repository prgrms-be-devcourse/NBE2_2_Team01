package me.seunghui.springbootdeveloper.dto;

import lombok.Getter;
import me.seunghui.springbootdeveloper.domain.Article;

@Getter
//게시글 목록데이터를 담고 있는 dto
public class ArticleListViewResponse {
    private final long id;
    private final String title;
    private final String content;


    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }
}
