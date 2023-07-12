package de.colenet.hexagonal.todo.list.domain.model.task;

import java.time.LocalDateTime;
import java.util.UUID;

public sealed interface Task {
    UUID id();
    String description();

    record OpenTask(UUID id, String description) implements Task {}

    record CompletedTask(UUID id, String description, LocalDateTime completionTime) implements Task {}
}
