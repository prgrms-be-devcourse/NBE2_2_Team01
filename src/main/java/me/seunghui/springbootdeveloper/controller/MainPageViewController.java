//package me.seunghui.springbootdeveloper.controller;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import me.seunghui.springbootdeveloper.service.UserService;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//@RequiredArgsConstructor
//@Log4j2
//public class MainPageViewController {
//    private final UserService userService;
//
//    @GetMapping("/main")  // "/articles" 경로로 GET 요청을 처리
//    public String mainPage(Model model) {
//
//        String currentUserName =  SecurityContextHolder.getContext().getAuthentication().getName();
////        model.addAttribute("currentUserName", currentUserName);
//        if (!currentUserName.equals("anonymousUser")) {
//            model.addAttribute("currentUserName", currentUserName);
//        } else {
//            model.addAttribute("currentUserName", null);
//        }
//
//        return "main/mypagePage1";
//    }
//}
