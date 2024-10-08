package me.seunghui.springbootdeveloper.controller;


import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.MyPageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController{

    private final MyPageService myPageService;

    @GetMapping("/mypage")
    public String getUserProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = myPageService.getUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("profileImage", user.getProfileImageAsBase64());

        return "mypage";
    }
}