package me.seunghui.springbootdeveloper.notification.event;


import lombok.Getter;
import me.seunghui.springbootdeveloper.notification.entity.CoustomAlarm;
import org.springframework.context.ApplicationEvent;

@Getter
public class CustomAlarmReceivedEvent extends ApplicationEvent {
    private final CoustomAlarm customAlarm;

    public CustomAlarmReceivedEvent(Object source, CoustomAlarm customAlarm) {
        super(source);
        this.customAlarm = customAlarm;

    }
}
