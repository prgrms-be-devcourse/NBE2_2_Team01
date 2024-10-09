package me.seunghui.springbootdeveloper.notification.repository;


import me.seunghui.springbootdeveloper.notification.entity.CoustomAlarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CoustomAlarmRepository extends JpaRepository<CoustomAlarm, Long> {
    List<CoustomAlarm> findByUserId(String userId);
    Optional<CoustomAlarm> findByIdAndUserId(Long id, String userId);
}

