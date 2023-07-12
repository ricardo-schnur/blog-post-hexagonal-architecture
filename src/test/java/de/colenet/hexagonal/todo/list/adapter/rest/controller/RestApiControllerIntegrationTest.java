package de.colenet.hexagonal.todo.list.adapter.rest.controller;

import static de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import static de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createOpenTaskBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiControllerIntegrationTest {

    @MockBean
    private TaskService taskService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getAllTasks_GetsTasksFromService_ReturnsMappedTasks() {
        List<Task> tasks = List.of(
            new CompletedTask(
                UUID.fromString("2694d026-dab7-1b4e-5fdb-1ba906d43565"),
                "This task is completed",
                Optional.of(LocalDate.of(2023, 7, 11)),
                LocalDateTime.of(2023, 7, 11, 9, 30)
            ),
            new OpenTask(UUID.fromString("71195625-2414-ad41-4dce-a9dfc6cf4c38"), "This task is open", Optional.empty())
        );

        when(taskService.getAllTasks()).thenReturn(tasks);

        var result = restTemplate.exchange(
            "/tasks",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<TaskDto>>() {}
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
            .containsExactlyInAnyOrder(
                new TaskDto(
                    "2694d026-dab7-1b4e-5fdb-1ba906d43565",
                    "This task is completed",
                    "2023-07-11",
                    "completed",
                    "2023-07-11T09:30"
                ),
                new TaskDto("71195625-2414-ad41-4dce-a9dfc6cf4c38", "This task is open", null, "open", null)
            );
    }

    @Test
    void createTask_InvalidParameters_ReturnsBadRequest() {
        String description = "  ";
        String dueDate = "2023-07-111";

        var result = restTemplate.postForEntity(
            UriComponentsBuilder
                .fromPath("/tasks")
                .queryParam("description", description)
                .queryParam("dueDate", dueDate)
                .build()
                .toUri(),
            null,
            String.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody())
            .isEqualTo("Description is mandatory; Due date has to be in format yyyy-MM-dd: 2023-07-111");
    }

    @Test
    void createTask_CreatesTaskViaService_ReturnsCreatedTasks() {
        String description = "Some description";
        String dueDateString = "2023-07-11";
        LocalDate dueDate = LocalDate.of(2023, 7, 11);
        OpenTask createdTask = createOpenTaskBuilder()
            .with(t -> t.description(description).dueDate(Optional.of(dueDate)));

        when(taskService.createTask(description, Optional.of(dueDate))).thenReturn(createdTask);

        var result = restTemplate.postForEntity(
            UriComponentsBuilder
                .fromPath("/tasks")
                .queryParam("description", description)
                .queryParam("dueDate", dueDateString)
                .build()
                .toUri(),
            null,
            TaskDto.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
            .isEqualTo(new TaskDto(createdTask.id().toString(), description, dueDateString, "open", null));
    }

    @Test
    void toggleCompletionState_InvalidParameters_ReturnsBadRequest() {
        String id = "Something that is not a UUID";

        var result = restTemplate.postForEntity("/tasks/toggle-completion/{id}", null, String.class, Map.of("id", id));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo("Not a valid UUID: Something that is not a UUID");
    }

    @Test
    void toggleCompletionState_TaskNotFound_ReturnsBadRequest() {
        String id = "71195625-2414-ad41-4dce-a9dfc6cf4c38";

        when(taskService.toggleCompletionState(UUID.fromString(id))).thenReturn(Optional.empty());

        var result = restTemplate.postForEntity("/tasks/toggle-completion/{id}", null, String.class, Map.of("id", id));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo("No task found for id: 71195625-2414-ad41-4dce-a9dfc6cf4c38");
    }

    @Test
    void toggleCompletionState_UpdatesTaskViaService_ReturnsUpdatesTasks() {
        String id = "71195625-2414-ad41-4dce-a9dfc6cf4c38";
        String description = "Some description";
        String dueDateString = "2023-07-11";
        Task updatedTask = createOpenTaskBuilder()
            .with(t ->
                t.id(UUID.fromString(id)).description(description).dueDate(Optional.of(LocalDate.parse(dueDateString)))
            );

        when(taskService.toggleCompletionState(UUID.fromString(id))).thenReturn(Optional.of(updatedTask));

        var result = restTemplate.postForEntity("/tasks/toggle-completion/{id}", null, TaskDto.class, Map.of("id", id));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
            .isEqualTo(new TaskDto(updatedTask.id().toString(), description, dueDateString, "open", null));
    }
}
