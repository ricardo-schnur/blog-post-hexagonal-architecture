package de.colenet.hexagonal.todo.list.end2end;

import static org.assertj.core.api.Assertions.assertThat;

import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import io.vavr.Function2;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HexagonalToDoListApplicationEndToEndTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void returnsEmptyListIfNoTasksHaveBeenCreated() {
        var result = getAllTasks();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
    }

    @Test
    void creatingTasksIsPossibleAndCreatedTasksAreReturnedFromGetAll() {
        List<Tuple2<String, String>> descriptionsAndDueDates = List.of(
            Tuple.of("Some description", "2023-07-11"),
            Tuple.of("Some other description", null),
            Tuple.of("Task", "2023-07-12"),
            Tuple.of("Task with same due date", "2023-07-12")
        );
        descriptionsAndDueDates.forEach(Function2.of(this::createTask).tupled()::apply);

        var result = getAllTasks();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
            .containsExactlyInAnyOrder(
                new TaskDto(null, "Some description", "2023-07-11", "open", null),
                new TaskDto(null, "Some other description", null, "open", null),
                new TaskDto(null, "Task", "2023-07-12", "open", null),
                new TaskDto(null, "Task with same due date", "2023-07-12", "open", null)
            );
        assertThat(result.getBody()).extracting(TaskDto::id).extracting(UUID::fromString).doesNotContainNull();
    }

    @Test
    void completionStateOfTasksCanBeToggledAndTogglingOnlyChangesStateAndCompletionTime() {
        var createdTask = createTask("Some description", "2023-07-11").getBody();
        assertThat(createdTask.state()).isEqualTo("open");

        var id = createdTask.id();

        var toggledTask = toggleTask(id).getBody();
        assertThat(toggledTask)
            .usingRecursiveComparison()
            .ignoringFields("state", "completionTime")
            .isEqualTo(createdTask);
        assertThat(toggledTask.state()).isEqualTo("completed");
        assertThat(toggledTask.completionTime()).isNotNull();

        var toggledTwiceTask = toggleTask(id).getBody();
        assertThat(toggledTwiceTask).isEqualTo(createdTask);

        var toggledThriceTask = toggleTask(id).getBody();
        assertThat(toggledThriceTask)
            .usingRecursiveComparison()
            .ignoringFields("completionTime")
            .isEqualTo(toggledTask);
        assertThat(LocalDateTime.parse(toggledThriceTask.completionTime()))
            .isAfter(LocalDateTime.parse(toggledTask.completionTime()));
    }

    private ResponseEntity<List<TaskDto>> getAllTasks() {
        return restTemplate.exchange("/tasks", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    private ResponseEntity<TaskDto> createTask(String description, String dueDate) {
        return restTemplate.postForEntity(
            UriComponentsBuilder
                .fromPath("/tasks")
                .queryParam("description", description)
                .queryParam("dueDate", dueDate)
                .build()
                .toUri(),
            null,
            TaskDto.class
        );
    }

    private ResponseEntity<TaskDto> toggleTask(String id) {
        return restTemplate.postForEntity("/tasks/toggle-completion/{id}", null, TaskDto.class, Map.of("id", id));
    }
}
