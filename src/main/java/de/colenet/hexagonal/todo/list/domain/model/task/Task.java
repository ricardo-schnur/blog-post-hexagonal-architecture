package de.colenet.hexagonal.todo.list.domain.model.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public sealed interface Task {
    UUID id();
    String description();
    Optional<LocalDate> dueDate();

    record OpenTask(UUID id, String description, Optional<LocalDate> dueDate) implements Task {}

    record CompletedTask(UUID id, String description, Optional<LocalDate> dueDate, LocalDateTime completionTime)
        implements Task {}
}
