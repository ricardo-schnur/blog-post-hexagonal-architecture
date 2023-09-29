# Hexagonal To-Do List application

This is a demo app that is being built as an example accompanying a series of [blog posts][Blog] on how to implement a
hexagonal architecture in a typical Java/Spring application.

## Features

The application exposes the following basic features of a To-Do List:

- List all tasks
- Create a new task (consisting of a description and an optional due date)
- Toggle completion state of an existing task

(To be implemented:) Additionally, it regularly sends reminders (which is a fancy way of saying, that it logs the tasks
as warnings to the console) for due tasks.

## Usage

The application offers an API that exposes an endpoint for each of the aforementioned features:

- `GET http://localhost:8080/tasks` returns all existing tasks
- `POST http://localhost:8080/tasks` creates a new task, expects the following parameters:
    - `description` (mandatory): The display name of the task
    - Example: `POST http://localhost:8080/tasks?description=Some%20Task`
- `POST http://localhost:8080/tasks/toggle-completion/{id}` toggles the completion state of the task with the specified
  id

## Build and ruin

As this is a standard Spring Boot Maven application, you can build it with the command

```
mvn -B package --file pom.xml
```

and run it via:

```
mvn spring-boot:run
```

[//]: # (TODO: Add links to blog posts when they are published)

[Blog]: https://www.colenet.de/blog/
