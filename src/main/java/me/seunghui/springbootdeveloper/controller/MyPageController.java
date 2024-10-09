package me.seunghui.springbootdeveloper.controller;


import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.MyPageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequiredArgsConstructor
public class MyPageController{

    private final MyPageService myPageService;

    @GetMapping("/mypage/edit")
    public String getUserProfile(Model model) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = myPageService.getUserByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("profileImage", user.getProfileImageAsBase64());

        return "mypage/mypageedit";
    }

    @PostMapping("/mypage/edit/updateProfileImage")
    public String updateProfileImage(@RequestParam("profileImage") MultipartFile profileImage) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        myPageService.updateProfileImage(email, profileImage);

        return "redirect:/mypage/edit"; // 이미지 변경 후 마이페이지로 리다이렉트
    }
}