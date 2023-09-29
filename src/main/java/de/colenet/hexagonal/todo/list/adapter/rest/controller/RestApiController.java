package de.colenet.hexagonal.todo.list.adapter.rest.controller;

import de.colenet.hexagonal.todo.list.adapter.rest.mapper.RestApiMapper;
import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
class RestApiController {

    private final RestApiMapper restApiMapper;
    private final TaskService taskService;

    RestApiController(RestApiMapper restApiMapper, TaskService taskService) {
        this.restApiMapper = restApiMapper;
        this.taskService = taskService;
    }

    @GetMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<TaskDto>> getAllTasks() {
        return createOkResponse(taskService.getAllTasks().stream().map(restApiMapper::toDto).toList());
    }

    @PostMapping(value = "/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createTask(@RequestParam String description) {
        return createOkResponse(restApiMapper.toDto(taskService.createTask(description)));
    }

    @PostMapping(value = "/tasks/toggle-completion/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> toggleCompletionState(@PathVariable String id) {
        return taskService
            .toggleCompletionState(UUID.fromString(id))
            .map(restApiMapper::toDto)
            .map(RestApiController::createOkResponse)
            .orElseGet(RestApiController::createBadRequestResponse);
    }

    private static <T> ResponseEntity<T> createOkResponse(T value) {
        return new ResponseEntity<>(value, HttpStatus.OK);
    }

    private static <T> ResponseEntity<T> createBadRequestResponse() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
