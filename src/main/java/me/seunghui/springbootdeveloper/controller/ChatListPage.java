package me.seunghui.springbootdeveloper.controller;

import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.config.chattingService.ChatRoomService;
import me.seunghui.springbootdeveloper.domain.ChatRoom;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.dto.chat.CreateChatForm;
import me.seunghui.springbootdeveloper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatListPage {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String index(Model model){
        model.addAttribute("list", chatRoomService.list());
        return "chatting/chatRoomList";
    }

    @GetMapping("/create")
    public String create(){
        return "chatting/chatRoomCreate";
    }

    @PostMapping("/createRoom")
    public String createRoom(CreateChatForm createChatForm, Principal principal){
        log.info("Principal : {}", principal.getName());
        User user = userService.findByEmail(principal.getName());
        String nickname = user.getUsername();
        ChatRoom chatRoom = createChatForm.toEntity(nickname);
        chatRoomService.save(chatRoom);
        return "redirect:/chat/list";
    }

}
