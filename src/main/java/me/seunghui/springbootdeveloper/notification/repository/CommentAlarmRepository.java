package me.seunghui.springbootdeveloper.notification.repository;

import me.seunghui.springbootdeveloper.notification.entity.CommentAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentAlarmRepository extends JpaRepository<CommentAlarm, Long> {
}
