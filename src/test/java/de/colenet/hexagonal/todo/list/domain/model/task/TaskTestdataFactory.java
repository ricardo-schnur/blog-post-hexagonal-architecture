package de.colenet.hexagonal.todo.list.domain.model.task;

import static com.tngtech.valueprovider.ValueProviderFactory.createRandomValueProvider;

import com.tngtech.valueprovider.ValueProvider;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import java.time.LocalDateTime;
import java.util.Optional;

public final class TaskTestdataFactory {

    private TaskTestdataFactory() {}

    public static Task createTask() {
        return createTask(createRandomValueProvider());
    }

    private static Task createTask(ValueProvider values) {
        return values.booleanValue() ? createCompletedTask() : createOpenTask();
    }

    public static TaskOpenTaskBuilder.With createOpenTaskBuilder() {
        return TaskOpenTaskBuilder.from(createOpenTask());
    }

    public static OpenTask createOpenTask() {
        return createOpenTask(createRandomValueProvider());
    }

    private static OpenTask createOpenTask(ValueProvider values) {
        return TaskOpenTaskBuilder
            .builder()
            .id(values.uuid())
            .description(values.fixedDecoratedString("Description-"))
            .dueDate(values.booleanValue() ? Optional.of(values.localDateBetweenYears(2000, 2100)) : Optional.empty())
            .build();
    }

    public static TaskCompletedTaskBuilder.With createCompletedTaskBuilder() {
        return TaskCompletedTaskBuilder.from(createCompletedTask());
    }

    public static CompletedTask createCompletedTask() {
        return createCompletedTask(createRandomValueProvider());
    }

    private static CompletedTask createCompletedTask(ValueProvider values) {
        return TaskCompletedTaskBuilder
            .builder()
            .id(values.uuid())
            .description(values.fixedDecoratedString("Description-"))
            .dueDate(values.booleanValue() ? Optional.of(values.localDateBetweenYears(2000, 2100)) : Optional.empty())
            .completionTime(LocalDateTime.of(values.localDateBetweenYears(2000, 2100), values.localTime()))
            .build();
    }
}
