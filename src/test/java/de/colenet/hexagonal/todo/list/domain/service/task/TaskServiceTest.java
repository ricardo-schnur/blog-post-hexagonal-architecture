package de.colenet.hexagonal.todo.list.domain.service.task;

import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now();

    private final Clock clock = Clock.fixed(CURRENT_TIME.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @BeforeEach
    void setup() {
        taskService = new TaskService(clock, taskRepository);
    }

    @Test
    void createTask_CreatedOpenTaskWithRandomUuid_CallsRepositoryAndReturnsSavedTask() {
        String description = "Some description";
        Task createdTaskFromRepository = createTask();
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        when(taskRepository.save(captor.capture())).thenReturn(createdTaskFromRepository);

        var result = taskService.createTask(description);

        assertThat(result).isEqualTo(createdTaskFromRepository);
        assertThat(captor.getValue())
            .isInstanceOfSatisfying(
                OpenTask.class,
                task -> {
                    assertThat(task.id()).isNotNull();
                    assertThat(task.description()).isEqualTo(description);
                }
            );
    }

    @Test
    void getAllTasks_CallsRepository_ReturnsResultFromRepository() {
        List<Task> tasksFromRepository = Stream.generate(TaskTestdataFactory::createTask).limit(4L).toList();

        when(taskRepository.getAll()).thenReturn(tasksFromRepository);

        var result = taskService.getAllTasks();

        assertThat(result).containsExactlyElementsOf(tasksFromRepository);
    }

    @Test
    void toggleCompletionState_TaskNotFoundInRepository_ReturnsEmpty() {
        UUID id = UUID.randomUUID();

        when(taskRepository.find(id)).thenReturn(Optional.empty());

        var result = taskService.toggleCompletionState(id);

        assertThat(result).isEmpty();
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void toggleCompletionState_RepositoryReturnsCompletedTask_SavesAndReturnsOpenTask() {
        UUID id = UUID.randomUUID();
        CompletedTask foundTaskFromRepository = createCompletedTask();
        Task savedTaskFromRepository = createTask();
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        when(taskRepository.find(id)).thenReturn(Optional.of(foundTaskFromRepository));
        when(taskRepository.save(captor.capture())).thenReturn(savedTaskFromRepository);

        var result = taskService.toggleCompletionState(id);

        assertThat(result).contains(savedTaskFromRepository);
        assertThat(captor.getValue())
            .isInstanceOfSatisfying(
                OpenTask.class,
                task -> assertThat(task).usingRecursiveComparison().isEqualTo(foundTaskFromRepository)
            );
    }

    @Test
    void toggleCompletionState_RepositoryReturnsOpenTask_SavesAndReturnsCompletedTask() {
        UUID id = UUID.randomUUID();
        OpenTask foundTaskFromRepository = createOpenTask();
        Task savedTaskFromRepository = createTask();
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);

        when(taskRepository.find(id)).thenReturn(Optional.of(foundTaskFromRepository));
        when(taskRepository.save(captor.capture())).thenReturn(savedTaskFromRepository);

        var result = taskService.toggleCompletionState(id);

        assertThat(result).contains(savedTaskFromRepository);
        assertThat(captor.getValue())
            .isInstanceOfSatisfying(
                CompletedTask.class,
                task -> {
                    assertThat(task)
                        .usingRecursiveComparison()
                        .ignoringFields("completionTime")
                        .isEqualTo(foundTaskFromRepository);
                    assertThat(task.completionTime()).isEqualTo(CURRENT_TIME);
                }
            );
    }
}
