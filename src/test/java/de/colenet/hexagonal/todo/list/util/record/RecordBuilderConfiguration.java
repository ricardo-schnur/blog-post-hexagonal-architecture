package de.colenet.hexagonal.todo.list.util.record;

import de.colenet.hexagonal.todo.list.domain.model.task.Task.CompletedTask;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder.Include(value = { CompletedTask.class, OpenTask.class }, packagePattern = "*")
public class RecordBuilderConfiguration {}
