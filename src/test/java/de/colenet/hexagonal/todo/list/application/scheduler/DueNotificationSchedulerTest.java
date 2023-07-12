package de.colenet.hexagonal.todo.list.application.scheduler;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.colenet.hexagonal.todo.list.domain.model.task.Task.OpenTask;
import de.colenet.hexagonal.todo.list.domain.model.task.TaskTestdataFactory;
import de.colenet.hexagonal.todo.list.domain.service.task.TaskService;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DueNotificationSchedulerTest {

    private static final LocalDate CURRENT_DATE = LocalDate.of(2023, 7, 1);

    private final Clock clock = Clock.fixed(CURRENT_DATE.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

    @Mock
    private DueNotificationSender dueNotificationSender;

    @Mock
    private TaskService taskService;

    private DueNotificationScheduler dueNotificationScheduler;

    @BeforeEach
    void setup() {
        dueNotificationScheduler = new DueNotificationScheduler(clock, dueNotificationSender, taskService);
    }

    @Test
    void sendDueNotifications_RetrievesDueTasksFromScheduler_CallsSenderWithEachTask() {
        List<OpenTask> tasksFromRepository = Stream.generate(TaskTestdataFactory::createOpenTask).limit(3L).toList();

        when(taskService.getAllOpenTasksWithDueDateBeforeOrEqual(CURRENT_DATE)).thenReturn(tasksFromRepository);

        dueNotificationScheduler.sendDueNotifications();

        for (OpenTask task : tasksFromRepository) {
            verify(dueNotificationSender).sendDueNotification(task);
        }
    }
}
