package de.colenet.hexagonal.todo.list.adapter.rest.controller;

import de.colenet.hexagonal.todo.list.adapter.rest.mapper.RestApiMapper;
import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.adapter.rest.validator.RestApiValidator;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskService;
import io.vavr.Function2;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
class RestApiController {

    private final RestApiMapper restApiMapper;
    private final RestApiValidator restApiValidator;
    private final TaskService taskService;

    RestApiController(RestApiMapper restApiMapper, RestApiValidator restApiValidator, TaskService taskService) {
        this.restApiMapper = restApiMapper;
        this.restApiValidator = restApiValidator;
        this.taskService = taskService;
    }

    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TaskDto>> getAllTasks() {
        return createOkResponse(taskService.getAllTasks().stream().map(restApiMapper::toDto).toList());
    }

    @PostMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createTask(@RequestParam String description, @RequestParam @Nullable String dueDate) {
        return restApiValidator
            .validateCreateTaskParameters(description, dueDate)
            .map(Function2.of(taskService::createTask).tupled())
            .map(restApiMapper::toDto)
            .mapError(messages -> String.join("; ", messages))
            .fold(RestApiController::createBadRequestResponse, RestApiController::createOkResponse);
    }

    @PostMapping(value = "/tasks/toggle-completion/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> toggleCompletionState(@PathVariable String id) {
        return restApiValidator
            .validateId(id)
            .map(taskService::toggleCompletionState)
            .fold(
                RestApiController::createBadRequestResponse,
                toggledTask ->
                    toggledTask
                        .map(restApiMapper::toDto)
                        .<ResponseEntity<?>>map(RestApiController::createOkResponse)
                        .orElseGet(() -> createBadRequestResponse("No task found for id: " + id))
            );
    }

    private static <T> ResponseEntity<T> createOkResponse(T value) {
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    private static ResponseEntity<String> createBadRequestResponse(String message) {
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
