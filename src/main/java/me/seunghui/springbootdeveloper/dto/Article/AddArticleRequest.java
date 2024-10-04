package me.seunghui.springbootdeveloper.dto.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.seunghui.springbootdeveloper.domain.Article;

@Getter
@NoArgsConstructor
@AllArgsConstructor
//클라이언트로부터 전달받은 게시글데이터를 담고 있는 DTO 클래스
public class AddArticleRequest {
    private String title;
    private String content;

    public Article toEntity(String author){
        return Article.builder().title(title).content(content).author(author).build();
    }
}
