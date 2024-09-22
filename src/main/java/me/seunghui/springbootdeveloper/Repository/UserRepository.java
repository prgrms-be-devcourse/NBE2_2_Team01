package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); //email로 사용자 정보를 가져옴

//    @Query("SELECT u.email FROM User u")
//    List<String> findAllEmails();

}
