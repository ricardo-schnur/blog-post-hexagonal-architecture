package de.colenet.hexagonal.todo.list.adapter.cache;

import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepository;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import org.springframework.stereotype.Repository;

@Repository
class TaskCache implements TaskRepository {

    // LinkedHashMap keeps insertion order
    private final Map<UUID, Task> tasks = new LinkedHashMap<>();

    @Override
    public Task save(Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    @Override
    public Optional<Task> find(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> getAll() {
        return List.copyOf(tasks.values());
    }

    @Override
    public List<OpenTask> getAllOpenTasksWithDueDateBeforeOrEqual(LocalDate date) {
        Predicate<LocalDate> isBeforeOrEqualToDate = otherDate -> otherDate.isBefore(date) || otherDate.isEqual(date);

        return tasks
            .values()
            .stream()
            .filter(task -> task.dueDate().filter(isBeforeOrEqualToDate).isPresent())
            .filter(OpenTask.class::isInstance)
            .map(OpenTask.class::cast)
            .toList();
    }
}
