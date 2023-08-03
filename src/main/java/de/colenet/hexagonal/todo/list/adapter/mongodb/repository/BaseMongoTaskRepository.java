package de.colenet.hexagonal.todo.list.adapter.mongodb.repository;

import de.colenet.hexagonal.todo.list.adapter.mongodb.entity.TaskEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseMongoTaskRepository extends MongoRepository<TaskEntity, String> {
    List<TaskEntity> findByDueDateIsBeforeOrDueDateEquals(LocalDate beforeDate, LocalDate equalsDate);

    default List<TaskEntity> findByDueDateIsBeforeOrDueDateEquals(LocalDate date) {
        return findByDueDateIsBeforeOrDueDateEquals(date, date);
    }
}
