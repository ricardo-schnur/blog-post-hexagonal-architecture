package de.colenet.hexagonal.todo.list.adapter.mongodb.repository;

import de.colenet.hexagonal.todo.list.adapter.mongodb.mapper.MongoMapper;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepository;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskRepositoryTestProvider;
import de.colenet.hexagonal.todo.list.util.mongo.MongoExtension;
import java.util.function.Consumer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@ExtendWith(MongoExtension.class)
@DataMongoTest(
    includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { MongoMapper.class, MongoTaskRepository.class }
    )
)
class MongoTaskRepositoryIntegrationTest {

    @Autowired
    private MongoTaskRepository mongoTaskRepository;

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(TaskRepositoryTestProvider.class)
    void runAllTestsFromTaskRepositoryTestProvider(Consumer<TaskRepository> test, String description) {
        test.accept(mongoTaskRepository);
    }
}
