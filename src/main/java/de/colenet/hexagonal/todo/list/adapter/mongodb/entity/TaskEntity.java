package de.colenet.hexagonal.todo.list.adapter.mongodb.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskEntity(
    String id,
    String description,
    LocalDate dueDate,
    boolean completed,
    LocalDateTime completionTime
) {}
