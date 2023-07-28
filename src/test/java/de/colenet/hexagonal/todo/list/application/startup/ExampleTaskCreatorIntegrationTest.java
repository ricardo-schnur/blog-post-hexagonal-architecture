package de.colenet.hexagonal.todo.list.application.startup;

import static org.assertj.core.api.Assertions.assertThat;

import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class ExampleTaskCreatorIntegrationTest {

    @Nested
    @SpringBootTest(properties = "startup.exampletasks.create=true")
    class CreatorIsEnabled {

        @Autowired
        private TaskRepository taskRepository;

        @Test
        void exampleTasksAreCreated() {
            assertThat(taskRepository.getAll())
                .hasSize(6)
                .extracting(Task::description)
                .allMatch(description -> description.startsWith("[EXAMPLE]"));
        }
    }

    @Nested
    @SpringBootTest(properties = "startup.exampletasks.create=false")
    class CreatorIsDisabled {

        @Autowired
        private TaskRepository taskRepository;

        @Test
        void exampleTasksAreNotCreated() {
            assertThat(taskRepository.getAll()).isEmpty();
        }
    }
}
