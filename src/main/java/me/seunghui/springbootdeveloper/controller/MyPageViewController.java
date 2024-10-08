package me.seunghui.springbootdeveloper.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.dto.Article.ArticleListViewResponse;
import me.seunghui.springbootdeveloper.dto.Article.PageRequestDTO;
import me.seunghui.springbootdeveloper.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/mypage")
public class MyPageViewController {
    private final UserService userService;

    @GetMapping()  // "/articles" 경로로 GET 요청을 처리
    public String myPage(Model model) {

        String currentUserName =  SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("currentUserName", currentUserName);

        return "mypage/mypageMain";
    }



}
