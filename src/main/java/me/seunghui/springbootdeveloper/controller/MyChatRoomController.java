package me.seunghui.springbootdeveloper.controller;

import lombok.extern.slf4j.Slf4j;
import me.seunghui.springbootdeveloper.chat.ChatRoomService;
import me.seunghui.springbootdeveloper.domain.ChatRoom;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/chat")
public class MyChatRoomController {
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    public MyChatRoomController(UserService userService, ChatRoomService chatRoomService) {
        this.userService = userService;
        this.chatRoomService = chatRoomService;
    }

    //사용자 Id 추출
    @GetMapping("/page")
    public String chatMyPage(Principal principal) {
        String email = principal.getName();

        User user = userService.findByEmail(email);
        Long userId = user.getId();
        return "redirect:/chat/page/" + userId;
    }



    //사용자 아이디 기반으로 개인 채팅관리 페이지로 이동
    @GetMapping("/page/{userId}")
    public String chatPage(@PathVariable("userId")Long userId, Model model) {
        User user = userService.findById(userId);
        //내가 참여한 채팅방 List
        //내가 만든 채팅방 list
        List<ChatRoom> create = chatRoomService.findByUsername(user.getUsername());
        log.info("list={}", create);
        //model로 넘기기
        model.addAttribute("create", create);
        return "/chatting/chatMyPage";
    }

    @DeleteMapping("/room/delete/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        try {
            chatRoomService.delete(roomId);
            return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting room", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

