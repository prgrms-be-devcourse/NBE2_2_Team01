//package me.seunghui.springbootdeveloper.service;
//
//import lombok.RequiredArgsConstructor;
//import me.seunghui.springbootdeveloper.Repository.ArticleRepository;
//import me.seunghui.springbootdeveloper.domain.Article;
//import me.seunghui.springbootdeveloper.dto.AddArticleRequest;
//import me.seunghui.springbootdeveloper.dto.ArticleListViewResponse;
//import me.seunghui.springbootdeveloper.dto.PageRequestDTO;
//import me.seunghui.springbootdeveloper.dto.UpdateArticleRequest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ArticleService {
//    private final ArticleRepository blogRepository;
//
//    //글 등록
//    public Article save(AddArticleRequest request,String userName){
//        return blogRepository.save(request.toEntity(userName));
//    }
//
//    public List<Article> findAll(){
//        return blogRepository.findAll();
//    }
//
//
//    //글 하나 조회
//    public Article findById(Long id){
//        return blogRepository.findById(id)
//                .orElseThrow(()->new IllegalArgumentException("not found: "+id));
//    }
//
//    //글 삭제
//    public void delete(Long id){
//        Article article=blogRepository.findById(id).orElseThrow(()->new IllegalArgumentException("not found: "+id));
//
//        authorizeArticleAuthor(article);
//        blogRepository.delete(article);
//    }
//
//    //글 수정
//    @Transactional
//    public Article update(Long id, UpdateArticleRequest request){
//        Article article = blogRepository.findById(id).orElseThrow(()->new IllegalArgumentException("not found: "+id));
//
//        authorizeArticleAuthor(article);
//        article.update(request.getTitle(),request.getContent());
//        return article;
//    }
//
//    public Page<ArticleListViewResponse> getList(PageRequestDTO pageRequestDTO){ //목록
//        Sort sort= Sort.by("id").descending();
//        Pageable pageable=pageRequestDTO.getPageable(sort);
//
//        return blogRepository.searchDTO(pageable);
//        //return blogRepository.list(pageRequestDTO.getId(),pageable);
//    }
//
//    //게시글을 작성한 유저인지 확인
//    private void authorizeArticleAuthor(Article article){
//        String userName= SecurityContextHolder.getContext().getAuthentication().getName();
//        if(!article.getAuthor().equals(userName)){
//            throw new IllegalArgumentException("not authorized");
//        }
//    }
//}
