package me.seunghui.springbootdeveloper.controller;


import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.MyPageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping("/mypage/updateProfileImage")
    public String updateProfileImage(@RequestParam("profileImage") MultipartFile profileImage,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            // 파일을 byte[]로 변환하고 서비스에 전달하여 저장
            byte[] imageBytes = profileImage.getBytes();
            myPageService.updateProfileImage(email, imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            // 오류 처리 로직 추가
        }
        return "redirect:/mypage"; // 이미지 변경 후 마이페이지로 리다이렉트
    }
}