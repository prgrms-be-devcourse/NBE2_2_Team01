package me.seunghui.springbootdeveloper.notification.dto;


import lombok.Data;
import me.seunghui.springbootdeveloper.domain.Article;
import me.seunghui.springbootdeveloper.notification.entity.LikeAlarm;
import me.seunghui.springbootdeveloper.notification.enums.AlarmType;

@Data
public class AddLikeRequest {
    private AlarmType alarmType;


    public LikeAlarm toEntity(Article article, String userName){
        return LikeAlarm.builder().alarmType(alarmType).article(article).username(userName).build();
    }

}
