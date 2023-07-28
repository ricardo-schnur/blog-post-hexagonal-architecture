package de.colenet.hexagonal.todo.list.adapter.rest.model;

import static com.tngtech.valueprovider.ValueProviderFactory.createRandomValueProvider;

import com.tngtech.valueprovider.ValueProvider;
import java.time.LocalDateTime;

public final class TaskDtoTestdataFactory {

    private TaskDtoTestdataFactory() {}

    public static TaskDtoBuilder.With createTaskDtoBuilder() {
        return TaskDtoBuilder.from(createTaskDto());
    }

    public static TaskDto createTaskDto() {
        return createTaskDto(createRandomValueProvider());
    }

    private static TaskDto createTaskDto(ValueProvider values) {
        boolean completed = values.booleanValue();
        return TaskDtoBuilder
            .builder()
            .id(values.uuid().toString())
            .description(values.fixedDecoratedString("Description-"))
            .dueDate(values.booleanValue() ? values.localDateBetweenYears(2000, 2100).toString() : null)
            .state(completed ? "completed" : "open")
            .completionTime(
                completed
                    ? LocalDateTime.of(values.localDateBetweenYears(2000, 2100), values.localTime()).toString()
                    : null
            )
            .build();
    }
}
