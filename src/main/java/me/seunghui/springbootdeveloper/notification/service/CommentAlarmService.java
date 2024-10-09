package me.seunghui.springbootdeveloper.notification.service;


import lombok.RequiredArgsConstructor;
import me.seunghui.springbootdeveloper.Repository.ArticleRepository;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.notification.entity.CommentAlarm;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import me.seunghui.springbootdeveloper.notification.repository.CommentAlarmRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentAlarmService {

    private final CommentAlarmRepository commentAlarmRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    public void addComment(Long userId, Long articleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자 없음"));
        Article article = articleRepository.findById(articleId).orElseThrow(() -> new RuntimeException("게시물 없음"));
        CommentAlarm commentAlarm = new CommentAlarm();
        commentAlarm.setUserId(user.getId());
        commentAlarm.setArticleId(article.getId());
        commentAlarm.setAlarmType(AlarmType.COMMENT);
        commentAlarmRepository.save(commentAlarm);

    }
}
