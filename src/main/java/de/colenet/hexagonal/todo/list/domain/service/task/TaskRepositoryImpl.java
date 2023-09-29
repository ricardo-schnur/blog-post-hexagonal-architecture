package de.colenet.hexagonal.todo.list.domain.service.task;

import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

// Only exists to make the context load - delete this class as soon as we have a "real" implementation
@Repository
class TaskRepositoryImpl implements TaskRepository {

    @Override
    public Task save(Task task) {
        return task;
    }

    @Override
    public Optional<Task> find(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Task> getAll() {
        return Collections.emptyList();
    }
}
