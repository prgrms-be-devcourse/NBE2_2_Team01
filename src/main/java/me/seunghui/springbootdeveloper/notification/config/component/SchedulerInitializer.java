package me.seunghui.springbootdeveloper.notification.config.component;//package com.example.notification.config.component;
//
//import com.example.notification.entity.CoustomAlarm;
//import com.example.notification.repository.CoustomAlarmRepository;
//import com.example.notification.service.CoustomAlarmService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Log4j2
//public class SchedulerInitializer implements CommandLineRunner {
//
//    private final CoustomAlarmService coustomAlarmService;
//    private final CoustomAlarmRepository coustomAlarmNotificationRepository;
//
//    @Override
//    public void run(String... args) throws Exception {
//        log.info("Initializing SchedulerInitializer: Rescheduling all active alarms.");
//
//        // 'status'가 true인 활성화된 알람만 조회
//        List<CoustomAlarm> activeNotifications = coustomAlarmNotificationRepository.findByStatusTrue();
//
//        if (activeNotifications.isEmpty()) {
//            log.info("No active alarms found to schedule.");
//            return;
//        }
//
//        for (CoustomAlarm notification : activeNotifications) {
//            try {
//                log.info("Scheduling alarm: {}", notification);
//                coustomAlarmService.scheduleNotification(notification);
//            } catch (Exception e) {
//                log.error("Failed to schedule alarm with ID: {}", notification.getId(), e);
//            }
//        }
//
//        log.info("SchedulerInitializer completed: All active alarms have been scheduled.");
//    }
//}