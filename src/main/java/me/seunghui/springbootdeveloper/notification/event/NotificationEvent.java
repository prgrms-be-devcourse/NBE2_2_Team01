package me.seunghui.springbootdeveloper.notification.event;


import lombok.Getter;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private final String recipient;
    private final String message;
    private final AlarmType alarmType;

    public NotificationEvent(Object source, String recipient, String message, AlarmType alarmType) {
        super(source);
        this.recipient = recipient;
        this.message = message;
        this.alarmType = alarmType;
    }
}
