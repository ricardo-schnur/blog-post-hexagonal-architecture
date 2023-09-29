package de.colenet.hexagonal.todo.list.adapter.rest.model;

public record TaskDto(String id, String description, String state, String completionTime) {}
