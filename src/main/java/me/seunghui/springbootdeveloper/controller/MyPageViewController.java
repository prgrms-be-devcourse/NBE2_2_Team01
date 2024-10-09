package me.seunghui.springbootdeveloper.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.dto.Article.ArticleListViewResponse;
import me.seunghui.springbootdeveloper.dto.Article.PageRequestDTO;
import me.seunghui.springbootdeveloper.dto.User.UserArticlesList;
import me.seunghui.springbootdeveloper.dto.User.UserCommentsList;
import me.seunghui.springbootdeveloper.dto.User.UserLikedArticlesList;
import me.seunghui.springbootdeveloper.service.ArticleService;
import me.seunghui.springbootdeveloper.service.CommentService;
import me.seunghui.springbootdeveloper.service.LikeService;
import me.seunghui.springbootdeveloper.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/mypage")
public class MyPageViewController {
    private final UserService userService;
    private final ArticleService articleService;  // 게시글 관련 서비스
    private final CommentService commentService;
    private final LikeService likeService;

    @GetMapping()  // "/articles" 경로로 GET 요청을 처리
    public String myPage(Model model) {

        String currentUserName =  SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentUserName", currentUserName);

        return "mypage/mypageMain";
    }

    @GetMapping("/articles")  // "/articles" 경로로 GET 요청을 처리
    public String myPageArticles(Model model) {

        String currentUserName =  SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentUserName", currentUserName);
        List<UserArticlesList> userArticlesLists = articleService.getUserAllArticles(currentUserName);

        model.addAttribute("userArticlesLists", userArticlesLists);
        return "mypage/articles";
    }

    @GetMapping("/comments")  // "/articles" 경로로 GET 요청을 처리
    public String myPageComments(Model model) {

        String currentUserName =  SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentUserName", currentUserName);
        List<UserCommentsList> userCommentsLists=commentService.getUserAllComments(currentUserName);

        model.addAttribute("userCommentsLists", userCommentsLists);

        return "mypage/comments";
    }

    @GetMapping("/likes")  // "/articles" 경로로 GET 요청을 처리
    public String myPageLikes(Model model) {

        String currentUserName =  SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentUserName", currentUserName);
        List<UserLikedArticlesList> userLikedArticlesLists = likeService.getUserAllArticlesAndLikes(currentUserName);
        model.addAttribute("userLikedArticlesLists", userLikedArticlesLists);

        return "mypage/likes";
    }

    @GetMapping("/chat-history")  // "/articles" 경로로 GET 요청을 처리
    public String myPageChats(Model model) {

        String currentUserName =  SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentUserName", currentUserName);

        return "mypage/chat-history";
    }






}
