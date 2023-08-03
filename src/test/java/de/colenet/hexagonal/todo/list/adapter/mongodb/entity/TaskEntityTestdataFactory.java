package de.colenet.hexagonal.todo.list.adapter.mongodb.entity;

import static com.tngtech.valueprovider.ValueProviderFactory.createRandomValueProvider;

import com.tngtech.valueprovider.ValueProvider;
import java.time.LocalDateTime;

public final class TaskEntityTestdataFactory {

    private TaskEntityTestdataFactory() {}

    public static TaskEntityBuilder.With createTaskEntityBuilder() {
        return TaskEntityBuilder.from(createTaskEntity());
    }

    public static TaskEntity createTaskEntity() {
        return createTaskEntity(createRandomValueProvider());
    }

    private static TaskEntity createTaskEntity(ValueProvider values) {
        boolean completed = values.booleanValue();
        return TaskEntityBuilder
            .builder()
            .id(values.uuid().toString())
            .description(values.fixedDecoratedString("Description-"))
            .dueDate(values.booleanValue() ? values.localDateBetweenYears(2000, 2100) : null)
            .completed(completed)
            .completionTime(
                completed ? LocalDateTime.of(values.localDateBetweenYears(2000, 2100), values.localTime()) : null
            )
            .build();
    }
}
