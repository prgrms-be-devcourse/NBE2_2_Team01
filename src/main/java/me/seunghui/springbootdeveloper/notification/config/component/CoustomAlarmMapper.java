package me.seunghui.springbootdeveloper.notification.config.component;



import me.seunghui.springbootdeveloper.notification.dto.CoustomAlarmDTO;
import me.seunghui.springbootdeveloper.notification.entity.CoustomAlarm;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class CoustomAlarmMapper {

    // CoustomAlarm -> CoustomAlarmDTO 변환
    public CoustomAlarmDTO toDTO(CoustomAlarm alarm) {
        if (alarm == null) {
            return null;
        }
        return CoustomAlarmDTO.builder()
                .id(alarm.getId())
                .message(alarm.getMessage())
                .notificationDays(alarm.getNotificationDays()) // Set<String> 형식으로 변환
                .reserveAt(alarm.getReserveAt().toString()) // LocalTime -> String 변환
                .status(alarm.getStatus())
                .isRead(alarm.getIsRead())
                .dataType("CoustomAlarm") // 고정된 데이터 타입 값 설정
                .build();
    }

    // CoustomAlarmDTO -> CoustomAlarm 변환
    public CoustomAlarm toEntity(CoustomAlarmDTO dto) {
        if (dto == null) {
            return null;
        }
        return CoustomAlarm.builder()
                .id(dto.getId())
                .message(dto.getMessage())
                .notificationDays(dto.getNotificationDays()) // Set<String> 유지
                .reserveAt(LocalTime.parse(dto.getReserveAt())) // String -> LocalTime 변환
                .status(dto.getStatus())
                .isRead(dto.getIsRead())
                .build();
    }
}
