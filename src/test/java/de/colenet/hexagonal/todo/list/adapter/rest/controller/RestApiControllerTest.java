package de.colenet.hexagonal.todo.list.adapter.rest.controller;

import static de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDtoTestdataFactory.createTaskDto;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.createTask;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.colenet.hexagonal.todo.list.adapter.rest.mapper.RestApiMapper;
import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.adapter.rest.validator.RestApiValidator;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskService;
import io.vavr.control.Validation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RestApiControllerTest {

    @Mock
    private RestApiMapper restApiMapper;

    @Mock
    private RestApiValidator restApiValidator;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private RestApiController restApiController;

    @Test
    void getAllTasks_CallsService_ReturnsMappedResultsFromService() {
        Task task1 = createTask();
        Task task2 = createTask();
        TaskDto mappedTask1 = createTaskDto();
        TaskDto mappedTask2 = createTaskDto();

        when(taskService.getAllTasks()).thenReturn(List.of(task1, task2));
        when(restApiMapper.toDto(task1)).thenReturn(mappedTask1);
        when(restApiMapper.toDto(task2)).thenReturn(mappedTask2);

        var result = restApiController.getAllTasks();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactlyInAnyOrder(mappedTask1, mappedTask2);
    }

    @Test
    void createTask_DescriptionInvalid_ReturnsBadRequestWithErrorMessage() {
        String description = "   ";

        when(restApiValidator.validateDescription(description)).thenReturn(Validation.invalid("Invalid description"));

        var result = restApiController.createTask(description);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo("Invalid description");
    }

    @Test
    void createTask_ParametersValid_CallsServiceAndReturnsCreatedTask() {
        String description = "Some task";
        Task createdTask = createTask();
        TaskDto mappedTask = createTaskDto();

        when(restApiValidator.validateDescription(description)).thenReturn(Validation.valid(description));
        when(taskService.createTask(description)).thenReturn(createdTask);
        when(restApiMapper.toDto(createdTask)).thenReturn(mappedTask);

        var result = restApiController.createTask(description);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mappedTask);
    }

    @Test
    void toggleCompletionState_ParametersInvalid_ReturnsBadRequestWithErrorMessage() {
        String id = "Not a valid id";
        String errorMessage = "Invalid id";

        when(restApiValidator.validateId(id)).thenReturn(Validation.invalid(errorMessage));

        var result = restApiController.toggleCompletionState(id);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(errorMessage);
    }

    @Test
    void toggleCompletionState_TaskNotFound_ReturnsBadRequestWithNotFoundErrorMessage() {
        UUID id = UUID.randomUUID();
        String idString = id.toString();

        when(restApiValidator.validateId(idString)).thenReturn(Validation.valid(id));
        when(taskService.toggleCompletionState(id)).thenReturn(Optional.empty());

        var result = restApiController.toggleCompletionState(idString);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo("No task found for id: " + idString);
    }

    @Test
    void toggleCompletionState_TaskFound_CallsServiceAndReturnsUpdatedTask() {
        UUID id = UUID.randomUUID();
        String idString = id.toString();
        Task createdTask = createTask();
        TaskDto mappedTask = createTaskDto();

        when(restApiValidator.validateId(idString)).thenReturn(Validation.valid(id));
        when(taskService.toggleCompletionState(id)).thenReturn(Optional.of(createdTask));
        when(restApiMapper.toDto(createdTask)).thenReturn(mappedTask);

        var result = restApiController.toggleCompletionState(idString);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(mappedTask);
    }
}
