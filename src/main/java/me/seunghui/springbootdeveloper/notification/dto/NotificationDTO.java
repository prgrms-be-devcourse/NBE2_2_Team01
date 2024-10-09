package me.seunghui.springbootdeveloper.notification.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.seunghui.springbootdeveloper.notification.entity.Notification;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String alarmType;
    private Date createdAt;
    private Boolean isRead;
    private String message;
    private String recipient;
    private Long targetId;
    private Long userId; // User의 ID만 포함
    private String makeId;
    private String userEmail; // 필요 시 User의 이메일 포함
    private String dataType;

    public NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .alarmType(String.valueOf(notification.getAlarmType()))
                .createdAt(notification.getCreatedAt())
                .isRead(notification.getIsRead())
                .message(notification.getMessage())
                .recipient(notification.getRecipient())
                .targetId(notification.getTargetId())
                .userId(notification.getUser().getId())
                .makeId(notification.getMakeId())
                .dataType("Notification")
                .userEmail(notification.getUser().getEmail()) // User 엔티티에 email 필드가 있다고 가정
                .build();
    }

    public Notification toEntity(NotificationDTO dto) {
        Notification notification = Notification.builder()
                .alarmType(AlarmType.valueOf(dto.getAlarmType()))
                .createdAt(dto.getCreatedAt())
                .isRead(dto.getIsRead())
                .message(dto.getMessage())
                .recipient(dto.getRecipient())
                .targetId(dto.getTargetId())
                .makeId(dto.getMakeId())
                // User 설정은 서비스 레이어에서 처리해야 함
                .build();
        return notification;
    }

}
