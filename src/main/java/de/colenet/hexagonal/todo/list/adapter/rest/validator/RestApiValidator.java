package de.colenet.hexagonal.todo.list.adapter.rest.validator;

import io.vavr.control.Try;
import io.vavr.control.Validation;
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

    public Validation<String, String> validateDescription(String description) {
        if (!StringUtils.hasText(description)) {
            return Validation.invalid("Description is mandatory");
        }

        return Validation.valid(description);
    }
}
