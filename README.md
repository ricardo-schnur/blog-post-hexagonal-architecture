# Hexagonal To-Do List application

This is a demo app that will be built as an example accompanying a series of [blog posts][Blog] on how to implement a
hexagonal architecture in a typical Java/Spring application.

## Features (TO BE IMPLEMENTED!)

The application exposes the following basic features of a To-Do List:

- List all tasks
- Create a new task (consisting of a description and an optional due date)
- Toggle completion state of an existing task

Additionally, it regularly sends reminders (which is a fancy way of saying, that it logs the tasks as warnings to the
console) for due tasks.

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
