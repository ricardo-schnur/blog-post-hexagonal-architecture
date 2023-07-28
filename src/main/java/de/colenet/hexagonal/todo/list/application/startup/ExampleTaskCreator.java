package de.colenet.hexagonal.todo.list.application.startup;

import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "startup", name = "exampletasks.create", havingValue = "true")
class ExampleTaskCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleTaskCreator.class);

    private final TaskRepository taskRepository;

    ExampleTaskCreator(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createExampleTasks() {
        LOGGER.info("Creating some example tasks");

        Stream
            .of(
                new CompletedTask(
                    UUID.randomUUID(),
                    "[EXAMPLE] This task was completed yesterday",
                    Optional.empty(),
                    LocalDateTime.now().minusDays(1L)
                ),
                new CompletedTask(
                    UUID.randomUUID(),
                    "[EXAMPLE] This task was completed after its due date... :(",
                    Optional.of(LocalDate.now().minusDays(1L)),
                    LocalDateTime.now()
                ),
                new OpenTask(UUID.randomUUID(), "[EXAMPLE] This task is due today", Optional.of(LocalDate.now())),
                new OpenTask(
                    UUID.randomUUID(),
                    "[EXAMPLE] This task was due yesterday",
                    Optional.of(LocalDate.now().minusDays(1L))
                ),
                new OpenTask(
                    UUID.randomUUID(),
                    "[EXAMPLE] This task is due tomorrow",
                    Optional.of(LocalDate.now().plusDays(1L))
                ),
                new OpenTask(UUID.randomUUID(), "[EXAMPLE] This task has no due date", Optional.empty())
            )
            .forEach(taskRepository::save);

        LOGGER.info("Example tasks successfully created");
    }
}
