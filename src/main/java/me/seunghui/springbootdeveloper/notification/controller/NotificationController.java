package me.seunghui.springbootdeveloper.notification.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.seunghui.springbootdeveloper.notification.config.component.CoustomAlarmMapper;
import me.seunghui.springbootdeveloper.notification.config.component.NotificationMapper;
import me.seunghui.springbootdeveloper.notification.dto.CoustomAlarmDTO;
import me.seunghui.springbootdeveloper.notification.dto.NotificationDTO;
import me.seunghui.springbootdeveloper.notification.entity.CoustomAlarm;
import me.seunghui.springbootdeveloper.notification.entity.Notification;
import me.seunghui.springbootdeveloper.notification.service.CoustomAlarmService;
import me.seunghui.springbootdeveloper.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Log4j2
public class NotificationController {

    private final NotificationService notificationService;
    private final CoustomAlarmService coustomAlarmService;

    // Mapper 사용을 위해 추가
    private final NotificationMapper notificationMapper;
    private final CoustomAlarmMapper coustomAlarmMapper;

    /**
     * 로그인된 사용자 이름(author) 가져오기
     *
     * @param principal 로그인된 사용자 정보
     * @return 사용자 이름
     */
    private String getAuthor(Principal principal) {
        return principal.getName();
    }

    /**
     * 사용자별 모든 읽지 않은 알림 조회
     *
     * @param principal 로그인된 사용자 정보
     * @return 읽지 않은 알림 리스트
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllUnreadNotifications(Principal principal) {
        String author = getAuthor(principal); // 사용자 식별 로직
        List<Notification> notifications = notificationService.getUnreadNotifications(author);
        List<NotificationDTO> dtos = notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * 사용자별 읽지 않은 알림 수 조회
     *
     * @param principal 로그인된 사용자 정보
     * @return 읽지 않은 알림 수
     */
//    @GetMapping("/unread-count")
//    public ResponseEntity<Long> getUnreadNotificationCount(Principal principal) {
//        String author = getAuthor(principal);
//        Long unreadCount = notificationService.getUnreadNotificationsCount(author);
//        return ResponseEntity.ok(unreadCount);
//    }
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount(Principal principal) {

            String username = principal.getName();
            Long unreadCount = notificationService.getUnreadNotificationsCount(username);
            Map<String, Long> response = new HashMap<>();
            response.put("unreadCount", unreadCount);
            return ResponseEntity.ok(response);

    }
    /**
     * 특정 알림을 읽음으로 처리
     *
     * @param id        알림 ID
     * @param principal 로그인된 사용자 정보
     * @return 응답
     */
    @PutMapping("/read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Principal principal) {
        String author = getAuthor(principal);
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 알림 삭제
     *
     * @param id        알림 ID
     * @param principal 로그인된 사용자 정보
     * @return 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id, Principal principal) {
        String author = getAuthor(principal);
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 사용자별 읽지 않은 커스텀 알람 조회
     *
     * @param principal 로그인된 사용자 정보
     * @return 커스텀 알람 리스트
     */
    @GetMapping("/custom")
    public ResponseEntity<List<CoustomAlarmDTO>> getCustomAlarms(Principal principal) {
        String author = principal.getName();
        List<CoustomAlarm> customAlarms = coustomAlarmService.getCustomAlarmsByUser(author);
        List<CoustomAlarmDTO> dtos = customAlarms.stream()
                .map(coustomAlarmMapper::toDTO) // Mapper를 통해 CoustomAlarm -> CoustomAlarmDTO 변환
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * 커스텀 알람의 상태 업데이트
     *
     * @param id     알람 ID
     * @param status 새로운 상태
     * @return 응답
     */
    @PutMapping("/custom/{id}/status")
    public ResponseEntity<Void> updateCustomAlarmStatus(@PathVariable Long id, @RequestBody Boolean status, Principal principal) {
        String author = principal.getName();
        boolean updated = coustomAlarmService.updateCustomAlarmStatus(id, status, author);
        if (updated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(404).build();
        }
    }

 /*   *//**
     * 사용자별 모든 알림 (일반 알림 + 커스텀 알람) 조회
     *
//     * @param principal 로그인된 사용자 정보
     * @return 모든 알림 리스트
     *//*
    @GetMapping("/all")
    public ResponseEntity<List<Object>> getAllNotifications(Principal principal) {
        String author = getAuthor(principal);

        // 일반 알림 데이터 조회
        List<Notification> notifications = notificationService.getUnreadNotifications(author);
        List<NotificationDTO> notificationDTOs = notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());

        // 커스텀 알람 데이터 조회
        List<CoustomAlarm> customAlarms = coustomAlarmService.getCustomAlarmsByUser(author);
        List<CoustomAlarmDTO> customAlarmDTOs = customAlarms.stream()
                .map(coustomAlarmMapper::toDTO)
                .collect(Collectors.toList());

        // 모든 알림 데이터를 합쳐서 반환
        List<Object> allNotifications = Stream.concat(notificationDTOs.stream(), customAlarmDTOs.stream())
                .collect(Collectors.toList());
        return ResponseEntity.ok(allNotifications);
    }*/
    @PutMapping("/custom/{id}")
    public ResponseEntity<Void> updateCustomAlarm(@PathVariable Long id, @RequestBody CoustomAlarmDTO coustomAlarmDTO) {
        coustomAlarmService.updateCustomAlarm(id, coustomAlarmDTO);
        log.info("찍히나?:" + coustomAlarmDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/custom/{id}")
    public ResponseEntity<Void> deleteCustomAlarm(@PathVariable Long id) {
        coustomAlarmService.deleteCustomAlarm(id);
        return ResponseEntity.noContent().build();
    }

}
