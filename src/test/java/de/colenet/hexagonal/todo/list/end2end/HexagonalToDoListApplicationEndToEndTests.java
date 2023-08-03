package de.colenet.hexagonal.todo.list.end2end;

import static org.assertj.core.api.Assertions.assertThat;

import de.colenet.hexagonal.todo.list.adapter.rest.model.TaskDto;
import de.colenet.hexagonal.todo.list.util.mongo.MongoExtension;
import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

class HexagonalToDoListApplicationEndToEndTests {

    @Nested
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "storage.type=cache")
    class CacheMode {

        @Autowired
        private TestRestTemplate restTemplate;

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(TestCases.class)
        void runAllTests(Consumer<TestRestTemplate> test, String description) {
            test.accept(restTemplate);
        }
    }

    @Nested
    @ExtendWith(MongoExtension.class)
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "storage.type=database")
    class DatabaseMode {

        @Autowired
        private TestRestTemplate restTemplate;

        @ParameterizedTest(name = "{1}")
        @ArgumentsSource(TestCases.class)
        void runAllTests(Consumer<TestRestTemplate> test, String description) {
            test.accept(restTemplate);
        }
    }

    private static class TestCases implements ArgumentsProvider {

        private TestCases() {}

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream
                .<Tuple2<Consumer<TestRestTemplate>, String>>of(
                    Tuple.of(
                        TestCases::returnsEmptyListIfNoTasksHaveBeenCreated,
                        "returnsEmptyListIfNoTasksHaveBeenCreated"
                    ),
                    Tuple.of(
                        TestCases::creatingTasksIsPossibleAndCreatedTasksAreReturnedFromGetAll,
                        "creatingTasksIsPossibleAndCreatedTasksAreReturnedFromGetAll"
                    ),
                    Tuple.of(
                        TestCases::completionStateOfTasksCanBeToggledAndTogglingOnlyChangesStateAndCompletionTime,
                        "completionStateOfTasksCanBeToggledAndTogglingOnlyChangesStateAndCompletionTime"
                    )
                )
                .map(tuple -> tuple.apply(Arguments::of));
        }

        private static void returnsEmptyListIfNoTasksHaveBeenCreated(TestRestTemplate restTemplate) {
            var result = getAllTasks(restTemplate);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEmpty();
        }

        private static void creatingTasksIsPossibleAndCreatedTasksAreReturnedFromGetAll(TestRestTemplate restTemplate) {
            List<Tuple2<String, String>> descriptionsAndDueDates = List.of(
                Tuple.of("Some description", "2023-07-11"),
                Tuple.of("Some other description", null),
                Tuple.of("Task", "2023-07-12"),
                Tuple.of("Task with same due date", "2023-07-12")
            );

            descriptionsAndDueDates.forEach(Function3.of(TestCases::createTask).apply(restTemplate).tupled()::apply);

            var result = getAllTasks(restTemplate);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrder(
                    new TaskDto(null, "Some description", "2023-07-11", "open", null),
                    new TaskDto(null, "Some other description", null, "open", null),
                    new TaskDto(null, "Task", "2023-07-12", "open", null),
                    new TaskDto(null, "Task with same due date", "2023-07-12", "open", null)
                );
            assertThat(result.getBody()).extracting(TaskDto::id).extracting(UUID::fromString).doesNotContainNull();
        }

        private static void completionStateOfTasksCanBeToggledAndTogglingOnlyChangesStateAndCompletionTime(
            TestRestTemplate restTemplate
        ) {
            var createdTask = createTask(restTemplate, "Some description", "2023-07-11").getBody();
            assertThat(createdTask.state()).isEqualTo("open");

            var id = createdTask.id();

            var toggledTask = toggleTask(restTemplate, id).getBody();
            assertThat(toggledTask)
                .usingRecursiveComparison()
                .ignoringFields("state", "completionTime")
                .isEqualTo(createdTask);
            assertThat(toggledTask.state()).isEqualTo("completed");
            assertThat(toggledTask.completionTime()).isNotNull();

            var toggledTwiceTask = toggleTask(restTemplate, id).getBody();
            assertThat(toggledTwiceTask).isEqualTo(createdTask);

            var toggledThriceTask = toggleTask(restTemplate, id).getBody();
            assertThat(toggledThriceTask)
                .usingRecursiveComparison()
                .ignoringFields("completionTime")
                .isEqualTo(toggledTask);
            assertThat(LocalDateTime.parse(toggledThriceTask.completionTime()))
                .isAfter(LocalDateTime.parse(toggledTask.completionTime()));
        }

        private static ResponseEntity<List<TaskDto>> getAllTasks(TestRestTemplate restTemplate) {
            return restTemplate.exchange("/tasks", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        }

        private static ResponseEntity<TaskDto> createTask(
            TestRestTemplate restTemplate,
            String description,
            String dueDate
        ) {
            return restTemplate.postForEntity(
                UriComponentsBuilder
                    .fromPath("/tasks")
                    .queryParam("description", description)
                    .queryParam("dueDate", dueDate)
                    .build()
                    .toUri(),
                null,
                TaskDto.class
            );
        }

        private static ResponseEntity<TaskDto> toggleTask(TestRestTemplate restTemplate, String id) {
            return restTemplate.postForEntity("/tasks/toggle-completion/{id}", null, TaskDto.class, Map.of("id", id));
        }
    }
}
