package de.colenet.hexagonal.todo.list.adapter.mongodb.mapper;

import de.colenet.hexagonal.todo.list.adapter.mongodb.entity.TaskEntity;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class MongoMapper {

    public TaskEntity toEntity(Task model) {
        return new TaskEntity(
            model.id().toString(),
            model.description(),
            model.dueDate().orElse(null),
            switch (model) {
                case OpenTask __ -> false;
                case CompletedTask __ -> true;
            },
            switch (model) {
                case OpenTask __ -> null;
                case CompletedTask t -> t.completionTime();
            }
        );
    }

    public Task toModel(TaskEntity entity) {
        return entity.completed() ? toCompletedTask(entity) : toOpenTask(entity);
    }

    private static CompletedTask toCompletedTask(TaskEntity entity) {
        return new CompletedTask(
            UUID.fromString(entity.id()),
            entity.description(),
            Optional.ofNullable(entity.dueDate()),
            entity.completionTime()
        );
    }

    private static OpenTask toOpenTask(TaskEntity entity) {
        return new OpenTask(UUID.fromString(entity.id()), entity.description(), Optional.ofNullable(entity.dueDate()));
    }
}
