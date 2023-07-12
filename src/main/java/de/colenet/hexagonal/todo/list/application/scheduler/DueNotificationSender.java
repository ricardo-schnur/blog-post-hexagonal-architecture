package de.colenet.hexagonal.todo.list.application.scheduler;

import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;

public interface DueNotificationSender {
    void sendDueNotification(OpenTask task);
}
