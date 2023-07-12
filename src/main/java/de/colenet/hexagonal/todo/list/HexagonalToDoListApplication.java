package de.colenet.hexagonal.todo.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
class HexagonalToDoListApplication {

    public static void main(String[] args) {
        SpringApplication.run(HexagonalToDoListApplication.class, args);
    }
}
