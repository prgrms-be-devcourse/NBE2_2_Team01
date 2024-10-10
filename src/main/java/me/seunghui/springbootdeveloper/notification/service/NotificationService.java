package me.seunghui.springbootdeveloper.notification.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.ArticleRepository;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.notification.entity.Notification;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import me.seunghui.springbootdeveloper.notification.event.NotificationEvent;
import me.seunghui.springbootdeveloper.notification.repository.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentAlarmService commentAlarmService;


    public boolean likeIsRead(String recipient,String makeId,AlarmType alarmType) {
        Notification notification1=notificationRepository
                .findByRecipientAndMakeIdAndAlarmTypeIsLikeAndIsReadFalse(recipient
                ,makeId,alarmType);
        return notification1 != null;
    }
//    게시글에 좋아요를 누른 경우 알림 생성 및 전송
    public void sendLikeNotification(Long articleId, String fromAuthor) {
        try {
            // 게시글 조회

            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            // 게시글 작성자
            String toAuthor = article.getAuthor();
            // 수신자 사용자 조회
            User recipientUser = userRepository.findByEmail(toAuthor)
                    .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));
            if(!article.getAuthor().equals(fromAuthor)) {
            // 알림 메시지 생성
            String message = fromAuthor + "님이 회원님의 "+article.getTitle() +" 게시물을 좋아합니다.";

            // 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .alarmType(AlarmType.LIKE)
                    .message(message)
                    .recipient(toAuthor)
                    .isRead(false)
                    .makeId(fromAuthor)
                    .targetId(articleId)
                    .user(recipientUser)
                    .build();

            // 알림 저장
            notificationRepository.save(notification);
            commentAlarmService.addComment(recipientUser.getId(),articleId);
            log.info("알림 저장 완료: {}", notification);

            // 알림 이벤트 발행
            eventPublisher.publishEvent(new NotificationEvent(this, toAuthor, message,notification.getAlarmType()));
            log.info("NotificationEvent 발행: recipient={}, message={}", toAuthor, message);
            }

        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 게시글에 댓글을 남긴 경우 알림 생성 및 전송
     *
     * @param articleId  댓글이 달린 게시글 ID
     * @param fromAuthor 댓글을 남긴 사용자의 이름 (이메일)
     */
    public void sendCommentNotification(Long articleId, String fromAuthor) {
        try {
            // 게시글 조회
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

            // 게시글 작성자
            String toAuthor = article.getAuthor();

            // 수신자 사용자 조회
            User recipientUser = userRepository.findByEmail(toAuthor)
                    .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

            if(!article.getAuthor().equals(fromAuthor)) {
            // 알림 메시지 생성
            String message = fromAuthor + "님이 회원님의 "+article.getTitle() +" 게시물에 댓글을 남겼습니다.";

            // 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .alarmType(AlarmType.COMMENT)
                    .message(message)
                    .recipient(toAuthor)
                    .isRead(false)
                    .makeId(fromAuthor)
                    .targetId(articleId)
                    .user(recipientUser)
                    .build();

            // 알림 저장
            notificationRepository.save(notification);
            log.info("알림 저장 완료: {}", notification);

            // 알림 이벤트 발행
            eventPublisher.publishEvent(new NotificationEvent(this, toAuthor, message,notification.getAlarmType()));
            log.info("NotificationEvent 발행: recipient={}, message={}", toAuthor, message);
            }

        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }
    /**
     * 사용자별 읽지 않은 알림 목록 조회
     *
     * @param recipient 사용자 이름 (author)
     * @return 읽지 않은 알림 리스트
     */
    public List<Notification> getUnreadNotifications(String recipient) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(recipient);
    }

    /**
     * 사용자별 읽지 않은 알림 수 카운트
     *
     * @param recipient 사용자 이름 (author)
     * @return 읽지 않은 알림 수
     */
    public Long getUnreadNotificationsCount(String recipient) {
        return notificationRepository.countByRecipientAndIsReadFalse(recipient);
    }
    @Transactional
    public Notification createNotification(Notification notification, Principal principal) {
        String author = principal.getName();
        User user = userRepository.findByEmail(author)
                .orElseThrow(() -> new RuntimeException("User not found"));
        notification.setUser(user);
        return notificationRepository.save(notification);
    }
    public List<Notification> getNotificationsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByUser(user);
    }


    /**
     * 특정 알림을 읽음으로 처리
     *
     * @param notificationId 알림 ID
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        notification.changeisRead(true);
        notificationRepository.save(notification);
    }

    /**
     * 특정 알림 삭제
     *
     * @param notificationId 알림 ID
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
