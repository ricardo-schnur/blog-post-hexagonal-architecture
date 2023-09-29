package de.colenet.hexagonal.todo.list.adapter.cache;

import static de.colenet.hexagonal.todo.list.domain.model.task.TaskOpenTaskBuilder.from;
import static de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Sets;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class TaskCacheTest {

    private final TaskCache taskCache = new TaskCache();

    @Test
    void save_TaskWithIdDoesNotExist_CreatesTask() {
        assertThat(taskCache.getAll()).isEmpty();

        Task task = createTask();
        taskCache.save(task);

        assertThat(taskCache.getAll()).containsExactly(task);
    }

    @Test
    void save_TaskWithIdAlreadyExist_UpdatesTask() {
        assertThat(taskCache.getAll()).isEmpty();

        Task.OpenTask task = createOpenTaskBuilder().withDescription("Old description");
        taskCache.save(task);

        assertThat(taskCache.getAll()).containsExactly(task);

        Task updatedTask = from(task).withDescription("New description");
        taskCache.save(updatedTask);

        assertThat(taskCache.getAll()).containsExactly(updatedTask);
    }

    @Test
    void find_TaskDoesNotExist_ReturnsEmpty() {
        UUID idToBeFound = UUID.randomUUID();
        UUID idOfOtherTask = UUID.randomUUID();
        taskCache.save(createOpenTaskBuilder().withId(idOfOtherTask));

        var result = taskCache.find(idToBeFound);

        assertThat(result).isEmpty();
    }

    @Test
    void find_TaskDoesExist_ReturnsTask() {
        UUID idToBeFound = UUID.randomUUID();
        Task task = createOpenTaskBuilder().withId(idToBeFound);
        taskCache.save(task);

        var result = taskCache.find(idToBeFound);

        assertThat(result).contains(task);
    }

    @Test
    void getAll_NothingSaved_ReturnsEmptyList() {
        assertThat(taskCache.getAll()).isEmpty();
    }

    @Test
    void getAll_MultipleTasksSaved_ReturnsAllTasks() {
        List<Task> tasks = Stream.generate(TaskTestdataFactory::createTask).limit(4L).toList();
        tasks.forEach(taskCache::save);

        var result = taskCache.getAll();

        assertThat(result).containsExactlyInAnyOrderElementsOf(tasks);
    }

    @Test
    void getAllOpenTasksWithDueDateBeforeOrEqual_ReturnsMatchingTasks() {
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
        Sets.union(tasksToBeFound, tasksNotToBeFound).forEach(taskCache::save);

        var result = taskCache.getAllOpenTasksWithDueDateBeforeOrEqual(date);

        assertThat(result).containsExactlyInAnyOrderElementsOf(tasksToBeFound);
    }
}
