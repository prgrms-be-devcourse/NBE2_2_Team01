package me.seunghui.springbootdeveloper.notification.config.component;


import me.seunghui.springbootdeveloper.notification.dto.NotificationDTO;
import me.seunghui.springbootdeveloper.notification.entity.Notification;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    // Notification -> NotificationDTO 변환
    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }
        return NotificationDTO.builder()
                .id(notification.getId())
                .alarmType(notification.getAlarmType().name()) // Enum 타입을 String으로 변환
                .createdAt(notification.getCreatedAt())
                .isRead(notification.getIsRead())
                .message(notification.getMessage())
                .recipient(notification.getRecipient())
                .targetId(notification.getTargetId())
                .makeId(notification.getMakeId())
                .userId(notification.getUser().getId())
                .userEmail(notification.getUser().getEmail()) // User 엔티티의 이메일 필드 사용
                .dataType("Notification") // 고정된 데이터 타입 값 설정
                .build();
    }

    // NotificationDTO -> Notification 변환
    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }


        return Notification.builder()
                .id(dto.getId())
                .alarmType(AlarmType.valueOf(dto.getAlarmType())) // String 타입을 Enum으로 변환
                .createdAt(dto.getCreatedAt())
                .isRead(dto.getIsRead())
                .message(dto.getMessage())
                .recipient(dto.getRecipient())
                .makeId(dto.getMakeId())
                .targetId(dto.getTargetId())
                .build();
    }
}
