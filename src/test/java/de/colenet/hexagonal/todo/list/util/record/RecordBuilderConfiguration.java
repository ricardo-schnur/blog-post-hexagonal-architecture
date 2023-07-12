package de.colenet.hexagonal.todo.list.util.record;

import de.colenet.hexagonal.todo.list.adapter.mongodb.entity.TaskEntity;
import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder.Include(
    value = { CompletedTask.class, OpenTask.class, TaskDto.class, TaskEntity.class },
    packagePattern = "*"
)
public class RecordBuilderConfiguration {}
