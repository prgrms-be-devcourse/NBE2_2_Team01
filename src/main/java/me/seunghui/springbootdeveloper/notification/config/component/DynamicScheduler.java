package me.seunghui.springbootdeveloper.notification.config.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@RequiredArgsConstructor
@Log4j2
public class DynamicScheduler {
    private final TaskScheduler taskScheduler;
    private final ConcurrentHashMap<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    public void scheduleTask(Long taskId, Runnable task, Date startTime) {
        ScheduledFuture<?> future = taskScheduler.schedule(() -> {
            try {
                task.run();
                log.info("Executed scheduled task with ID: {}", taskId);
                tasks.remove(taskId);
            } catch (Exception e) {
                log.error("Error executing scheduled task with ID: {}", taskId, e);
            }
        }, startTime);
        tasks.put(taskId, future);
        log.info("Scheduled task with ID: {} at {}", taskId, startTime);
    }

    public void cancelTask(Long taskId) {
        ScheduledFuture<?> future = tasks.remove(taskId);
        if (future != null) {
            future.cancel(false);
            log.info("Cancelled task with ID: {}", taskId);
        }
    }
}
