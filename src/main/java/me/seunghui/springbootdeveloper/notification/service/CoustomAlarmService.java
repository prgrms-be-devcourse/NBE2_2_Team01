package me.seunghui.springbootdeveloper.notification.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.Repository.UserRepository;
import me.seunghui.springbootdeveloper.domain.User;
import me.seunghui.springbootdeveloper.notification.config.component.DynamicScheduler;
import me.seunghui.springbootdeveloper.notification.dto.CoustomAlarmDTO;
import me.seunghui.springbootdeveloper.notification.entity.CoustomAlarm;
import me.seunghui.springbootdeveloper.notification.entity.Notification;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import me.seunghui.springbootdeveloper.notification.event.CustomAlarmReceivedEvent;
import me.seunghui.springbootdeveloper.notification.event.NotificationEvent;
import me.seunghui.springbootdeveloper.notification.repository.CoustomAlarmRepository;
import me.seunghui.springbootdeveloper.notification.repository.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CoustomAlarmService { // 클래스 이름 수정
    private final DynamicScheduler dynamicScheduler;
    private final CoustomAlarmRepository customAlarmRepository; // 리포지토리 이름 수정
    private final RedisTemplate<String, String> redisTemplate;
    private final ApplicationEventPublisher eventPublisher; // NotificationHandler 대신 ApplicationEventPublisher
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // 알림 생성 시 스케줄링
    public void createOrUpdateNotification(CoustomAlarm notification) {
        try {
            // 알림을 데이터베이스에 저장
            log.info(notification.getId());
            CoustomAlarm savedNotification = customAlarmRepository.save(notification);
            log.info("Saved Notification: {}", savedNotification);
            // 새롭게 알림 스케줄링
            scheduleNotification(savedNotification);
        } catch (Exception e) {
            log.error("Error saving notification: {}", e.getMessage(), e);
        }
    }

    // 알림을 스케줄링하는 메소드
    public void scheduleNotification(CoustomAlarm notification) {
        LocalTime notificationTime = notification.getReserveAt();
        Set<DayOfWeek> notificationDays = parseDays(notification.getNotificationDays());

        // 알림을 실행할 작업 정의
        Runnable notificationTask = () -> {
            DayOfWeek today = LocalDate.now().getDayOfWeek();
            LocalTime now = LocalTime.now();
            log.info("Today: {}, Notification Days: {}", today, notificationDays);
            log.info("Comparison result: now.until(notificationTime, ChronoUnit.MINUTES) = {}"
                    , now.until(notificationTime, ChronoUnit.MINUTES));
            log.info("Now: {}, Notification Time: {}", now, notificationTime);
            log.info("Notification Status: {}", notification.getStatus());
            if (notificationDays.contains(today) &&
                    now.until(notificationTime, ChronoUnit.MINUTES) == 0 &&
                    Boolean.TRUE.equals(notification.getStatus()))
                {
                try {
                    log.info("Reserve at: {}", notificationTime);
                    sendNotificationToRedisStream(notification);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        // 다음 실행 시간을 계산하여 스케줄링
        Date nextExecutionTime = getNextExecutionTime(notificationTime, notificationDays);
        dynamicScheduler.scheduleTask(notification.getId(), notificationTask, nextExecutionTime);
    }

    // Redis Streams로 알림을 전송하는 메소드
    private void sendNotificationToRedisStream(CoustomAlarm notification) throws Exception {
        StreamOperations<String, Object, Object> streamOps = redisTemplate.opsForStream();
        Map<String, Object> fields = new HashMap<>();
        fields.put("userId", notification.getUserId());
        fields.put("message", notification.getMessage());
        User recipientUser = userRepository.findByEmail(notification.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));
        streamOps.add(MapRecord.create("notifications", fields));
        Notification notifications = Notification.builder()
                .alarmType(AlarmType.COUSTOM)
                .message(notification.getMessage())
                .recipient(notification.getUserId())
                .isRead(false)
                .targetId(recipientUser.getId())
                .user(recipientUser)
                .build();

        // 알림 저장
        notificationRepository.save(notifications);
        // 이벤트 발행을 통해 알림 전송
        eventPublisher.publishEvent(new NotificationEvent(this, notification.getUserId(), notification.getMessage(),notifications.getAlarmType()));
    }

    // 다음 실행 시간을 계산하는 메소드
    private Date getNextExecutionTime(LocalTime notificationTime, Set<DayOfWeek> notificationDays) {
        LocalDate today = LocalDate.now();
        DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        LocalTime now = LocalTime.now();

        for (int i = 0; i < 7; i++) {
            DayOfWeek day = today.plusDays(i).getDayOfWeek();
            if (notificationDays.contains(day)) {
                LocalDate targetDate = today.plusDays(i);
                LocalTime targetTime = notificationTime;

                if (i == 0 && targetTime.isBefore(now)) {
                    continue; // 오늘 이미 지난 시간은 제외
                }

                return Date.from(targetTime.atDate(targetDate)
                        .atZone(ZoneId.systemDefault())
                        .toInstant());
            }
        }

        // 기본값으로 다음 날 같은 시간으로 설정
        LocalDate nextDate = today.plusDays(1);
        return Date.from(notificationTime.atDate(nextDate)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    // 요일 문자열을 Set<DayOfWeek>로 변환하는 메소드
    private Set<DayOfWeek> parseDays(Set<String> days) {
        return days.stream()
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet());
    }

    public void updateCustomAlarm(Long id, CoustomAlarmDTO coustomAlarmDTO) {
        CoustomAlarm existingAlarm = customAlarmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Custom Alarm not found"));
        existingAlarm.setMessage(coustomAlarmDTO.getMessage());
        existingAlarm.setNotificationDays(coustomAlarmDTO.getNotificationDays());
        existingAlarm.setReserveAt(LocalTime.parse(coustomAlarmDTO.getReserveAt()));
        existingAlarm.setStatus(coustomAlarmDTO.getStatus());
        customAlarmRepository.save(existingAlarm);
        scheduleNotification(existingAlarm);
    }

    public void deleteCustomAlarm(Long id) {
        customAlarmRepository.deleteById(id);
    }
    public List<CoustomAlarm> getCustomAlarmsByUser(String userId) {
        return customAlarmRepository.findByUserId(userId);
    }

    /**
     * 커스텀 알람의 상태 업데이트
     *
     * @param id     알람 ID
     * @param status 새로운 상태
     * @param userId 사용자 ID
     * @return 업데이트 성공 여부
     */
    public boolean updateCustomAlarmStatus(Long id, Boolean status, String userId) {
        return customAlarmRepository.findByIdAndUserId(id, userId)
                .map(alarm -> {
                    alarm.setStatus(status);
                    customAlarmRepository.save(alarm);
                    return true;
                })
                .orElse(false);
    }

    // 이벤트 리스너 추가
    @EventListener
    public void handleCustomAlarmReceived(CustomAlarmReceivedEvent event) {
        try {
            CoustomAlarm notification = event.getCustomAlarm();

            log.info("Handling CustomAlarmReceivedEvent: {}", notification);
            createOrUpdateNotification(notification);
        } catch (Exception e) {
            log.error("Error handling CustomAlarmReceivedEvent: {}", e.getMessage(), e);
        }
    }
}
