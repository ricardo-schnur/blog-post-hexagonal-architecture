package de.colenet.hexagonal.todo.list.util.mongo;

import de.colenet.hexagonal.todo.list.adapter.mongodb.repository.BaseMongoTaskRepository;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public class MongoExtension implements BeforeAllCallback, AfterEachCallback {

    // This is the port MongoDB runs on inside the Docker container
    private static final int PORT = 27017;

    // Use the same version as in the Docker Compose file
    private static final String CONTAINER_VERSION = "6.0.10";

    @Container
    private static final MongoDBContainer container = new MongoDBContainer("mongo:" + CONTAINER_VERSION)
        .withExposedPorts(PORT);

    @Override
    public void beforeAll(ExtensionContext context) {
        // Start the container and configure the test application to use it
        container.start();
        System.setProperty("testcontainers.mongodb.port", container.getMappedPort(PORT).toString());
        System.setProperty("storage.type", "database");
        // Make sure there is no leftover content from previous container starts
        emptyDatabase(context);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        emptyDatabase(context);
    }

    private static void emptyDatabase(ExtensionContext context) {
        SpringExtension.getApplicationContext(context).getBean(BaseMongoTaskRepository.class).deleteAll();
    }
}
