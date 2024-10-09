package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); //email로 사용자 정보를 가져옴

    @Modifying
    @Query("UPDATE User u SET u.profileImage = :profileImage WHERE u.email = :email")
    void updateProfileImage(@Param("email") String email, @Param("profileImage") byte[] profileImage);

//    @Query("SELECT u.profileImage FROM User u WHERE  u.email = :email")
//    byte[] getProfileImage(@Param("email") String email);

   // void deleteByUsername(String email);
}
