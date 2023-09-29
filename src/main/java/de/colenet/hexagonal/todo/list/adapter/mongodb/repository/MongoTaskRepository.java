package de.colenet.hexagonal.todo.list.adapter.mongodb.repository;

import de.colenet.hexagonal.todo.list.adapter.mongodb.mapper.MongoMapper;
import de.colenet.hexagonal.todo.list.domain.model.task.Task;
import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepository;
import io.vavr.Function1;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
class MongoTaskRepository implements TaskRepository {

    private final BaseMongoTaskRepository baseMongoTaskRepository;
    private final MongoMapper mongoMapper;

    MongoTaskRepository(BaseMongoTaskRepository baseMongoTaskRepository, MongoMapper mongoMapper) {
        this.baseMongoTaskRepository = baseMongoTaskRepository;
        this.mongoMapper = mongoMapper;
    }

    @Override
    public Task save(Task task) {
        return Function1
            .of(mongoMapper::toEntity)
            .andThen(baseMongoTaskRepository::save)
            .andThen(mongoMapper::toModel)
            .apply(task);
    }

    @Override
    public Optional<Task> find(UUID id) {
        return baseMongoTaskRepository.findById(id.toString()).map(mongoMapper::toModel);
    }

    @Override
    public List<Task> getAll() {
        return baseMongoTaskRepository.findAll().stream().map(mongoMapper::toModel).toList();
    }

    @Override
    public List<OpenTask> getAllOpenTasksWithDueDateBeforeOrEqual(LocalDate date) {
        return baseMongoTaskRepository
            .findByDueDateIsBeforeOrDueDateEquals(date)
            .stream()
            .map(mongoMapper::toModel)
            // Should probably be filtered in the database call in production applications!
            .filter(OpenTask.class::isInstance)
            .map(OpenTask.class::cast)
            .toList();
    }
}
