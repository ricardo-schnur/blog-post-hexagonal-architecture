package de.colenet.hexagonal.todo.list.adapter.rest.mapper;

import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createCompletedTaskBuilder;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createOpenTaskBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RestApiMapperTest {

    private final RestApiMapper mapper = new RestApiMapper();

    @Test
    void toDto_MapsOpenTaskAsExpected() {
        LocalDate dueDate = LocalDate.of(2023, 7, 1);
        Task model = createOpenTaskBuilder().withDueDate(Optional.of(dueDate));

        var result = mapper.toDto(model);

        assertThat(result)
            .isEqualTo(new TaskDto(model.id().toString(), model.description(), "2023-07-01", "open", null));
    }

    @Test
    void toDto_MapsCompletedTaskAsExpected() {
        LocalDate dueDate = LocalDate.of(2023, 7, 1);
        String completionTime = "2023-07-05T10:57:53.539959155";
        Task model = createCompletedTaskBuilder()
            .with(t -> t.dueDate(Optional.of(dueDate)).completionTime(LocalDateTime.parse(completionTime)));

        var result = mapper.toDto(model);

        assertThat(result)
            .isEqualTo(
                new TaskDto(model.id().toString(), model.description(), "2023-07-01", "completed", completionTime)
            );
    }
}
