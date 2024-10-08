package me.seunghui.springbootdeveloper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VideoChatController {

    @GetMapping("/p2p-video-chat")
    public String videoChat() {
        return "p2p-video-chat";
    }

    @GetMapping("/random-video-chat")
    public String randomVideoChat() {
        System.out.println("접속됨?");
        return "random-video-chat";
    }

    @GetMapping("/newRandom-video-chat")
    public String newRandomVideoChat() {
        return "newRandom-video-chat";
    }

    @GetMapping("/chat")
    public String Chat() {
        return "chat";
    }
}