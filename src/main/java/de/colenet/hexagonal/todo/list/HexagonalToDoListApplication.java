package de.colenet.hexagonal.todo.list;

import static de.colenet.hexagonal.todo.list.HexagonalToDoListApplication.ADAPTER_PACKAGE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ADAPTER_PACKAGE + ".cache.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ADAPTER_PACKAGE + ".mongodb.*"),
    }
)
@EnableScheduling
class HexagonalToDoListApplication {

    static final String ADAPTER_PACKAGE = "de.colenet.hexagonal.todo.list.adapter";

    public static void main(String[] args) {
        SpringApplication.run(HexagonalToDoListApplication.class, args);
    }

    @Configuration
    @ComponentScan(ADAPTER_PACKAGE + ".cache")
    @ConditionalOnProperty(name = "storage.type", havingValue = "cache")
    @EnableAutoConfiguration(
        exclude = {
            MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class,
        }
    )
    class CacheConfiguration {}

    @Configuration
    @ComponentScan(ADAPTER_PACKAGE + ".mongodb")
    @ConditionalOnProperty(name = "storage.type", havingValue = "database")
    class DatabaseConfiguration {}
}
