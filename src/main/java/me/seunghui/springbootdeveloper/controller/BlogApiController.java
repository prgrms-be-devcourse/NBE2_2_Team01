package me.seunghui.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.dto.AddArticleRequest;
import me.seunghui.springbootdeveloper.dto.ArticleResponse;
import me.seunghui.springbootdeveloper.dto.UpdateArticleRequest;
import me.seunghui.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogApiController {
    private final BlogService blogService;

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request, Principal principal) {
        Article savedAarticle=blogService.save(request,principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAarticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        // 1. blogService.findAll()을 호출하여 데이터베이스에서 모든 Article 엔티티를 조회
        List<ArticleResponse> articles=blogService.findAll()
                .stream()// 2. Stream API 사용하여 Article 리스트를 스트림으로 변환
                .map(ArticleResponse::new)// 3. 각 Article을 ArticleResponse로 변환 (생성자 사용)
                .toList(); // 4. 변환된 스트림을 다시 리스트로 변환
        // 5. 변환된 ArticleResponse 리스트를 ResponseEntity로 감싸서 반환
        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticleById(@PathVariable Long id) {
        Article article=blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody UpdateArticleRequest request) {
        Article updateArticle =blogService.update(id,request);

        return ResponseEntity.ok().body(updateArticle);
    }
}
