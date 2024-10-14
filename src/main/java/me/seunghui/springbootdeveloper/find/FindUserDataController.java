package me.seunghui.springbootdeveloper.find;

import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class FindUserDataController {


    private static final Logger log = LoggerFactory.getLogger(FindUserDataController.class);
    private final FindUserDataService userService;

    @GetMapping("/find-username")
    public String findUserId(Model model) {
        return "find-username";
    }

    @GetMapping("/find-password")
    public String findUserPassword(Model model) {
        return "find-password";
    }

    @PostMapping("/find-email")
    public String findEmail(Id dto, Model model) {
        log.info("find-email");
        log.info("dto: {}", dto.toString());
        User user = userService.findEmailByNickname(dto.getNickname());
        log.info(user.getEmail());
        if (user.getEmail() != null) {
            model.addAttribute("resultMessage", "해당 닉네임으로 가입된 이메일은: " + user.getEmail() + "입니다.");
        } else {
            model.addAttribute("resultMessage", "해당 닉네임으로 가입된 이메일이 없습니다.");
        }
        return "find-username";
    }

    @PostMapping("/find-password")
    public String findPassword(Pw dto, Model model) {

        log.info("1. dto: {}", dto.toString());
        User user = userService.findPasswordByEmailAndNickname(dto.getEmail(), dto.getNickname());
        userService.updatePasswordByEmailAndNickname(dto.getEmail(), dto.getNickname(), dto.getPassword());
        log.info("5. " + user.toString());
        if (user.getEmail() != null && user.getNickname() != null) { // 둘다 틀리면 안된다
            model.addAttribute("resultMessage", "해당 이메일과 닉네임으로 새롭게 가입된 비밀번호는: " + dto.getPassword() + "입니다.");
        } else {
            model.addAttribute("resultMessage", "이메일 또는 닉네임이 올바르지 않습니다.");
        }
        return "find-password";
    }
}


