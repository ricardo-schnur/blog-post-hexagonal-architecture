package de.colenet.hexagonal.todo.list.adapter.rest.controller;

import static de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import static de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createOpenTaskBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskService;
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
                LocalDateTime.of(2023, 7, 11, 9, 30)
            ),
            new OpenTask(UUID.fromString("71195625-2414-ad41-4dce-a9dfc6cf4c38"), "This task is open")
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
                    "completed",
                    "2023-07-11T09:30"
                ),
                new TaskDto("71195625-2414-ad41-4dce-a9dfc6cf4c38", "This task is open", "open", null)
            );
    }

    @Test
    void createTask_CreatesTaskViaService_ReturnsCreatedTasks() {
        String description = "Some description";
        OpenTask createdTask = createOpenTaskBuilder().withDescription(description);

        when(taskService.createTask(description)).thenReturn(createdTask);

        var result = restTemplate.postForEntity(
            UriComponentsBuilder.fromPath("/tasks").queryParam("description", description).build().toUri(),
            null,
            TaskDto.class
        );

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new TaskDto(createdTask.id().toString(), description, "open", null));
    }

    @Test
    void toggleCompletionState_TaskNotFound_ReturnsBadRequest() {
        String id = "71195625-2414-ad41-4dce-a9dfc6cf4c38";

        when(taskService.toggleCompletionState(UUID.fromString(id))).thenReturn(Optional.empty());

        var result = restTemplate.postForEntity("/tasks/toggle-completion/{id}", null, String.class, Map.of("id", id));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void toggleCompletionState_UpdatesTaskViaService_ReturnsUpdatesTasks() {
        String id = "71195625-2414-ad41-4dce-a9dfc6cf4c38";
        String description = "Some description";
        Task updatedTask = createOpenTaskBuilder().with(t -> t.id(UUID.fromString(id)).description(description));

        when(taskService.toggleCompletionState(UUID.fromString(id))).thenReturn(Optional.of(updatedTask));

        var result = restTemplate.postForEntity("/tasks/toggle-completion/{id}", null, TaskDto.class, Map.of("id", id));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(new TaskDto(updatedTask.id().toString(), description, "open", null));
    }
}
