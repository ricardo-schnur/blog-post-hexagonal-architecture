package de.colenet.hexagonal.todo.list.adapter.cache;

import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepository;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepositoryTestProvider;
import java.util.function.Consumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

class TaskCacheTest {

    private final TaskCache taskCache = new TaskCache();

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(TaskRepositoryTestProvider.class)
    void runAllTestsFromTaskRepositoryTestProvider(Consumer<TaskRepository> test, String description) {
        test.accept(taskCache);
    }
}
