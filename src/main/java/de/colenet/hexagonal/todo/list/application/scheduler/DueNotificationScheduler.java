package de.colenet.hexagonal.todo.list.application.scheduler;

import de.colenet.hexagonal.todo.list.domain.service.task.TaskService;
import java.time.Clock;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class DueNotificationScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DueNotificationScheduler.class);

    private final Clock clock;
    private final DueNotificationSender dueNotificationSender;
    private final TaskService taskService;

    DueNotificationScheduler(Clock clock, DueNotificationSender dueNotificationSender, TaskService taskService) {
        this.clock = clock;
        this.dueNotificationSender = dueNotificationSender;
        this.taskService = taskService;
    }

    @Scheduled(fixedRateString = "${notification.interval}", timeUnit = TimeUnit.SECONDS)
    void sendDueNotifications() {
        LOGGER.info("Sending due notifications");

        taskService
            .getAllOpenTasksWithDueDateBeforeOrEqual(LocalDate.now(clock))
            .forEach(dueNotificationSender::sendDueNotification);

        LOGGER.info("Finished sending due notifications");
    }
}
