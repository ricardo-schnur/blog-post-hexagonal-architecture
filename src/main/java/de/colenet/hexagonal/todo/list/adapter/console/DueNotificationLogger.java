package de.colenet.hexagonal.todo.list.adapter.console;

import de.colenet.hexagonal.todo.list.application.scheduler.DueNotificationSender;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class DueNotificationLogger implements DueNotificationSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DueNotificationLogger.class);

    public void sendDueNotification(OpenTask task) {
        LOGGER.warn("Task is due: {}", task);
    }
}
