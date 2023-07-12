package de.colenet.hexagonal.todo.list.adapter.rest.validator;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RestApiValidator {

    public Validation<String, UUID> validateId(String id) {
        if (!StringUtils.hasText(id)) {
            return Validation.invalid("ID is mandatory");
        }

        return Try.of(() -> UUID.fromString(id)).toValidation(__ -> "Not a valid UUID: " + id);
    }

    public Validation<Seq<String>, Tuple2<String, Optional<LocalDate>>> validateCreateTaskParameters(
        String description,
        String dueDate
    ) {
        return Validation.combine(validateDescription(description), validateDueDate(dueDate)).ap(Tuple::of);
    }

    private static Validation<String, String> validateDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return Validation.invalid("Description is mandatory");
        }

        return Validation.valid(description);
    }

    private static Validation<String, Optional<LocalDate>> validateDueDate(String dueDate) {
        if (!StringUtils.hasText(dueDate)) {
            return Validation.valid(Optional.empty());
        }

        return Try
            .success(dueDate)
            .mapTry(LocalDate::parse)
            .map(Optional::of)
            .toValidation(__ -> "Due date has to be in format yyyy-MM-dd: " + dueDate);
    }
}
