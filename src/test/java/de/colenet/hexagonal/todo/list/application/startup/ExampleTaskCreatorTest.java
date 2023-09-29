package de.colenet.hexagonal.todo.list.application.startup;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExampleTaskCreatorTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ExampleTaskCreator exampleTaskCreator;

    @Test
    void createExampleTasks_DefinesSixTasks_CallsSaveForEachOfThem() {
        exampleTaskCreator.createExampleTasks();

        verify(taskRepository, times(6)).save(any(Task.class));
    }
}
