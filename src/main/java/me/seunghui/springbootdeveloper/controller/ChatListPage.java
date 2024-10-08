package me.seunghui.springbootdeveloper.controller;

import com.nimbusds.oauth2.sdk.auth.JWTAuthentication;
import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.chat.ChatRoomService;
import me.seunghui.springbootdeveloper.config.jwt.JwtPrincipal;
import me.seunghui.springbootdeveloper.domain.ChatRoom;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.dto.chat.CreateChatForm;
import me.seunghui.springbootdeveloper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@Slf4j
@RequestMapping("/chat")
public class ChatListPage {


    private final ChatRoomService chatRoomService;
    @Autowired
    private final UserService userService;

    public ChatListPage(ChatRoomService chatRoomService, UserService userService) {
        this.chatRoomService = chatRoomService;
        this.userService = userService;
    }

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
    public String createRoom(CreateChatForm createChatForm,Principal principal){
        String email = principal.getName();
        log.info("email: {}", email);
        ChatRoom chatRoom = createChatForm.toEntity(email);
        chatRoomService.save(chatRoom);
        return "redirect:/chat/list";
    }


}
