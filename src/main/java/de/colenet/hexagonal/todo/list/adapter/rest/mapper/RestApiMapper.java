package de.colenet.hexagonal.todo.list.adapter.rest.mapper;

import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class RestApiMapper {

    public TaskDto toDto(Task model) {
        return new TaskDto(
            model.id().toString(),
            model.description(),
            model.dueDate().map(LocalDate::toString).orElse(null),
            switch (model) {
                case OpenTask __ -> "open";
                case CompletedTask __ -> "completed";
            },
            switch (model) {
                case OpenTask __ -> null;
                case CompletedTask t -> t.completionTime().toString();
            }
        );
    }
}
