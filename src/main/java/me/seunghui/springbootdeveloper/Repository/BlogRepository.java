package me.seunghui.springbootdeveloper.Repository;

import me.seunghui.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {

}
