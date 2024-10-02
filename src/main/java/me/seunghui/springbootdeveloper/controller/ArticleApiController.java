package me.seunghui.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.dto.AddArticleRequest;
import me.seunghui.springbootdeveloper.dto.ArticleResponse;
import me.seunghui.springbootdeveloper.dto.CommentResponse;
import me.seunghui.springbootdeveloper.dto.UpdateArticleRequest;
import me.seunghui.springbootdeveloper.service.ArticleService;
import me.seunghui.springbootdeveloper.service.FileUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
@Log4j2
public class ArticleApiController {

    private final ArticleService articleService;
    private final FileUploadService fileUploadService;

    // 게시글 등록 API (POST)
   @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   // @PostMapping()
    public ResponseEntity<Article> addArticle(
            @RequestPart("request") AddArticleRequest request, // 게시글 데이터
            Principal principal // 현재 로그인한 사용자의 정보
    ) {
        // Article을 먼저 저장 (이미지는 아직 저장하지 않음)
        Article savedArticle = articleService.save(request, principal.getName(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle); // 저장된 게시글 반환
    }

    // 모든 게시글 조회 API (GET)
   @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // 서버가 항상 JSON 형식으로 응답하도록 명시적 설정
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 사용자 이름 가져오기
        //log.info("userName: {}", userName);
        // 데이터베이스에서 모든 게시글을 조회하여 ArticleResponse로 변환
//        List<ArticleResponse> articles = articleService.findAll()
//                .stream()
//                .map(ArticleResponse::new) // Article 엔티티를 ArticleResponse DTO로 변환
//                .toList();
        List<ArticleResponse> articles = articleService.getArticles();
//        for (ArticleResponse article : articles) {
//            log.info("Article Content: {}, Title: {}",
//                    article.getContent(),article.getTitle());
//        }

        return ResponseEntity.ok().body(articles); // 조회된 게시글 리스트를 반환
    }

    // 특정 게시글 조회 API (GET)
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ArticleResponse> findArticleById(@PathVariable("id") Long id) {
        // 게시글을 조회하고 현재 사용자 정보 확인
        Article article = articleService.findById(id);
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 로그인된 사용자 이름 가져오기

        // 작성자와 현재 사용자를 비교하여 isOwner 값 설정
        boolean isOwner = article.getAuthor().equals(currentUserName);
        log.info("currentUserName: {}", currentUserName);
        log.info("isOwner: {}", isOwner);

        return ResponseEntity.ok().body(new ArticleResponse(article)); // 조회된 게시글을 반환
    }

    // 게시글 삭제 API (DELETE)
    // 특정 게시글을 삭제
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteArticle(@PathVariable("id") Long id) {
        articleService.delete(id); // 게시글 삭제 서비스 호출
        return ResponseEntity.ok().build(); // 성공 시 200 OK 반환
    }

    // 게시글 수정 API (PUT)
    // 게시글을 수정하고, 선택적으로 파일도 함께 수정
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Article> updateArticle(
            @PathVariable("id") Long id, // 수정할 게시글 ID
            @RequestPart("request") UpdateArticleRequest request, // 게시글 수정 데이터
            @RequestPart(value = "files", required = false) List<MultipartFile> files // 선택적으로 수정할 파일 리스트
    ) {
        // 게시글과 파일을 업데이트하는 서비스 호출
        Article updatedArticle = articleService.update(id, request, files);
        return ResponseEntity.ok().body(updatedArticle); // 수정된 게시글 반환
    }
}
//consumes = MediaType.MULTIPART_FORM_DATA_VALUE를 작성한 이유는
// 서버가 해당 요청이 multipart/form-data 형식으로 전송된다는 것을 명시적으로 알리기 위함
//파일 업로드: 파일을 함께 전송할 때 multipart/form-data 형식이 사용됨

//@RequestPart로 파일이나 JSON 데이터를 동시에 전송하려면, 서버는 이 요청이 multipart/form-data 형식으로 전송될 것이라는 정보를 알아야 한다.
//consumes = MediaType.MULTIPART_FORM_DATA_VALUE는 이 컨트롤러 메서드가 multipart/form-data 형식의 요청만 처리할 수 있다는 것을 명시적으로 지정한다.
