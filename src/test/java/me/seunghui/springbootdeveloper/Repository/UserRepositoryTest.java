package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Test
    public void testFindAllEmails() {

     //    When: 모든 이메일을 가져옵니다.
//        List<String> emails = userRepository.findAllEmails();
//        // Then: 이메일 목록을 출력합니다.
//        System.out.println("모든 이메일:");
//        emails.forEach(System.out::println); // 이메일을 한 줄씩 출력
//        // Then: 이메일 목록이 예상한 대로 반환되는지 확인합니다.
////        assertThat(emails).contains("test1@example.com", "test2@example.com");
////
//
//        // When: 이메일이 존재하는지 확인합니다.
//        Optional<User> foundUser = userRepository.findByEmail("yaedam5@naver.com");
//
//        // Then: 이메일이 존재하는지 확인합니다.
//        assertTrue(foundUser.isPresent(), "이메일이 존재하지 않습니다."); // 존재해야 함
//        assertEquals("yaedam5@naver.com", foundUser.get().getEmail()); // 이메일 값 확인
//        System.out.println("이메일 검증 성공: " + foundUser.get().getEmail());

        String email="yaedam5@naver.com";
        User user = userService.findByEmail(email);
       // User user1=userRepository.findByEmail(email) ;
        System.out.println(userService.findByEmail(email));
        System.out.println(user.getEmail());

    }
}