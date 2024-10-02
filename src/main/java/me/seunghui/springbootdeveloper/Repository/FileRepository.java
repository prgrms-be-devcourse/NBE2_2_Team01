package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.InsertedFile;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface FileRepository extends JpaRepository<InsertedFile, Long> {
    //파일 이름으로 파일을 조회하는 메서드
  //  Optional<InsertedFile> findByFileName(String originalFileName);

    Optional<InsertedFile> findByArticleIdAndUuidFileName(Long articleId, String uuidFileName);
}