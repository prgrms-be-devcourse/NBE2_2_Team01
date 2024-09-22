package me.seunghui.springbootdeveloper.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.dto.ArticleListViewResponse;
import me.seunghui.springbootdeveloper.dto.ArticleViewResonse;
import me.seunghui.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogViewController {
    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleListViewResponse> articles = blogService.findAll() //모든 Article 객체들의 리스트를 가져옴
                .stream()// 1. Article 객체들을 Stream으로 변환
                .map(ArticleListViewResponse::new) // 2. 각 Article을 ArticleListViewResponse로 변환
                .toList(); // 3. 변환된 Stream을 List로 다시 변환
        model.addAttribute("articles", articles);

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        Article article = blogService.findById(id);
        model.addAttribute("article", article);
        return "article";
    }

    @GetMapping("/new-article")
    //id 키를 가진 쿼리 파라미터의 값을 id 변수에 매핑(id는 없을 수도 있음)
    public String newAticle(@RequestParam(required=false)Long id, Model model) {
        if(id==null){
            model.addAttribute("article", new ArticleViewResonse()); //기본 생성자를 이용
        }else {
            Article article=blogService.findById(id);
            model.addAttribute("article", new ArticleViewResonse(article));
        }

        return "newArticle";
    }

}
