package me.seunghui.springbootdeveloper.notification.dto;

import lombok.*;
import me.seunghui.springbootdeveloper.notification.entity.CoustomAlarm;

import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoustomAlarmDTO {
    private Long id;
    private String message;
    private Set<String> notificationDays;
    private String reserveAt; // "HH:mm" 형식
    private Boolean status;
    private Boolean isRead;
    private String dataType;
    public CoustomAlarmDTO toDTO(CoustomAlarm alarm) {
        return CoustomAlarmDTO.builder()
                .id(alarm.getId())
                .message(alarm.getMessage())
                .notificationDays(alarm.getNotificationDays())
                .reserveAt(alarm.getReserveAt().toString()) // "HH:mm" 형식으로 변환
                .status(alarm.getStatus())
                .dataType("CoustomAlarm")
                .isRead(alarm.getIsRead())
                .build();
    }

    public CoustomAlarm toEntity(CoustomAlarmDTO dto) {
        return CoustomAlarm.builder()
                .id(dto.getId())
                .message(dto.getMessage())
                .notificationDays(dto.getNotificationDays())
                .reserveAt(LocalTime.parse(dto.getReserveAt()))
                .status(dto.getStatus())
                .isRead(dto.getIsRead())
                .build();
    }
}
