package me.seunghui.springbootdeveloper.service;//package me.seunghui.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.Repository.ArticleRepository;
import me.seunghui.springbootdeveloper.Repository.CommentRepository;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.InsertedFile;
import me.seunghui.springbootdeveloper.dto.Article.*;
import me.seunghui.springbootdeveloper.dto.User.UserArticlesList;
import me.seunghui.springbootdeveloper.dto.User.UserCommentedArticlesList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final FileUploadService fileUploadService;
    private final LikeService likeService;

    // 글 등록 메서드: 게시글을 저장하고 첨부 파일을 처리하여 파일과 게시글을 연결
    public Article save(AddArticleRequest request, String userName, List<MultipartFile> files) {
        Article savedArticle = articleRepository.save(request.toEntity(userName)); // 게시글 저장
        if (files != null && !files.isEmpty()) { // 파일이 있으면 파일도 저장
            List<InsertedFile> insertedFiles = fileUploadService.uploadFiles(files, savedArticle);
            savedArticle.addFiles(insertedFiles); // 파일을 게시글에 추가
        }
        return savedArticle;
    }

    // 모든 게시글 조회
    public List<ArticleResponse> getArticles(){
        List<Article> articles = articleRepository.findAll();
        return articles.stream()
                .map(ArticleResponse::new) // Article 엔티티를 ArticleResponse DTO로 변환
                .toList();
    }

    // 특정 ID로 게시글 조회
    public Article findById(Long id) {
        Article article= articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        long likeCount= likeService.getLikeCount(id);

        article.changeLikeCount(likeCount);

        articleRepository.save(article);
        return article;
    }

    // 게시글 삭제 메서드: 게시글을 작성한 사용자만 삭제 가능
    public void delete(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        authorizeArticleAuthor(article); // 작성자 확인
        articleRepository.delete(article);
    }

    // 게시글 수정 메서드: 내용과 파일을 수정 가능
    @Transactional
    public Article update(Long id, UpdateArticleRequest request, List<MultipartFile> files) {
        Article savedArticle = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        authorizeArticleAuthor(savedArticle); // 작성자 확인
        savedArticle.update(request.getTitle(), request.getContent()); // 내용 수정

        if (files != null && !files.isEmpty()) { // 파일 수정 처리
            List<InsertedFile> insertedFiles = fileUploadService.uploadFiles(files, savedArticle);
            savedArticle.addFiles(insertedFiles); // 기존 파일 대체
        }
        return savedArticle;
    }

    //게시글 조회수 증가
    public Article getIncreaseViewCount(Long id) {
        Article article=articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        article.isIncrementViewCount();  // 조회수 증가
         // 변경된 조회수를 저장
        return articleRepository.save(article);
    }




    // 게시글 목록 조회 메서드: 페이지네이션을 적용하여 게시글 목록을 조회
    public Page<ArticleListViewResponse> getList(PageRequestDTO pageRequestDTO) {
        Sort sort = Sort.by("id").descending();
        Pageable pageable = pageRequestDTO.getPageable(sort);
        return articleRepository.searchDTO(pageable); // QueryDSL을 통한 동적 검색
    }

    //사용자가 작성한 목록 조회
    public List<UserArticlesList> getUserAllArticles(String userName){
        List<Article> articles = articleRepository.findUserArticles(userName);
        return articles.stream()
                .map(UserArticlesList::new) // Article 엔티티를 ArticleResponse DTO로 변환
                .toList();
    }




    // 게시글의 작성자를 확인하여 권한 검증
    private void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName(); // 현재 로그인된 사용자 확인
        if (!article.getAuthor().equals(userName)) { // 작성자가 아니면 예외 발생
            throw new IllegalArgumentException("not authorized");
        }
    }
}