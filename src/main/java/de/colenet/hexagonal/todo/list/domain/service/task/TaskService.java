package de.colenet.hexagonal.todo.list.domain.service.task;

import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final Clock clock;
    private final TaskRepository taskRepository;

    public TaskService(Clock clock, TaskRepository taskRepository) {
        this.clock = clock;
        this.taskRepository = taskRepository;
    }

    public Task createTask(String description) {
        return taskRepository.save(new OpenTask(UUID.randomUUID(), description));
    }

    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }

    public Optional<Task> toggleCompletionState(UUID id) {
        return taskRepository.find(id).map(this::withToggledCompletionState).map(taskRepository::save);
    }

    private Task withToggledCompletionState(Task task) {
        return switch (task) {
            case OpenTask t -> new CompletedTask(t.id(), t.description(), LocalDateTime.now(clock));
            case CompletedTask t -> new OpenTask(t.id(), t.description());
        };
    }
}
