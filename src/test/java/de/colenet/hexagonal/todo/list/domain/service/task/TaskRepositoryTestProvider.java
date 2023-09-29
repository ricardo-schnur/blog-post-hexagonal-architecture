package de.colenet.hexagonal.todo.list.domain.service.task;

import static de.colenet.hexagonal.todo.list.domain.model.task.TaskOpenTaskBuilder.from;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Sets;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public final class TaskRepositoryTestProvider implements ArgumentsProvider {

    private TaskRepositoryTestProvider() {}

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream
            .<Tuple2<Consumer<TaskRepository>, String>>of(
                Tuple.of(
                    TaskRepositoryTestProvider::save_TaskWithIdDoesNotExist_CreatesTask,
                    "save_TaskWithIdDoesNotExist_CreatesTask"
                ),
                Tuple.of(
                    TaskRepositoryTestProvider::save_TaskWithIdAlreadyExist_UpdatesTask,
                    "save_TaskWithIdAlreadyExist_UpdatesTask"
                ),
                Tuple.of(
                    TaskRepositoryTestProvider::find_TaskDoesNotExist_ReturnsEmpty,
                    "find_TaskDoesNotExist_ReturnsEmpty"
                ),
                Tuple.of(TaskRepositoryTestProvider::find_TaskDoesExist_ReturnsTask, "find_TaskDoesExist_ReturnsTask"),
                Tuple.of(
                    TaskRepositoryTestProvider::getAll_NothingSaved_ReturnsEmptyList,
                    "getAll_NothingSaved_ReturnsEmptyList"
                ),
                Tuple.of(
                    TaskRepositoryTestProvider::getAll_MultipleTasksSaved_ReturnsAllTasks,
                    "getAll_MultipleTasksSaved_ReturnsAllTasks"
                ),
                Tuple.of(
                    TaskRepositoryTestProvider::getAllOpenTasksWithDueDateBeforeOrEqual_ReturnsMatchingTasks,
                    "getAllOpenTasksWithDueDateBeforeOrEqual_ReturnsMatchingTasks"
                )
            )
            .map(tuple -> tuple.apply(Arguments::of));
    }

    private static void save_TaskWithIdDoesNotExist_CreatesTask(TaskRepository taskRepository) {
        assertThat(taskRepository.getAll()).isEmpty();

        Task task = createTask();
        taskRepository.save(task);

        assertThat(taskRepository.getAll()).containsExactly(task);
    }

    private static void save_TaskWithIdAlreadyExist_UpdatesTask(TaskRepository taskRepository) {
        assertThat(taskRepository.getAll()).isEmpty();

        Task.OpenTask task = createOpenTaskBuilder().withDescription("Old description");
        taskRepository.save(task);

        assertThat(taskRepository.getAll()).containsExactly(task);

        Task updatedTask = from(task).withDescription("New description");
        taskRepository.save(updatedTask);

        assertThat(taskRepository.getAll()).containsExactly(updatedTask);
    }

    private static void find_TaskDoesNotExist_ReturnsEmpty(TaskRepository taskRepository) {
        UUID idToBeFound = UUID.randomUUID();
        UUID idOfOtherTask = UUID.randomUUID();
        taskRepository.save(createOpenTaskBuilder().withId(idOfOtherTask));

        var result = taskRepository.find(idToBeFound);

        assertThat(result).isEmpty();
    }

    private static void find_TaskDoesExist_ReturnsTask(TaskRepository taskRepository) {
        UUID idToBeFound = UUID.randomUUID();
        Task task = createOpenTaskBuilder().withId(idToBeFound);
        taskRepository.save(task);

        var result = taskRepository.find(idToBeFound);

        assertThat(result).contains(task);
    }

    private static void getAll_NothingSaved_ReturnsEmptyList(TaskRepository taskRepository) {
        assertThat(taskRepository.getAll()).isEmpty();
    }

    private static void getAll_MultipleTasksSaved_ReturnsAllTasks(TaskRepository taskRepository) {
        List<Task> tasks = Stream.generate(TaskTestdataFactory::createTask).limit(4L).toList();
        tasks.forEach(taskRepository::save);

        var result = taskRepository.getAll();

        assertThat(result).containsExactlyInAnyOrderElementsOf(tasks);
    }

    private static void getAllOpenTasksWithDueDateBeforeOrEqual_ReturnsMatchingTasks(TaskRepository taskRepository) {
        LocalDate date = LocalDate.now();
        Set<Task> tasksNotToBeFound = Sets.union(
            Set.of(
                createOpenTaskBuilder().withDueDate(Optional.of(date.plusDays(1L))),
                createOpenTaskBuilder().withDueDate(Optional.empty()),
                createCompletedTaskBuilder().withDueDate(Optional.of(date.plusDays(1L))),
                createCompletedTaskBuilder().withDueDate(Optional.of(date)),
                createCompletedTaskBuilder().withDueDate(Optional.of(date.minusDays(1L))),
                createCompletedTaskBuilder().withDueDate(Optional.empty())
            ),
            Stream.generate(TaskTestdataFactory::createCompletedTask).limit(100L).collect(Collectors.toSet())
        );
        Set<Task.OpenTask> tasksToBeFound = Set.of(
            createOpenTaskBuilder().withDueDate(Optional.of(date)),
            createOpenTaskBuilder().withDueDate(Optional.of(date.minusDays(1L)))
        );
        Sets.union(tasksToBeFound, tasksNotToBeFound).forEach(taskRepository::save);

        var result = taskRepository.getAllOpenTasksWithDueDateBeforeOrEqual(date);

        assertThat(result).containsExactlyInAnyOrderElementsOf(tasksToBeFound);
    }
}
