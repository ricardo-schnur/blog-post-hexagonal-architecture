package de.colenet.hexagonal.todo.list.domain.service.task;

import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
    Task save(Task task);

    Optional<Task> find(UUID id);

    List<Task> getAll();
}
