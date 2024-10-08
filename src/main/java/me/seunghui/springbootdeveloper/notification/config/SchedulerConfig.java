package me.seunghui.springbootdeveloper.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {
    // 스케쥴러 설정
    @Bean
    public TaskScheduler taskScheduler() { // 스케쥴러의 크기와 초기 설정
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 필요에 따라 조정
        scheduler.setThreadNamePrefix("task-scheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
