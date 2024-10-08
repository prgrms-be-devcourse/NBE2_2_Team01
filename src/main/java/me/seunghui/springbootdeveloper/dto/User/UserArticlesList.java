package me.seunghui.springbootdeveloper.dto.User;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seunghui.springbootdeveloper.domain.Article;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class UserArticlesList {
    private Long id;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long viewCount;

    public UserArticlesList(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.createdAt = article.getCreatedAt();
        this.viewCount = article.getViewCount();
    }


}
