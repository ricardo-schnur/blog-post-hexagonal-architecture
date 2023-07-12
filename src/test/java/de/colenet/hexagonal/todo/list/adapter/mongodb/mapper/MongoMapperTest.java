package de.colenet.hexagonal.todo.list.adapter.mongodb.mapper;

import static de.colenet.hexagonal.todo.list.adapter.mongodb.entity.TaskEntityTestdataFactory.createTaskEntityBuilder;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createCompletedTaskBuilder;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createOpenTaskBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.colenet.hexagonal.todo.list.adapter.mongodb.entity.TaskEntity;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MongoMapperTest {

    private final MongoMapper mapper = new MongoMapper();

    @Test
    void toEntity_MapsOpenTaskAsExpected() {
        LocalDate dueDate = LocalDate.now();
        Task model = createOpenTaskBuilder().withDueDate(Optional.of(dueDate));

        var result = mapper.toEntity(model);

        assertThat(result).isEqualTo(new TaskEntity(model.id().toString(), model.description(), dueDate, false, null));
    }

    @Test
    void toEntity_MapsCompletedTaskAsExpected() {
        LocalDate dueDate = LocalDate.now();
        LocalDateTime completionTime = LocalDateTime.now();
        Task model = createCompletedTaskBuilder()
            .with(t -> t.dueDate(Optional.of(dueDate)).completionTime(completionTime));

        var result = mapper.toEntity(model);

        assertThat(result)
            .isEqualTo(new TaskEntity(model.id().toString(), model.description(), dueDate, true, completionTime));
    }

    @Test
    void toModel_MapsOpenTaskAsExpected() {
        LocalDate dueDate = LocalDate.now();
        TaskEntity entity = createTaskEntityBuilder()
            .with(t -> t.completed(false).completionTime(null).dueDate(dueDate));

        var result = mapper.toModel(entity);

        assertThat(result)
            .isEqualTo(new OpenTask(UUID.fromString(entity.id()), entity.description(), Optional.of(dueDate)));
    }

    @Test
    void toModel_MapsCompletedTaskAsExpected() {
        LocalDate dueDate = LocalDate.now();
        LocalDateTime completionTime = LocalDateTime.now();
        TaskEntity entity = createTaskEntityBuilder()
            .with(t -> t.completed(true).completionTime(completionTime).dueDate(dueDate));

        var result = mapper.toModel(entity);

        assertThat(result)
            .isEqualTo(
                new CompletedTask(
                    UUID.fromString(entity.id()),
                    entity.description(),
                    Optional.of(dueDate),
                    completionTime
                )
            );
    }
}
