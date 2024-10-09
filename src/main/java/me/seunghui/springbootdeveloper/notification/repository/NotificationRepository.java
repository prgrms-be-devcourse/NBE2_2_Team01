package me.seunghui.springbootdeveloper.notification.repository;


import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.notification.entity.Notification;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림을 받을 작성자 (recipient)로 조회 (읽지 않은 알림)
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(String recipient);
    Notification findByRecipientAndMakeIdAndAlarmTypeIsLikeAndIsReadFalse(String recipient, String makeId, AlarmType alarmType);
    // 작성자별 읽지 않은 알림 수 카운트
    Long countByRecipientAndIsReadFalse(String recipient);
    List<Notification> findByUser(User user);

}

