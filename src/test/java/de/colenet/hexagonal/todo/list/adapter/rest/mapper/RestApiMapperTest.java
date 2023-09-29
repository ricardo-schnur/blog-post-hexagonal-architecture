package de.colenet.hexagonal.todo.list.adapter.rest.mapper;

import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createCompletedTaskBuilder;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createOpenTask;
import static org.assertj.core.api.Assertions.assertThat;

import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class RestApiMapperTest {

    private final RestApiMapper mapper = new RestApiMapper();

    @Test
    void toDto_MapsOpenTaskAsExpected() {
        Task model = createOpenTask();

        var result = mapper.toDto(model);

        assertThat(result).isEqualTo(new TaskDto(model.id().toString(), model.description(), "open", null));
    }

    @Test
    void toDto_MapsCompletedTaskAsExpected() {
        String completionTime = "2023-07-05T10:57:53.539959155";
        Task model = createCompletedTaskBuilder().withCompletionTime(LocalDateTime.parse(completionTime));

        var result = mapper.toDto(model);

        assertThat(result)
            .isEqualTo(new TaskDto(model.id().toString(), model.description(), "completed", completionTime));
    }
}
